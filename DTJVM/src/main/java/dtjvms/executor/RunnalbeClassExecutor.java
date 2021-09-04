package dtjvms.executor;

import dtjvms.DTConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class RunnalbeClassExecutor extends Executor{

    public static RunnalbeClassExecutor rcExecutor;

    public static RunnalbeClassExecutor getInstance(){

        if (rcExecutor == null){
            rcExecutor = new RunnalbeClassExecutor();
        }
        return rcExecutor;
    }

    @Override
    public HashMap<String, ArrayList<String>> execute(String cmd){

        ArrayList<String> junitClasses = new ArrayList<>();
        ArrayList<String> applicationClasses = new ArrayList<>();
        HashMap<String, ArrayList<String>> runnableClaees = new HashMap<>();
        currentCMD = cmd;
        try {

            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            new Thread(() -> {

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line = null ;
                    while ((line = inputReader.readLine()) !=  null ){

                        if (DTConfiguration.debug){
                            System.out.println(line);
                        }
                        if (line.startsWith("Junit@")){
                            junitClasses.add(line.split("@")[1]);
                        } else if (line.startsWith("Application@")){
                            applicationClasses.add(line.split("@")[1]);
                        }
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

                    if (DTConfiguration.debug){
                        System.out.println(line);
                    }
                    if (line.startsWith("Junit@")){
                        junitClasses.add(line.split("@")[1]);
                    } else if (line.startsWith("Application@")){
                        applicationClasses.add(line.split("@")[1]);
                    }
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
        } catch (Exception e){

            e.printStackTrace();
            System.err.println("Current CMD: " + currentCMD);
        }

        runnableClaees.put("Junit", junitClasses);
        runnableClaees.put("Application", applicationClasses);
        return runnableClaees;
    }
}
