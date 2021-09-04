import dtjvms.*;
import dtjvms.loader.DTLoader;
import org.junit.Test;

import java.util.ArrayList;

public class Preview {

    public static void main(String[] args) {

        DTConfiguration.setJvmDepensRoot("." + DTPlatform.FILE_SEPARATOR + "01JVMS");
        DTConfiguration.setProjectDepensRoot("." + DTPlatform.FILE_SEPARATOR + "02Benchmarks");
        /**
         * Testing platform
         */
        System.out.println(DTPlatform.getInstance());

        ArrayList<JvmInfo> jvmCmds = DTLoader.getInstance().loadJvms();
        ArrayList<ProjectInfo> projectInfos = DTLoader.getInstance().loadProjects(jvmCmds);

        /**
         * JVM
         */
        for (JvmInfo jvmCmd : jvmCmds) {
            System.out.println(jvmCmd);
        }
        /**
         * Projects
         */
        for (ProjectInfo projectInfo : projectInfos) {

            System.out.println(projectInfo);
        }
    }
}
