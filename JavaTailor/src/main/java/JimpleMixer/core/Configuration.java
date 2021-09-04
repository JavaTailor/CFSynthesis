package JimpleMixer.core;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.options.Options;

import java.util.LinkedList;

public class Configuration {

    public static LinkedList<String> excludePathList;
    public static int maxDepth = 5;

    public static void initSootEnv(){

        String claspath = System.getProperty("java.class.path");
        Options.v().set_soot_classpath(claspath);

        Options.v().set_ignore_resolving_levels(true);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);
        excludeJDKLibrary();
    }

    public static void initSootEnvWithClassPath(String classPath){

        String claspath = System.getProperty("java.class.path");
        claspath = claspath +  System.getProperty("path.separator") + classPath;
        Options.v().set_soot_classpath(claspath);
        Options.v().set_java_version(Options.java_version_8);
        Options.v().set_wrong_staticness(Options.wrong_staticness_ignore);

        Options.v().set_ignore_resolving_levels(true);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);
        excludeJDKLibrary();
    }

    public static void set_output_path(String outpath){

        Options.v().set_output_dir(outpath);
    }

    private static void excludeJDKLibrary() {
        //exclude jdk classes
        Options.v().set_exclude(excludeList());
//        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
    }

    private static LinkedList<String> excludeList() {

        if(excludePathList == null) {

            excludePathList = new LinkedList<String> ();
            excludePathList.add("java.");
            excludePathList.add("javax.");
            excludePathList.add("sun.");
            excludePathList.add("sunw.");
            excludePathList.add("com.sun.");
            excludePathList.add("com.ibm.");
            excludePathList.add("com.apple.");
            excludePathList.add("apple.awt.");
        }
        return excludePathList;
    }
}
