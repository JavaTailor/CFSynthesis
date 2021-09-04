package dtjvms.loader;

import dtjvms.DTConfiguration;
import dtjvms.DTPlatform;
import dtjvms.JvmInfo;
import dtjvms.ProjectInfo;
import dtjvms.executor.DTExecutor;
import dtjvms.executor.ExecutorHelper;
import dtjvms.executor.RunnalbeClassExecutor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Loader {

    protected ArrayList<JvmInfo> JVMCMDS;
    protected ArrayList<ProjectInfo> Projects;

    /**
     * load java cmd of different JVM versions according to the OS type
     * @return
     */
    public ArrayList<JvmInfo> loadJvms(){

        if (JVMCMDS != null){
            return JVMCMDS;
        }
        JVMCMDS = new ArrayList<>();

        if (DTPlatform.isMac()){

            DTConfiguration.JVM_DEPENS_ROOT = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + "macOSx64";
            if (DTConfiguration.OPENJDK_VERSIONS.size() > 0){

                for (String openjdkVersion : DTConfiguration.OPENJDK_VERSIONS) {
                    String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + openjdkVersion;
                    JVMCMDS.addAll(LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.MACOS_JAVA_PATH, openjdkVersion));
                }
            } else {

                //load all openjdks
                ArrayList<String> dirs = LoaderHelper.getAllDirs(DTConfiguration.JVM_DEPENS_ROOT);
                if (dirs.size() > 0){

                    for (String dir : dirs) {

                        String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + dir;
                        ArrayList<JvmInfo> tmp = LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.MACOS_JAVA_PATH, dir);
                        if (tmp.size() > 0){
                            JVMCMDS.addAll(tmp);
                        }
                    }
                }else {

                    throw new RuntimeException("There is no java cmd available!");
                }
            }

        }else if (DTPlatform.isLinux()){

            //TODO linux depens path
//            JVM_DEPENS_ROOT = JVM_DEPENS_ROOT + TestPlatform.FILE_SEPARATOR + "linux64";
//            JVMCMDS = searchJavaCmds(JVM_DEPENS_ROOT, TestPlatform.LINUX_JAVA_PATH);
            DTConfiguration.JVM_DEPENS_ROOT = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + "linux64";
            if (DTConfiguration.OPENJDK_VERSIONS.size() > 0){

                for (String openjdkVersion : DTConfiguration.OPENJDK_VERSIONS) {
                    String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + openjdkVersion;
                    JVMCMDS.addAll(LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.LINUX_JAVA_PATH, openjdkVersion));
                }
            } else {

                //load all openjdks
                ArrayList<String> dirs = LoaderHelper.getAllDirs(DTConfiguration.JVM_DEPENS_ROOT);
                if (dirs.size() > 0){

                    for (String dir : dirs) {

                        String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + dir;
                        ArrayList<JvmInfo> tmp = LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.LINUX_JAVA_PATH, dir);
                        if (tmp.size() > 0){
                            JVMCMDS.addAll(tmp);
                        }
                    }
                }else {

                    throw new RuntimeException("There is no java cmd available!");
                }
            }
        }else {

            //TODO windows depens path
//            JVM_DEPENS_ROOT = JVM_DEPENS_ROOT + TestPlatform.FILE_SEPARATOR + "Windows";
//            JVMCMDS = searchJavaCmds(JVM_DEPENS_ROOT, TestPlatform.WIN_JAVA_PATH);
            DTConfiguration.JVM_DEPENS_ROOT = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + "Windows";
            if (DTConfiguration.OPENJDK_VERSIONS.size() > 0){

                for (String openjdkVersion : DTConfiguration.OPENJDK_VERSIONS) {
                    String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + openjdkVersion;
                    JVMCMDS.addAll(LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.WIN_JAVA_PATH, openjdkVersion));
                }
            } else {

                //load all openjdks
                ArrayList<String> dirs = LoaderHelper.getAllDirs(DTConfiguration.JVM_DEPENS_ROOT);
                if (dirs.size() > 0){

                    for (String dir : dirs) {

                        String jvmPath = DTConfiguration.JVM_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + dir;
                        ArrayList<JvmInfo> tmp = LoaderHelper.searchJavaCmds(jvmPath, DTPlatform.WIN_JAVA_PATH, dir);
                        if (tmp.size() > 0){
                            JVMCMDS.addAll(tmp);
                        }
                    }
                }else {

                    throw new RuntimeException("There is no java cmd available!");
                }
            }
        }
        if (JVMCMDS == null || JVMCMDS.size() <= 0){

            throw new RuntimeException("There is no java cmd available!");
        }
        return JVMCMDS;
    }

    public ArrayList<ProjectInfo> loadProjects() {

        if (Projects == null){
            Projects = new ArrayList<>();
        }
        File pDpends = new File(DTConfiguration.PROJECT_DEPENS_ROOT);
        if (pDpends.exists()){

            for (File projectFile : pDpends.listFiles()) {
                //lib, class
                if (projectFile.isDirectory()) {

                    String projectName = projectFile.getName();
                    if (LoaderHelper.shouldTestProject(projectName)) {

                        ProjectInfo currentProject = LoaderHelper.analysisProject(projectFile);
                        Projects.add(setupRunnableClasses(currentProject, null));
                    }
                }
            }
        }else{
            throw new RuntimeException("Project-depens directory: " + DTConfiguration.PROJECT_DEPENS_ROOT + " not exists!");
        }
        return Projects;
    }

    /**
     * initial test projects form PROJECT_DEPENS_ROOT with boot jvms
     * @param bootJvms
     * @return
     */
    public ArrayList<ProjectInfo> loadProjects(ArrayList<JvmInfo> bootJvms){

        JvmInfo bootJvm = null;
        if (bootJvms != null && bootJvms.size() > 0){
            bootJvm = LoaderHelper.getBootJvm(bootJvms);
        }
        if (Projects == null){
            Projects = new ArrayList<>();
        }
        File pDpends = new File(DTConfiguration.PROJECT_DEPENS_ROOT);
        if (pDpends.exists()){

            for (File projectFile : pDpends.listFiles()) {
                //lib, class
                if (projectFile.isDirectory()) {

                    String projectName = projectFile.getName();
                    if (LoaderHelper.shouldTestProject(projectName)) {

                        ProjectInfo currentProject = LoaderHelper.analysisProject(projectFile);
                        Projects.add(setupRunnableClasses(currentProject, bootJvm));
                    }
                }
            }
        }else{
            throw new RuntimeException("Project-depens directory: " + DTConfiguration.PROJECT_DEPENS_ROOT + " not exists!");
        }
        return Projects;
    }

    public static ProjectInfo setupRunnableClasses(ProjectInfo currentProject, JvmInfo bootJVM){

        String className = DTPlatform.RUNNABLE_CLASS_LOADER;
        String classPath = DTPlatform.getJavaClassPath();
        ArrayList<String> classPathArray = new ArrayList<>();
        classPathArray.add(classPath);
        String libPath = currentProject.getLibPath();

        if (libPath != null){
            libPath = libPath + DTPlatform.FILE_SEPARATOR + "*";
            classPathArray.add(libPath);
        }

        ArrayList<String> junitClasses = new ArrayList<>();
        ArrayList<String> applicationClasses = new ArrayList<>();

        if (currentProject.getSrcClassPath() != null){
            classPathArray.add(currentProject.getSrcClassPath());
        }
        if (currentProject.getTestClassPath() != null){
            classPathArray.add(currentProject.getTestClassPath());
        }
        if (currentProject.getSrcClassPath() == null &&
                currentProject.getTestClassPath() == null &&
                currentProject.getProjectRootPath() != null){
            classPathArray.add(currentProject.getProjectRootPath());
        }

        classPath = StringUtils.join(classPathArray, DTPlatform.PATH_SEPARATOR);

        if (currentProject.getSrcClassPath() != null){

            HashMap<String, ArrayList<String>> classes = getRunnableClasses(bootJVM, currentProject, classPath, className, currentProject.getSrcClassPath());
            junitClasses.addAll(classes.get("Junit"));
            applicationClasses.addAll(classes.get("Application"));
        }

        if (currentProject.getTestClassPath() != null){

            HashMap<String, ArrayList<String>> classes = getRunnableClasses(bootJVM, currentProject, classPath, className, currentProject.getTestClassPath());
            junitClasses.addAll(classes.get("Junit"));
            applicationClasses.addAll(classes.get("Application"));
        }

        if (currentProject.getSrcClassPath() == null &&
                currentProject.getTestClassPath() == null &&
                currentProject.getProjectRootPath() != null){

            HashMap<String, ArrayList<String>> classes = getRunnableClasses(bootJVM, currentProject, classPath, className, currentProject.getProjectRootPath());
            junitClasses.addAll(classes.get("Junit"));
            applicationClasses.addAll(classes.get("Application"));
        }
        currentProject.setJunitClasses(junitClasses);
        currentProject.setApplicationClasses(applicationClasses);
        return currentProject;
    }

    public static HashMap<String, ArrayList<String>> getRunnableClasses(JvmInfo bootJVM, ProjectInfo currentProject, String classPath, String className, String calssDependsPath){

        HashMap<String, ArrayList<String>> classes = new HashMap<>();
        ArrayList<String> junitClasses = new ArrayList<>();
        ArrayList<String> applicationClasses = new ArrayList<>();
        String cmd;
        if (currentProject.getHasPredefinedClass()){

            if (bootJVM != null){
                cmd = ExecutorHelper.assembleJavaCmd(bootJVM.getJavaCmd(),
                        currentProject.getVmoptions(),
                        classPath,
                        className,
                        false,
                        calssDependsPath,
                        currentProject.getProjectName(),
                        "false", //isDebug
                        currentProject.getPredefinedClassPath());
            } else {

                cmd = ExecutorHelper.assembleJavaCmd(DTPlatform.JAVA_CMD,
                        currentProject.getVmoptions(),
                        classPath,
                        className,
                        false,
                        calssDependsPath,
                        currentProject.getProjectName(),
                        "false", //isDebug
                        currentProject.getPredefinedClassPath());
            }

        } else {

            if (bootJVM != null){
                cmd = ExecutorHelper.assembleJavaCmd(bootJVM.getJavaCmd(),
                        currentProject.getVmoptions(),
                        classPath,
                        className,
                        false,
                        calssDependsPath,
                        currentProject.getProjectName());
            } else {
                cmd = ExecutorHelper.assembleJavaCmd(DTPlatform.JAVA_CMD,
                        currentProject.getVmoptions(),
                        classPath,
                        className,
                        false,
                        calssDependsPath,
                        currentProject.getProjectName());
            }
        }

        HashMap<String, ArrayList<String>> runnableClasses = RunnalbeClassExecutor.getInstance().execute(cmd);
        runnableClasses.keySet().forEach(type -> {
            if (type.equals("Junit")){

                junitClasses.addAll(runnableClasses.get(type));
            } else {
                applicationClasses.addAll(runnableClasses.get(type));
            }
        });
        classes.put("Junit", junitClasses);
        classes.put("Application", applicationClasses);
        return classes;
    }

    public ArrayList<JvmInfo> getJVMCMDS() {
        return JVMCMDS;
    }

    public void setJVMCMDS(ArrayList<JvmInfo> JVMCMDS) {
        this.JVMCMDS = JVMCMDS;
    }

    public ArrayList<ProjectInfo> getProjects() {
        return Projects;
    }

    public void setProjects(ArrayList<ProjectInfo> projects) {
        Projects = projects;
    }
}
