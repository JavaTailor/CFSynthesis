package dtjvms.executor;

import dtjvms.DTConfiguration;
import dtjvms.DTGlobal;
import dtjvms.DTPlatform;
import dtjvms.analyzer.DiffCore;
import dtjvms.executor.CFM.JvmOutput;
import dtjvms.executor.CFM.StreamPumper;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExecutorHelper {

    /**
     * Pumps stdout and stderr the running process into a String.
     *
     * @param process Process to pump.
     * @return Output from process.
     * @throws IOException If an I/O error occurs.
     */
//    public static JvmOutput getJvmOutput(Process process) throws IOException {
//
//        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
//        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
//        StreamPumper outPumper = new StreamPumper(process.getInputStream(), stdoutBuffer);
//        StreamPumper errPumper = new StreamPumper(process.getErrorStream(), stderrBuffer);
//        Thread outPumperThread = new Thread(outPumper);
//        Thread errPumperThread = new Thread(errPumper);
//
//        outPumperThread.setDaemon(true);
//        errPumperThread.setDaemon(true);
//
//        outPumperThread.start();
//        errPumperThread.start();
//
//        try {
//            process.waitFor(15, TimeUnit.SECONDS);
//            outPumperThread.join();
//            errPumperThread.join();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return null;
//        }
//
//        return new JvmOutput(stdoutBuffer.toString(), stderrBuffer.toString(), process.exitValue());
//    }

    public static JvmOutput getJvmOutput(Process process) throws IOException {

        final String[] stdoutBuffer = {""};
        String stderrBuffer = "";

        try {
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            new Thread(() -> {

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line = null ;
                    while ((line = inputReader.readLine()) !=  null ){

                        stdoutBuffer[0] = stdoutBuffer[0] + line + "\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{

                    try {
                        inputStream.close();
                        inputReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            try {
                String line = null;
                while ((line = errorReader.readLine()) != null) {
                    stderrBuffer = stderrBuffer + line + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    errorStream.close();
                    errorReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            process.waitFor();
            process.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }

        return new JvmOutput(stdoutBuffer[0], stderrBuffer, process.exitValue());
    }

    /**
     * assemble the given param to a runnable java cmd
     * @param javaCmd   path to java
     * @param vmoptions jvm vm options
     * @param classpath classpath (jar + jvm classpath + project path)
     * @param classname class name
     * @param isJunit   if current class is a junit class, use junit cmd
     * @param args  ars
     * @return
     */
    public static String assembleJavaCmd(String javaCmd, ArrayList<String> vmoptions, String classpath, String classname, boolean isJunit, String... args){

        String argString = "";
        String optString = "";
        if (args.length > 0){
            argString = StringUtils.join(Arrays.asList(args), " ");
        }
        if (vmoptions != null && vmoptions.size() > 0){
            optString = StringUtils.join(vmoptions, " ");
        }

        if (isJunit){

            return DTPlatform.JUNIT_CMD.replace("JAVACMD", javaCmd)
                    .replace("VMOPTIONS", optString)
                    .replace("CLASSPATH", classpath)
                    .replace("CLASSNAME", classname)
                    .replace("ARGS", argString);
        }else{
            return DTPlatform.APPLICATION_CMD.replace("JAVACMD", javaCmd)
                    .replace("VMOPTIONS", optString)
                    .replace("CLASSPATH", classpath)
                    .replace("CLASSNAME", classname)
                    .replace("ARGS", argString);
        }
    }

    public static void logJvmOutput(Logger logger, String projName, String className, DiffCore diff, HashMap<String, JvmOutput> results){

        if (logger != null){

            logger.info("Difference found: project-" + projName + "-class-" + className);
            logger.info(diff.getDetailedMessage());
            for (String s : results.keySet()) {

                logger.info(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                logger.info(String.valueOf(diff.getDiffMessage() + ":" + results.get(s).getFEEInfo()));
                logger.info(String.valueOf(results.get(s)));
            }
        } else {

            System.err.println("Difference found: project-" + projName + "@class-" + className);
            System.err.println(diff.getDetailedMessage());
            for (String s : results.keySet()) {

                System.err.println(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                System.err.println(String.valueOf(diff.getDiffMessage() + ":" + results.get(s).getFEEInfo()));
                System.err.println(results.get(s));
            }
        }
    }

    public static void logData(Logger logger, String projName, HashMap<String, Integer> data){

        if (logger != null){

            logger.info("Difference found: project-" + projName);
            for (String s : data.keySet()) {

                Integer time = data.get(s);
                logger.info(s + " " + time);
            }
        } else {

            System.err.println("Difference found: project-" + projName);
            for (String s : data.keySet()) {
                Integer time = data.get(s);
                System.out.println(s + " " + time);
            }
        }
    }


    public static void logJvmOutput(String projName, String className, DiffCore diff, HashMap<String, JvmOutput> results){

        if (DTGlobal.getDiffLogger() != null){

            DTGlobal.getDiffLogger().info("Difference found: project-" + projName + "-class-" + className);
            DTGlobal.getDiffLogger().info(diff.getDetailedMessage());
            for (String s : results.keySet()) {

                DTGlobal.getDiffLogger().info(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                DTGlobal.getDiffLogger().info(String.valueOf(diff.getDiffMessage() + ":" + results.get(s).getFEEInfo()));
                DTGlobal.getDiffLogger().info(String.valueOf(results.get(s)));
            }
        } else {

            System.err.println("Difference found: project-" + projName + "@class-" + className);
            System.err.println(diff.getDetailedMessage());
            for (String s : results.keySet()) {

                System.err.println(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                System.err.println(String.valueOf(diff.getDiffMessage() + ":" + results.get(s).getFEEInfo()));
                System.err.println(results.get(s));
            }
        }
    }

    public static void logJvmOutput(String projName, String className, HashMap<String, JvmOutput> results){

        if (DTGlobal.getDiffLogger() != null){

            DTGlobal.getDiffLogger().info("Difference found: project-" + projName + "-class-" + className);
            for (String s : results.keySet()) {

                DTGlobal.getDiffLogger().info(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                DTGlobal.getDiffLogger().info(String.valueOf(results.get(s)));
            }
        } else {

            System.err.println("Difference found: project-" + projName + "@class-" + className);
            for (String s : results.keySet()) {

                System.err.println(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                System.err.println(results.get(s));
            }
        }
    }

    /**
     * log different jvm output to local file
     * @param projName  current project name
     * @param className current class name
     * @param results   all jvm output
     */
    public static void logOutput(String projName, String className, HashMap<String, ArrayList<String>> results){

        if (DTGlobal.getDiffLogger() != null){

            DTGlobal.getDiffLogger().info("Difference found: project-" + projName + "-class-" + className);
            for (String s : results.keySet()) {

                DTGlobal.getDiffLogger().info(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                DTGlobal.getDiffLogger().info(String.valueOf(results.get(s)));
            }
        } else {

            System.err.println("Difference found: project-" + projName + "@class-" + className);
            for (String s : results.keySet()) {

                System.err.println(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                System.err.println(results.get(s));
            }
        }
    }

    /**
     * log different jvm output and the options used in this run to local file
     * @param projName  current project name
     * @param className current class name
     * @param options   vm options used
     * @param commands  full java command
     * @param results   all jvm output
     */
    public static void logWithOptions(String projName,
                                      String className,
                                      HashMap<String, ArrayList<String>> options ,
                                      HashMap<String, String> commands,
                                      HashMap<String, ArrayList<String>> results){

        if (DTGlobal.getDiffLogger() != null){

            DTGlobal.getDiffLogger().info("Difference found: project-" + projName + "-class-" + className);
            for (String s : results.keySet()) {

                DTGlobal.getDiffLogger().info(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));

                //options
                List<String> opts = options.get(s);
                DTGlobal.getDiffLogger().info(String.valueOf(opts));
                //commands
                String cmd = commands.get(s);
                DTGlobal.getDiffLogger().info(cmd);
                //results
                List<String> less = results.get(s);
                DTGlobal.getDiffLogger().info(String.valueOf(less));
            }
            DTGlobal.getDiffLogger().info("\n");
        } else {

            System.err.println("Difference found: project-" + projName + "@class-" + className);
            for (String s : results.keySet()) {

                System.err.println(String.join("", Collections.nCopies(50,"=")) +
                        s + String.join("", Collections.nCopies(50,"=")));
                System.err.println(results.get(s));
            }
        }
    }
}
