package dtjvms.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import dtjvms.DTConfiguration;
import dtjvms.DTPlatform;
import dtjvms.JvmInfo;
import dtjvms.ProjectInfo;
import dtjvms.executor.DTExecutor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import util.RuntimeConfigParser;

public class DTLoader extends Loader{

    private static DTLoader dtloader;

    public static DTLoader getInstance(){

        if (dtloader == null){
            dtloader = new DTLoader();
        }
        return dtloader;
    }

    /**
     * load java cmd of different JVM versions according to the OS type
     * @return
     */
    @Override
    public ArrayList<JvmInfo> loadJvms() {

        super.loadJvms();
        if (DTConfiguration.SPECIFY_VM_VMOPTIONS.size() > 0){

            for (String specifyVmVmoption : DTConfiguration.SPECIFY_VM_VMOPTIONS) {

                String[] ops = specifyVmVmoption.split("@");
                for (JvmInfo jvmcmd : JVMCMDS) {

                    if (jvmcmd.getJvmName().toLowerCase().equals(ops[0])
                            && jvmcmd.getVersion().toLowerCase().equals(ops[1])){

                        JvmInfo newJvm = new JvmInfo(jvmcmd.getRootPath(),
                                jvmcmd.getFolderName(),
                                jvmcmd.getJvmName() + "_" + ops[2],
                                jvmcmd.getVersion(),
                                jvmcmd.getJavaCmd() + " " + ops[2]);
                        JVMCMDS.add(newJvm);
                        break;
                    }
                }
            }
        }
        return JVMCMDS;
    }

    @Override
    public ArrayList<ProjectInfo> loadProjects() {

        super.loadProjects();
        return Projects;
    }

    @Override
    public ArrayList<ProjectInfo> loadProjects(ArrayList<JvmInfo> bootJvms) {

        super.loadProjects(bootJvms);
        return Projects;
    }

    public ProjectInfo loadTargetProject(String projectName, ArrayList<JvmInfo> bootJvms){

        JvmInfo bootJvm = null;
        if (bootJvms != null && bootJvms.size() > 0){

            bootJvm = LoaderHelper.getBootJvm(bootJvms);
        }

        String projectPath = DTConfiguration.PROJECT_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + projectName;
        File projectFile = new File(projectPath);
        ProjectInfo currentProject;
        if (projectFile.exists()){
            currentProject = LoaderHelper.analysisProject(projectFile);
        } else {
            throw new RuntimeException("loadTargetTestProject - Project " + projectPath + " not exist!");
        }
        return setupRunnableClasses(currentProject, bootJvm);
    }

    public ArrayList<ProjectInfo> loadTargetProjects(ArrayList<String> projects, ArrayList<JvmInfo> bootJvms){

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
                if (projectFile.isDirectory() && projects.contains(projectFile.getName())) {

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

    public ProjectInfo loadTargetProjectWithPredefinedClassPath(String projectName, ArrayList<JvmInfo> bootJvms, boolean usePredefinedClass){

        JvmInfo bootJvm = null;
        if (bootJvms != null && bootJvms.size() > 0){

            bootJvm = LoaderHelper.getBootJvm(bootJvms);
        }

        String projectPath = DTConfiguration.PROJECT_DEPENS_ROOT + DTPlatform.FILE_SEPARATOR + projectName;
        File projectFile = new File(projectPath);
        ProjectInfo currentProject;
        if (projectFile.exists()){
            currentProject = LoaderHelper.analysisProject(projectFile);

            if (usePredefinedClass){
                String predefinedClassPath = projectPath + DTPlatform.FILE_SEPARATOR + "testcases.txt";
                File predefinedClassFile = new File(predefinedClassPath);
                if (predefinedClassFile.exists()){
                    currentProject.setPredefinedClassPath(predefinedClassPath);
                }
            }
        } else {
            throw new RuntimeException("loadTargetTestProject - Project " + projectPath + " not exist!");
        }
        return setupRunnableClasses(currentProject, bootJvm);
    }

    public ProjectInfo loadTargetProjectWithGivenPath(String projectDir, String projectName, ArrayList<JvmInfo> bootJvms, boolean usePredefinedClass){

        JvmInfo bootJvm = null;
        if (bootJvms != null && bootJvms.size() > 0){

            bootJvm = LoaderHelper.getBootJvm(bootJvms);
        }

        String projectPath = projectDir + DTPlatform.FILE_SEPARATOR + projectName;
        File projectFile = new File(projectPath);
        ProjectInfo currentProject;
        if (projectFile.exists()){

            currentProject = LoaderHelper.analysisProject(projectFile);
            if (usePredefinedClass) {
                String predefinedClassPath = projectPath + DTPlatform.FILE_SEPARATOR + "testcases.txt";
                File predefinedClassFile = new File(predefinedClassPath);
                if (predefinedClassFile.exists()){
                    currentProject.setPredefinedClassPath(predefinedClassPath);
                }
            }
        } else {
            throw new RuntimeException("loadTargetTestProject - Project " + projectPath + " not exist!");
        }
        return setupRunnableClasses(currentProject, bootJvm);
    }
}
