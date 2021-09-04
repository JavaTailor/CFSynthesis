import JimpleMixer.blocks.BlockInfo;
import JimpleMixer.blocks.BlocksContainer;
import JimpleMixer.core.Configuration;
import JimpleMixer.core.JMUtils;
import JimpleMixer.core.MutationHelper;
import core.ClassInfo;
import core.MainConfiguration;
import core.MainHelper;
import dtjvms.*;
import dtjvms.executor.CFM.CFMExecutor;
import dtjvms.loader.DTLoader;
import org.apache.commons.cli.CommandLine;
import soot.*;
import soot.options.Options;

import java.util.*;

public class Main {

    public static String projectName = "HotspotTests-Java";
    public static String mutationProviderProject = "HotspotTests-Java";
    public static String defineClassesPath = "testcases.txt";

    public static int randomSeed = 1;
    public static String timeStamp;
    public static List<ClassInfo> seeds;
    public static boolean crossProject = false;

    public static void main(String[] args) {

        CommandLine options = MainHelper.parseArgs(args);
        if (options != null){
            if (options.hasOption("t")) {
                timeStamp = options.getOptionValue("t");
            } else {
                timeStamp = String.valueOf(new Date().getTime());
            }
            if (options.hasOption("p")) {

                projectName = options.getOptionValue("p");
            }
            if (options.hasOption("f")) {
                defineClassesPath = options.getOptionValue("f");
            }
            if (options.hasOption("i")) {
                randomSeed = Integer.parseInt(options.getOptionValue("i"));
            }
        }
        DTGlobal.setDiffLogger(timeStamp + DTPlatform.FILE_SEPARATOR + projectName, "difference");

        DTConfiguration.debug = false;
        DTConfiguration.setJvmDepensRoot("." + DTPlatform.FILE_SEPARATOR + "01JVMS");
        DTConfiguration.setProjectDepensRoot("." + DTPlatform.FILE_SEPARATOR + "sootOutput");

        System.out.println(DTPlatform.getInstance());
        ArrayList<JvmInfo> jvmCmds = DTLoader.getInstance().loadJvms();
        for (JvmInfo jvmCmd : jvmCmds) {
            System.out.println(jvmCmd);
        }

        ProjectInfo originProject = null;
        ProjectInfo targetProject = null;
        ProjectInfo originMutationProject = null;
        ProjectInfo mutationProject = null;

        String mutationHistoryPath = MainConfiguration.mutationHistoryPath +
                DTPlatform.FILE_SEPARATOR + timeStamp +
                DTPlatform.FILE_SEPARATOR + "classhistory";

        String diffClassPath = MainConfiguration.mutationHistoryPath +
                DTPlatform.FILE_SEPARATOR + timeStamp +
                DTPlatform.FILE_SEPARATOR + "diffClass";

        MainHelper.createFolderIfNotExist(mutationHistoryPath);
        MainHelper.createFolderIfNotExist(diffClassPath);

        if ( projectName == null || projectName.equals(mutationProviderProject) ){

            crossProject = false;
            originProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("02Benchmarks", "HotspotTests-Java", null, true);
            targetProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("sootOutput", "HotspotTests-Java", null, true);

            System.out.println(originProject);
            System.out.println(targetProject);

            Configuration.initSootEnvWithClassPath(targetProject.getpClassPath());
            Configuration.set_output_path(targetProject.getSrcClassPath());

            List<String> seedClasses = originProject.getApplicationClasses();
            MainHelper.restoreBadClasses(seedClasses, originProject, targetProject);
            targetProject.setApplicationClasses(new ArrayList<>(seedClasses));
            seeds = MainHelper.initialSeeds(seedClasses, targetProject.getSrcClassPath());

            List<String> mutationClasses = MainHelper.duplicateSeedsAndChangeModifiers(seedClasses);

            G.reset();
            Configuration.initSootEnvWithClassPath(targetProject.getpClassPath());
            Configuration.set_output_path(targetProject.getSrcClassPath());

            BlocksContainer.initMutantsFromClasses(mutationClasses);
        } else {

            crossProject = true;
            originProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("02Benchmarks", projectName, null, true);
            targetProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("sootOutput", projectName, null, true);

            originMutationProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("02Benchmarks", mutationProviderProject, null, true);
            mutationProject = DTLoader.getInstance().loadTargetProjectWithGivenPath("sootOutput", mutationProviderProject, null, true);

            System.out.println(originProject);
            System.out.println(targetProject);
            System.out.println(mutationProject);

            Configuration.initSootEnvWithClassPath(mutationProject.getpClassPath());
            Configuration.set_output_path(mutationProject.getSrcClassPath());

            List<String> originMutateClasses = originMutationProject.getApplicationClasses();
            MainHelper.restoreBadClasses(originMutateClasses, originMutationProject, mutationProject);
            mutationProject.setApplicationClasses(new ArrayList<>(originMutateClasses));

            List<String> mutationClasses = MainHelper.duplicateSeedsAndChangeModifiers(originMutateClasses);

            G.reset();

            Configuration.initSootEnvWithClassPath(targetProject.getpClassPath() + System.getProperty("path.separator") + mutationProject.getpClassPath());
            Configuration.set_output_path(targetProject.getSrcClassPath());

            List<String> seedClasses = originProject.getApplicationClasses();
            MainHelper.restoreBadClasses(seedClasses, originProject, targetProject);
            targetProject.setApplicationClasses(new ArrayList<>(seedClasses));

            List<String> seedJunits = originProject.getJunitClasses();
            MainHelper.restoreBadClasses(seedJunits, originProject, targetProject);
            targetProject.setJunitClasses(new ArrayList<>(seedJunits));

            seeds = MainHelper.initialSeedsWithType(seedClasses, targetProject.getSrcClassPath(), false, mutationHistoryPath);
            seeds.addAll(MainHelper.initialSeedsWithType(seedJunits, targetProject.getTestClassPath(), true, mutationHistoryPath));

            BlocksContainer.initMutantsFromClasses(mutationClasses);
        }

        Random random = new Random(randomSeed);

        while (seeds.size() > 0){

            ClassInfo seed = seeds.get(random.nextInt(seeds.size()));

            if (seed.isJunit()){
                Configuration.set_output_path(targetProject.getTestClassPath());
            } else {
                Configuration.set_output_path(targetProject.getSrcClassPath());
            }

            if (seed.getMutationTimes() > 10){
                seeds.remove(seed);
                continue;
            }

            System.out.println("current: " + seed.getClassName());

            String classFileFolder = mutationHistoryPath + DTPlatform.FILE_SEPARATOR + seed.getOriginClassName();
            MainHelper.createFolderIfNotExist(classFileFolder);
            SootClass seedClass;
            if (seed.isOriginClass() && !seed.hasCovered()){

                seedClass = JMUtils.loadTargetClass(seed.getOriginClassName());
            } else {

                seed.storeToCoverOriginClass();
                seedClass = JMUtils.loadTargetClass(seed.getOriginClassName());
            }

            if (seedClass == null){
                continue;
            }

            Map<String, Body> candidateMethods = new HashMap<>();
            List<SootMethod> seedMethods = seedClass.getMethods();

            Iterator<SootMethod> methodIterator = seedMethods.iterator();
            while (methodIterator.hasNext()){
                SootMethod seedMethod = methodIterator.next();
                if (!seedMethod.isAbstract()){
                    try {
                        Body methodBody = seedMethod.retrieveActiveBody();
                        if (!seedMethod.isConstructor()) {
                            candidateMethods.put(seedMethod.getName(), methodBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            List<String> tmp = new ArrayList<>(candidateMethods.keySet());
            if (tmp.size() <= 0){

                seeds.remove(seed);
                Scene.v().removeClass(seedClass);
                continue;
            }
            Body methodBody = candidateMethods.get(tmp.get(random.nextInt(tmp.size())));

            if (methodBody != null){

                List<Unit> validUnits = MainHelper.getValidStmtsFromMethodBody(methodBody);
                Unit targetUnit = MainHelper.getTargetUnitRandom(random, validUnits);

                //random get mutant
                BlockInfo ingredient = MainHelper.getIngredientRandom(random, BlocksContainer.getValidMutationMap());
                int tryTimes = 0;
                while (ingredient == null && tryTimes < 10){
                    ingredient = MainHelper.getIngredientRandom(random, BlocksContainer.getValidMutationMap());
                    tryTimes++;
                }
                if (ingredient != null){
                    String mutantClass = ingredient.getClassName();
                    MutationHelper.insertBlockToMethod(seedClass, methodBody, targetUnit, BlocksContainer.getAllMutationMap().get(mutantClass), ingredient);
                }
            }

            if (JMUtils.saveSootClassToLocal(seedClass, Options.output_format_class)) {

                seed.mutationTimesIncrease();
                ClassInfo newMutateClass = new ClassInfo(seed.getOriginClassName(),
                        seed.getOriginClassPath(),
                        seed.generateMutateClassFilename(),
                        classFileFolder + DTPlatform.FILE_SEPARATOR + seed.generateMutateClassFilename(),
                        seed.isJunit(),
                        seed.getMutationOrder() + 1,
                        0);
                newMutateClass.saveSootClassToFile(seedClass);
                if (crossProject){
                    if (!CFMExecutor.getInstance().dtSingleClassInProj(jvmCmds,
                            targetProject,
                            mutationProject,
                            newMutateClass.getOriginClassName(),
                            newMutateClass.getClassName())){
                        seeds.add(newMutateClass);
                    }
                } else {
                    if (!CFMExecutor.getInstance().dtSingleClassInProj(jvmCmds,
                            targetProject,
                            newMutateClass.getOriginClassName(),
                            newMutateClass.getClassName())){
                        seeds.add(newMutateClass);
                    }
                }

                if (CFMExecutor.getInstance().isDiffFound()){

                    String diffClassFolder = diffClassPath + DTPlatform.FILE_SEPARATOR + seed.getOriginClassName();
                    MainHelper.createFolderIfNotExist(diffClassFolder);
                    newMutateClass.saveSootClassToTargetPath(seedClass, diffClassFolder + DTPlatform.FILE_SEPARATOR + newMutateClass.getClassName());
                }
            }
            Scene.v().removeClass(seedClass);
        }
    }
}
