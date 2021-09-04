package core;

import JimpleMixer.blocks.BlockInfo;
import JimpleMixer.blocks.BlocksContainer;
import JimpleMixer.core.JMUtils;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import dtjvms.DTPlatform;
import dtjvms.ProjectInfo;
import org.apache.commons.cli.*;
import soot.*;
import soot.jimple.internal.JIdentityStmt;
import soot.tagkit.AbstractHost;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MainHelper {

    public static Options options = null;

    public static void loadOptions(){

        options = new Options();
        options.addOption(new Option("t", "timestamp", true, "time stamp"));
        options.addOption(new Option("p", "project", true, "test project"));
        options.addOption(new Option("f", "file", true, "predefined classes"));
        options.addOption(new Option("i", "mutation", true, "mutation order"));

    }

    public static CommandLine parseArgs(String[] args){

        if (options == null){
            loadOptions();
        }
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cmd == null){

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "CFMutation Commands", options );
            System.out.println();
            return null;
        }
        return cmd;
    }

    public static List<ClassInfo> initialSeedsWithType(List<String> classes, String srcClassPath, boolean isJunit, String bakPath) {

        List<ClassInfo> classInfos = new ArrayList<>();
        classes.forEach(clazz -> {

            String cpath = srcClassPath + DTPlatform.FILE_SEPARATOR +
                    clazz.replace(".", DTPlatform.FILE_SEPARATOR) + ".class";
            if (!bakPath.equals("")){

                String classFileFolder = bakPath + DTPlatform.FILE_SEPARATOR + clazz;
                MainHelper.createFolderIfNotExist(classFileFolder);
                String originClassBakPath = classFileFolder + DTPlatform.FILE_SEPARATOR + clazz + "-origin.class";
                MainHelper.copyToFile(cpath, originClassBakPath);
                classInfos.add(new ClassInfo(clazz, cpath, clazz, originClassBakPath , isJunit, 0, 0));

            } else {
                classInfos.add(new ClassInfo(clazz, cpath, clazz, cpath, isJunit, 0, 0));
            }
        });
        return classInfos;
    }

    public static List<ClassInfo> initialSeeds(List<String> classes, String srcClassPath) {

        List<ClassInfo> classInfos = new ArrayList<>();
        classes.forEach(clazz -> {

            String cpath = srcClassPath + DTPlatform.FILE_SEPARATOR +
                    clazz.replace(".", DTPlatform.FILE_SEPARATOR) + ".class";
            classInfos.add(new ClassInfo(clazz, cpath, clazz, cpath, 0, 0));
        });
        return classInfos;
    }

    public static void createFolderIfNotExist(String folderPath){

        File folder = new File(folderPath);
        if (!folder.exists()){

            folder.mkdirs();
        }
    }

    public static List<Unit> getValidStmtsFromMethodBody(Body methodBody){

        List<Unit> validUnits = new ArrayList<>();
        for (Unit unit : methodBody.getUnits()) {
            if (! (unit instanceof JIdentityStmt)){
                validUnits.add(unit);
            }
        }
        return validUnits;
    }

    public static Unit getTargetUnitRandom(Random random, List<Unit> validUnits){

        Unit targetUnit = null;
        if (validUnits.size() > 0){
            targetUnit = validUnits.get(random.nextInt(validUnits.size()));
        }
        return targetUnit;
    }

    public static String getIngredientClassRandom(Random random, List<String> mutantClasses){

        return mutantClasses.get(random.nextInt(mutantClasses.size()));
    }

    public static BlockInfo getIngredientBlockRandom(Random random, List<BlockInfo> ingredients){

        if (ingredients.size() > 0){
            return ingredients.get(random.nextInt(ingredients.size()));
        }
        return null;
    }

    public static BlockInfo getIngredientRandom(Random random, Map<String, List<BlockInfo>> ingredients){

        Set<String> classCandidate = ingredients.keySet();
        String candidate = (String) classCandidate.toArray()[random.nextInt(classCandidate.size())];
        List<BlockInfo> methodCandidate = ingredients.get(candidate);

        if (methodCandidate.size() > 0){
            return methodCandidate.get(random.nextInt(methodCandidate.size()));
        }
        return null;
    }

    public static List<String> loadBadClasses(boolean isJunit){

        List<String> badClasses = new ArrayList<>();
        String tmpPath = "";
        if (isJunit){
            tmpPath = "./tmp-junit.txt";
        } else {
            tmpPath = "./tmp-application.txt";
        }
        File file = new File(tmpPath);
        if (file.exists()){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.isEmpty()){
                        badClasses.add(line);
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Exception found when reading: " + tmpPath + " !");
            }
        } else {
//            throw new RuntimeException("Defined Classes Path: " + tmpPath + " not Available!");
        }
        return badClasses;
    }

    public static void restoreBadClasses(List<String> badClasses, ProjectInfo originProject, ProjectInfo targetProject){

        String originAppOutPath = originProject.getSrcClassPath();
        String targetAppOutPath = targetProject.getSrcClassPath();
        String originTestOutPath = originProject.getTestClassPath();
        String targetTestOutPath = targetProject.getTestClassPath();

        for (String badClass : badClasses) {

            String cpath = badClass.replace(".", DTPlatform.FILE_SEPARATOR) + ".class";

            if (originProject.getApplicationClasses().contains(badClass)){

                String sourceFilePath = originAppOutPath + DTPlatform.FILE_SEPARATOR + cpath;
                String targetFilePath = targetAppOutPath + DTPlatform.FILE_SEPARATOR + cpath;
                copyToFile(sourceFilePath, targetFilePath);
            }
            if (originProject.getJunitClasses().contains(badClass)){

                String sourceFilePath = originTestOutPath + DTPlatform.FILE_SEPARATOR + cpath;
                String targetFilePath = targetTestOutPath + DTPlatform.FILE_SEPARATOR + cpath;
                copyToFile(sourceFilePath, targetFilePath);
            }
        }
    }

    public static void copyToFile(String sourceFilePath, String targetFilePath){

        try {
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.exists()){
                File targetFile = new File(targetFilePath);
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> duplicateSeedsAndChangeModifiers(List<String> classes) {

        List<String> seeds = new ArrayList<>();
        for (String aClass : classes) {

            SootClass sclass = JMUtils.loadTargetClass(aClass);
            if (!isPublic(sclass)){
                sclass.setModifiers(sclass.getModifiers() | Modifier.PUBLIC);
            }
            sclass.getFields().forEach(sootField -> {

                if (!isPublic(sootField)){
                    sootField.setModifiers(sootField.getModifiers() | Modifier.PUBLIC);
                }
            });
            sclass.getMethods().forEach(sootMethod -> {
                sootMethod.retrieveActiveBody();
                if (!isPublic(sootMethod) && !sootMethod.isStaticInitializer() && !sootMethod.isConstructor()){
                    sootMethod.setModifiers(sootMethod.getModifiers() | Modifier.PUBLIC);
                }
            });
            String newClassName = aClass + "_mutator";
            sclass.setName(newClassName);
            if (JMUtils.saveSootClassToLocal(sclass, soot.options.Options.output_format_class)) {
                seeds.add(newClassName);
            }
        }
        return seeds;
    }

    public static boolean isPublic(AbstractHost node){

        if (node instanceof SootClass){

            SootClass clazz = (SootClass) node;
            if (clazz.isPrivate() || clazz.isProtected() || clazz.getModifiers() == Modifier.STATIC || clazz.getModifiers() == 0){
                return false;
            }
        }
        if (node instanceof SootField){

            SootField field = (SootField) node;
            if (field.isPrivate() || field.isProtected() || field.getModifiers() == Modifier.STATIC || field.getModifiers() == 0){
                return false;
            }
        }
        if (node instanceof SootMethod){

            SootMethod method = (SootMethod) node;
            if (method.isPrivate() || method.isProtected() || method.getModifiers() == Modifier.STATIC || method.getModifiers() == 0){
                return false;
            }
        }
        return true;
    }
}
