import dtjvms.*;
import dtjvms.loader.DTLoader;

import java.util.ArrayList;

public class DTPreview {

    /**
     * Overview of the test platform, test jvms and test projects
     */
    public static void main(String[] args) {

        Boolean predefinedClasses = true;
        String defineClassesPath = "testcases";

        switch (args.length){

            case 1:

                /**
                 * 01 predefined class path
                 */
                predefinedClasses = true;
                defineClassesPath = args[0];
                break;
            default:
                break;
        }

        DTConfiguration.setJvmDepensRoot("." + DTPlatform.FILE_SEPARATOR + "01JVMS");
        DTConfiguration.setProjectDepensRoot("." + DTPlatform.FILE_SEPARATOR + "02Benchmarks");

        /**
         * Testing platform
         */
        System.out.println(DTPlatform.getInstance());

        ArrayList<JvmInfo> jvmCmds = DTLoader.getInstance().loadJvms();
//        ArrayList<ProjectInfo> projectInfos = DTLoader.loadTestProjectsWithBootJvms(jvmCmds);

        /**
         * JVM
         */
        for (JvmInfo jvmCmd : jvmCmds) {
            System.out.println(jvmCmd);
        }
        /**
         * Projects
         */
//        for (ProjectInfo projectInfo : projectInfos) {
//
//            System.out.println(projectInfo);
////            if (projectInfo.getApplicationClasses().size() > 0){
////                projectInfo.getApplicationClasses().forEach(System.out::println);
////            }
//        }
    }
}
