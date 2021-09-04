package dtjvms;

import java.io.*;
import java.util.Collections;

public class DTPlatform {

    public static String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static String OS_VERSION = System.getProperty("os.version").toLowerCase();
    public static String JAVA_HOME = System.getProperty("java.home");
    public static String JAVA_VERSION = System.getProperty("java.version");
    public static String FILE_SEPARATOR = System.getProperty("file.separator");
    public static String PATH_SEPARATOR = System.getProperty("path.separator");
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    public static String FILE_ENCODIND;

    public static String JAR_SUFFIX = ".jar";
    public static String CLASS_SUFFIX = ".class";
    public static String JAVA_CMD = "java";
    public static String MACOS_JAVA_PATH = "Contents/Home/bin/java";
    public static String LINUX_JAVA_PATH = "bin" + FILE_SEPARATOR + "java";
    public static String WIN_JAVA_PATH = "bin" + FILE_SEPARATOR + "java.exe";
    public static String PROJECT_ELEMENTS = "lib|out|src|test|target";
    public static String RUNNABLE_CLASS_LOADER = "dtjvms.loader.RunnableClassLoader";

    public static String JUNIT_CMD = "JAVACMD VMOPTIONS -cp CLASSPATH org.junit.runner.JUnitCore CLASSNAME ARGS";
    public static String APPLICATION_CMD = "JAVACMD VMOPTIONS -cp CLASSPATH CLASSNAME ARGS";

    public static String LOCAL_JAVA_CMD = "java -cp CLASSPATH CLASSNAME ARGS";
    public static String LOCAL_FULL_JAVA_CMD = "java VMOPTIONS -cp CLASSPATH CLASSNAME ARGS";

    public static String[] FILTER_CLASS_PATH = new String[]{
        "/Users/yingquanzhao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-E/ch-0/203.5981.183/IntelliJ IDEA Edu.app/Contents/lib/idea_rt.jar",
        ":/Users/yingquanzhao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-E/ch-0/203.5981.183/IntelliJ IDEA Edu.app/Contents/lib/idea_rt.jar",
        ":/Users/yingquanzhao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-E/ch-0/203.5981.183/IntelliJ IDEA Edu.app/Contents/plugins/junit/lib/junit5-rt.jar",
        ":/Users/yingquanzhao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-E/ch-0/203.5981.183/IntelliJ IDEA Edu.app/Contents/plugins/junit/lib/junit-rt.jar",
        ":/Users/yingquanzhao/Library/Caches/JetBrains/IdeaIE2020.3/captureAgent/debugger-agent.jar",
        ":/Users/yingquanzhao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-C/ch-0/211.7628.21/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar",
    };

    private static DTPlatform tp;

    static {
        if (isWin()){
            FILE_ENCODIND = "GBK";
        }else {
            FILE_ENCODIND = "UTF-8";
        }
    }

    public static String getJavaClassPath(){

        String classPath = System.getProperty("java.class.path");
        for (String s : FILTER_CLASS_PATH) {
            classPath = classPath.replace(s,"");
        }
        return classPath;
    }

    public static DTPlatform getInstance() {

        if (tp == null){
            tp = new DTPlatform();
        }
        return tp;
    }

    public static String getOsName() {
        return OS_NAME;
    }

    public static boolean isMac(){
        return OS_NAME.indexOf("mac") >= 0;
    }

    public static boolean isLinux(){
        return OS_NAME.indexOf("linux") >= 0;
    }

    public static boolean isWin(){
        return OS_NAME.indexOf("windows") >= 0;
    }

    @Override
    public String toString() {
        String titile = String.join("", Collections.nCopies(50,"=")) +
                " Testing Platform Information " + String.join("", Collections.nCopies(50,"="));
        return  titile + LINE_SEPARATOR +
                "     os name: " + OS_NAME + LINE_SEPARATOR +
                "  os version: " + OS_VERSION + LINE_SEPARATOR +
                "   java home: " + JAVA_HOME + LINE_SEPARATOR +
                "java version: " + JAVA_VERSION + LINE_SEPARATOR +
                String.join("", Collections.nCopies(titile.length(),"="));
    }
}
