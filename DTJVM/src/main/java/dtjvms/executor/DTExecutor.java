package dtjvms.executor;

import dtjvms.*;
import dtjvms.analyzer.DTAnalyzer;
import dtjvms.analyzer.DiffCore;
import dtjvms.analyzer.JDKAnalyzer;
import dtjvms.executor.CFM.CFMExecutor;
import dtjvms.executor.CFM.JvmOutput;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import vmoptions.VMOptions;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DTExecutor extends Executor{

    private JvmOutput currentOutput;
    private Process currentProcess;

    public static DTExecutor dtExecutor;

    public static DTExecutor getInstance(){

        if (dtExecutor == null){

            dtExecutor = new DTExecutor();
        }
        return dtExecutor;
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

    public void dtSingleClassInProj(ArrayList<JvmInfo> jvmCmds, ProjectInfo currentProject, String classname){

        ArrayList<String> vmOptions = currentProject.getVmoptions();
        String classPath = currentProject.getpClassPath();
        /**
         * 01 differential testing java application class
         */
        if (currentProject.getApplicationClasses().contains(classname)){

            System.out.println("Project-application: "
                    + currentProject.getProjectName()
                    + classname
                    + "...");

            Thread ctester = new Thread(new Runnable() {
                @Override
                public void run() {
                    dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() , classname, false, new String[]{});
                }
            });
            ctester.start();
            try {
                TimeUnit.SECONDS.timedJoin(ctester, DTConfiguration.CLASS_MAX_RUNTIME);
                if (ctester.isAlive() || ctester.isInterrupted()){
                    ctester.stop();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /**
         * 02 differential testing junit test case
         */
        if (currentProject.getJunitClasses().contains(classname)){

            System.out.println("Project-junit: "
                    + currentProject.getProjectName()
                    + classname
                    + "...");

            ThreadGroup threadGroup = new ThreadGroup("Tester");
            Thread ctester = new Thread(threadGroup, new Thread(new Runnable() {
                @Override
                public void run() {
                    dtSingleClass(jvmCmds, vmOptions, classPath, currentProject.getProjectName() ,classname, true, new String[]{});
                }
            }));
            ctester.start();
            try {
                TimeUnit.SECONDS.timedJoin(ctester, DTConfiguration.CLASS_MAX_RUNTIME);
                threadGroup.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dtSingleClass(ArrayList<JvmInfo> jvms,
                                     ArrayList<String> pOptions,
                                     String classpath,
                                     String projName,
                                     String className,
                                     boolean isJunit,
                                     String... args){

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

        DiffCore diff = JDKAnalyzer.getInstance().analysis(className, results);

        if (diff != null){

            ExecutorHelper.logJvmOutput(projName, className, diff ,results);
        }
    }
}
