package dtjvms.executor;

import dtjvms.DTPlatform;

import java.io.*;

public class Executor {

    protected String currentCMD;

    public static Executor executor;
    public static Executor getInstance(){

        if (executor == null){

            executor = new Executor();
        }
        return executor;
    }

    /**
     * execute cmd
     * @param cmd
     */
    public Object execute(String cmd) {

        currentCMD = cmd;
        try {

            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            SequenceInputStream sequenceIs = new SequenceInputStream(inputStream, errorStream);
            BufferedInputStream bufStream = new BufferedInputStream(sequenceIs);
            Reader reader = new InputStreamReader(bufStream, DTPlatform.FILE_ENCODIND);
            BufferedReader bufReader = new BufferedReader(reader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);
            }
            inputStream.close();
            errorStream.close();
            process.waitFor();
            process.destroy();
        } catch (Exception e){

            e.printStackTrace();
            System.err.println("Current CMD: " + currentCMD);
        }
        return null;
    }

    public String getCurrentCMD() {
        return currentCMD;
    }

    public void setCurrentCMD(String currentCMD) {
        this.currentCMD = currentCMD;
    }
}
