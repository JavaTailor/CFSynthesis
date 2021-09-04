package dtjvms.executor.CFM;

import dtjvms.*;
import dtjvms.analyzer.DiffCore;
import dtjvms.analyzer.JDKAnalyzer;
import dtjvms.executor.Executor;
import dtjvms.executor.ExecutorHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CFMExecutor extends Executor {

    private JvmOutput currentOutput;
    private Process currentProcess;

    public static HashMap<String, Integer> allDiff = new HashMap<>();

    private boolean diffFound;
    private boolean disCard;

    public static CFMExecutor cfmExecutor;

    public static CFMExecutor getInstance(){

        if (cfmExecutor == null){
            cfmExecutor = new CFMExecutor();
        }
        return cfmExecutor;
    }

    @Override
    public JvmOutput execute(String cmd) {

        JvmOutput output = null;
        currentOutput = null;
        try {
            currentProcess = Runtime.getRuntime().exec(cmd);
            output = ExecutorHelper.getJvmOutput(currentProcess);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentOutput = output;
        return output;
    }

    public void shutDown(){
        if (currentProcess != null){
            currentProcess.destroy();
            currentProcess.destroyForcibly();
        }
    }

    public boolean dtSingleClassInProj(ArrayList<JvmInfo> jvmCmds,
                                       ProjectInfo currentProject,
                                       ProjectInfo mutateProvideProject,
                                       String executeClassName,
                                       String logClassName) {

        diffFound = false;
        disCard = false;

        HashMap<String, JvmOutput> results = new HashMap<>();

        ArrayList<String> vmOptions = currentProject.getVmoptions();
        ArrayList<String> projOptions = currentProject.getProjoptions();
        String[] projOptionsArray = projOptions.toArray(new String[projOptions.size()]);
        String classPath = currentProject.getpClassPath() + DTPlatform.PATH_SEPARATOR + mutateProvideProject.getpClassPath();
        /**
         * 01 differential testing java application class
         */
        if (currentProject.getApplicationClasses().contains(executeClassName)){

            System.out.println("Project-application: "
                    + currentProject.getProjectName()
                    + "-"
                    + logClassName
                    + "...");
            results = dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() , executeClassName, false, projOptionsArray);
        }
        /**
         * 02 differential testing junit test case
         */
        if (currentProject.getJunitClasses().contains(executeClassName)){

            System.out.println("Project-junit: "
                    + currentProject.getProjectName()
                    + "-"
                    + logClassName
                    + "...");
            results = dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() ,executeClassName, true, projOptionsArray);
        }

        DiffCore diff = JDKAnalyzer.getInstance().analysis(logClassName, results);

        if (diff != null){

            diffFound = true;
            ExecutorHelper.logJvmOutput(DTGlobal.getDiffLogger(), currentProject.getProjectName(), logClassName, diff ,results);
        }
        disCard = JDKAnalyzer.getInstance().getDiscardFlag();
        return disCard;
    }

    public boolean dtSingleClassInProj(ArrayList<JvmInfo> jvmCmds,
                                    ProjectInfo currentProject,
                                    String executeClassName,
                                    String logClassName) {

        diffFound = false;
        disCard = false;
        HashMap<String, JvmOutput> results = new HashMap<>();

        ArrayList<String> vmOptions = currentProject.getVmoptions();
        ArrayList<String> projOptions = currentProject.getProjoptions();
        String[] projOptionsArray = projOptions.toArray(new String[projOptions.size()]);
        String classPath = currentProject.getpClassPath();
        /**
         * 01 differential testing java application class
         */
        if (currentProject.getApplicationClasses().contains(executeClassName)){

            System.out.println("Project-application: "
                    + currentProject.getProjectName()
                    + "-"
                    + logClassName
                    + "...");
            results = dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() , executeClassName, false, projOptionsArray);
        }
        /**
         * 02 differential testing junit test case
         */
        if (currentProject.getJunitClasses().contains(executeClassName)){

            System.out.println("Project-junit: "
                    + currentProject.getProjectName()
                    + "-"
                    + logClassName
                    + "...");
            results = dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() ,executeClassName, true, projOptionsArray);
        }
        DiffCore diff = JDKAnalyzer.getInstance().analysis(logClassName, results);

        if (diff != null){
            diffFound = true;
            ExecutorHelper.logJvmOutput(DTGlobal.getDiffLogger(), currentProject.getProjectName(), logClassName, diff ,results);
        }
        disCard = JDKAnalyzer.getInstance().getDiscardFlag();
        return disCard;
    }

    public boolean isDiffFound() {
        return diffFound;
    }

    public boolean isDisCard() {
        return disCard;
    }

    public void dtSingleClassMultiThread(ArrayList<JvmInfo> jvms,
                                         ArrayList<String> pOptions,
                                         String classpath,
                                         String projName,
                                         String className,
                                         boolean isJunit,
                                         String... args) {

        HashMap<String, JvmOutput> results = new HashMap<>();

        for (JvmInfo jvm : jvms) {

            String jvmId = jvm.getJvmId() != null ? jvm.getJvmId() : jvm.getFolderName();
            String cmdExecute = ExecutorHelper.assembleJavaCmd(jvm.getJavaCmd(), pOptions, classpath, className, isJunit, args);

            Thread ctester = new Thread(new Runnable() {
                @Override
                public void run() {
                    getInstance().execute(cmdExecute);
                }
            });
            ctester.start();
            try {
                TimeUnit.SECONDS.timedJoin(ctester, DTConfiguration.CLASS_MAX_RUNTIME);
                CFMExecutor.getInstance().shutDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentOutput != null){
                results.put(jvmId, currentOutput);
            } else {
                results.put(jvmId, new JvmOutput("JvmOutput-TIMEOUT"));
            }
        }

        if (JDKAnalyzer.getInstance().analysis(className, results) != null){
            ExecutorHelper.logJvmOutput(projName, className, results);
        }
    }


    public HashMap<String, JvmOutput> dtSingleClass(ArrayList<JvmInfo> jvms,
                                                    ArrayList<String> pOptions,
                                                    String classpath,
                                                    String projName,
                                                    String className,
                                                    boolean isJunit,
                                                    String... args) {

        HashMap<String, JvmOutput> results = new HashMap<>();

        for (JvmInfo jvm : jvms) {

            String jvmId = jvm.getJvmId() != null ? jvm.getJvmId() : jvm.getFolderName();
            String cmdExecute = ExecutorHelper.assembleJavaCmd(jvm.getJavaCmd(), pOptions, classpath, className, isJunit, args);

            Thread ctester = new Thread(new Runnable() {
                @Override
                public void run() {
                    getInstance().execute(cmdExecute);
                }
            });
            ctester.start();
            try {
                TimeUnit.SECONDS.timedJoin(ctester, DTConfiguration.CLASS_MAX_RUNTIME);
                CFMExecutor.getInstance().shutDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentOutput != null){
                results.put(jvmId, currentOutput);
            } else {
                results.put(jvmId, new JvmOutput("JvmOutput-TIMEOUT"));
            }
        }
        return results;
    }

    public JvmOutput getCurrentOutput() {
        return currentOutput;
    }
}
