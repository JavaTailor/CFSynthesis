package dtjvms.loader;

import dtjvms.DTConfiguration;
import dtjvms.DTPlatform;
import dtjvms.JvmInfo;
import dtjvms.ProjectInfo;
import org.apache.commons.lang3.StringUtils;
import util.RuntimeConfigParser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class LoaderHelper {

    /**
     * search java cmd from a given path
     * @param jvmPath   DTConfiguration.JVM_DEPENS_ROOT + / + OSTYPE + openjdkVersion
     * @param javaCmdPath   different java path under OS, e.g. "Contents/Home/bin/java" in macOS, "bin/java" in linux
     * @param openjdkVersion openjdkVersion
     * @return
     */
    public static ArrayList<JvmInfo> searchJavaCmds(String jvmPath, String javaCmdPath, String openjdkVersion){

        ArrayList<JvmInfo> cmdArray = new ArrayList<>();
        File macFile = new File(jvmPath);
        if (macFile.exists()){
            for (File file : macFile.listFiles()) {

                if (file.isDirectory()){

                    String jvmdir = file.getName();
                    String jvmName = inferJvmNameByDirectory(jvmdir);
                    if (shouldTestJVM(jvmName.toLowerCase())){

                        File tmp = new File(file.getAbsoluteFile() + DTPlatform.FILE_SEPARATOR + javaCmdPath);
                        if (tmp.exists()){
                            JvmInfo jvmInfo = new JvmInfo(jvmPath, jvmdir, jvmName, openjdkVersion, tmp.getAbsolutePath());
                            cmdArray.add(jvmInfo);
                        }
                    }
                }
            }
        }else {
            System.err.println("Directory: " + jvmPath + " not exists!");
//            throw new RuntimeException("Directory: " + jvmPath + " not exists!");
        }
        return cmdArray;
    }

    /**
     * analysis target project elements
     * @param projectFile project file
     * @return ProjectInfo object
     */
    public static ProjectInfo analysisProject(File projectFile){

        String projectName = projectFile.getName();
        String projectRootPath = projectFile.getAbsolutePath();
        ProjectInfo currentProject = new ProjectInfo(projectName, projectRootPath);

        //TODO: analysis runtime config
        String runtimeConfigPath = projectRootPath + DTPlatform.FILE_SEPARATOR + DTConfiguration.PROJECT_RUNTIME_CONFIG;
        File rcFile = new File(runtimeConfigPath);
        if (rcFile.exists()){
//            System.out.println(RuntimeConfigParser.parse(rcFile));
            currentProject.setProjoptions(RuntimeConfigParser.parse(rcFile));
        } else {
            currentProject.setProjoptions(new ArrayList<>());
        }

        File[] contentFiles = projectFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                //replace with default.properties
                return pathname.isDirectory() && DTConfiguration.PROJECTS_ELEMENTS.contains(pathname.getName());
            }
        });
        if (contentFiles.length > 0) {
            for (File contentFile : contentFiles) {
                switch (contentFile.getName()) {
                    case "lib":
                        currentProject.setLibPath(contentFile.getAbsolutePath());
                        currentProject.setLibsString(parseLibString(contentFile));
                        break;
                    case "out":
                        String oSrcClassPath = contentFile.getAbsolutePath() + DTPlatform.FILE_SEPARATOR
                                + "production" + DTPlatform.FILE_SEPARATOR + currentProject.getProjectName();
                        File oClassFile = new File(oSrcClassPath);
                        if (oClassFile.exists()) {
                            currentProject.setSrcClassPath(oSrcClassPath);
                            currentProject.setTotalSrcClassSize(countFileSizeWithSuffix(oClassFile, ".class"));
                        }
                        String oTestClassPath = contentFile.getAbsolutePath() + DTPlatform.FILE_SEPARATOR
                                + "test" + DTPlatform.FILE_SEPARATOR + currentProject.getProjectName();
                        File oTestFile = new File(oTestClassPath);
                        if (oTestFile.exists()) {
                            currentProject.setTestClassPath(oTestClassPath);
                            currentProject.setTotalTestClassSize(countFileSizeWithSuffix(oTestFile, ".class"));
                        }
                        break;
                    case "target":
                        //TODO target analysis
                        String tSrcClassPath = contentFile.getAbsolutePath() + DTPlatform.FILE_SEPARATOR
                                + "classes";
                        File tClassFile = new File(tSrcClassPath);
                        if (tClassFile.exists()) {
                            currentProject.setSrcClassPath(tSrcClassPath);
                            currentProject.setTotalSrcClassSize(countFileSizeWithSuffix(tClassFile, ".class"));
                        }
                        String tTestClassPath = contentFile.getAbsolutePath() + DTPlatform.FILE_SEPARATOR
                                + "test-classes";
                        File tTestFile = new File(tTestClassPath);
                        if (tTestFile.exists()) {
                            currentProject.setTestClassPath(tTestClassPath);
                            currentProject.setTotalTestClassSize(countFileSizeWithSuffix(tTestFile, ".class"));
                        }
                        break;
                    case "src":
                        currentProject.setSrcJavaPath(contentFile.getAbsolutePath());
                        currentProject.setTotalSrcJavaSize(countFileSizeWithSuffix(contentFile, ".java"));
                        break;
                    case "test":
                        currentProject.setTestJavaPath(contentFile.getAbsolutePath());
                        currentProject.setTotalTestJavaSize(countFileSizeWithSuffix(contentFile, ".java"));

                        break;
                    case "java":
                        if (currentProject.getSrcJavaPath() == null){
                            currentProject.setSrcJavaPath(contentFile.getAbsolutePath());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (currentProject.getSrcClassPath() == null &&  currentProject.getTestClassPath() == null){

            long classNumber = countFileSizeWithSuffix(projectFile, ".class");
            if (classNumber > 0){
                currentProject.setSrcClassPath(projectRootPath);
                currentProject.setTotalSrcClassSize(classNumber);
                currentProject.setTotalTestClassSize(classNumber);
            } else {
                System.err.println("Project " + projectName + " seems to be an empty project! Please check the project root path: " + projectRootPath);
            }
        }
        return currentProject;
    }

    public static ArrayList<String> getAllDirs(String path){

        ArrayList<String> dirNames = new ArrayList<>();
        File file = new File(path);
        if (file.exists()){

            for (File listFile : file.listFiles()) {

                if (listFile.isDirectory()){

                    dirNames.add(listFile.getName());
                }
            }
        }
        return dirNames;
    }

    public static JvmInfo getBootJvm(ArrayList<JvmInfo> jvms){

        for (JvmInfo jvm : jvms) {

            if (jvm.getJvmName().equalsIgnoreCase("hotspot")){
                return jvm;
            }
        }
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return jvms.get(random.nextInt(jvms.size()));
    }

    public static long countFileSizeWithSuffix(File currentPath, String suffix){

        long fileCounter = 0;
        Stack<File> stack = new Stack<>();
        stack.push(currentPath);

        while (!stack.isEmpty()) {
            File path = stack.pop();
            File[] classFiles = path.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(suffix);
                }
            });
            if (classFiles == null) {
                break;
            }
            for (File subFile : classFiles) {
                if (subFile.isDirectory()) {
                    stack.push(subFile);
                } else {
                    fileCounter++;
                }
            }
        }
        return fileCounter;
    }

    public static boolean shouldTestJVM(String jvmname){

        if (jvmname == null){
            return false;
        }
        if (DTConfiguration.TARGET_JVMS.contains(jvmname)){
            return true;
        }
        if (DTConfiguration.FILTER_JVMS.contains(jvmname)){
            return false;
        }
        return true;
    }

    public static boolean shouldTestProject(String projectName){

        if (projectName == null){
            return false;
        }
        if (DTConfiguration.TARGET_PROJECTS.contains(projectName)){
            return true;
        }
        if (DTConfiguration.FILTER_PROJECTS.contains(projectName)){
            return false;
        }
        return true;
    }

    public static String inferJvmNameByDirectory(String folderName){

        if (folderName.toLowerCase().contains("openj9")){
            return "OpenJ9";
        }else if (folderName.toLowerCase().contains("hotspot")){
            return "Hotspot";
        }else if (folderName.toLowerCase().contains("zulu")){
            return "Zulu";
        } else if (folderName.toLowerCase().contains("graalvm")) {
            return "Graalvm";
        } else if (folderName.toLowerCase().contains("dragonwell")) {
            return "Dragonwell";
        } else{
            return "UNKNOWN";
        }
    }

    public static String parseLibString(File libFile){

        File[] libs = libFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(DTPlatform.JAR_SUFFIX);
            }
        });
        if (libs.length > 0){

            String[] libsPath = new String[libs.length];
            for (int i = 0; i < libs.length; i++){
                libsPath[i] = libs[i].getAbsolutePath();
            }
            return StringUtils.join(Arrays.asList(libsPath), DTPlatform.PATH_SEPARATOR);
        }else{
            return null;
        }
    }
}
