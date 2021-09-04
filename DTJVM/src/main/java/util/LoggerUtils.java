package util;

import dtjvms.DTPlatform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtils {

    private static Logger logger;
    private static String RESULT_FILE_NAME = "difference";
    private static String FILE_SUFFIX = ".log";
    private static String ROOT_PATH = "./03results";

    public static Logger getInstance(String timeStamp){

        logger = Logger.getLogger(timeStamp);
        logger.setLevel(Level.ALL);
        FileHandler handler = null;
        try {

            handler = new FileHandler(getLoggerPath(timeStamp, RESULT_FILE_NAME, FILE_SUFFIX),true);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    public static Logger getInstance(String timeStamp, String filename){

        logger = Logger.getLogger(filename);
        logger.setLevel(Level.ALL);
        FileHandler handler = null;
        try {

            handler = new FileHandler(getLoggerPath(timeStamp, filename, FILE_SUFFIX),true);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    public static Logger getInstance(String timeStamp, String filename, boolean append){

        logger = Logger.getLogger(filename);
        logger.setLevel(Level.ALL);
        FileHandler handler = null;
        try {

            handler = new FileHandler(getLoggerPath(timeStamp, filename, FILE_SUFFIX),append);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    public static String getLoggerPath(String logdir, String logfilename, String suffix) {

        if(ROOT_PATH == null){

            if (DTPlatform.isMac()) {

                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else if (DTPlatform.isLinux()){

                //TODO check this path
                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else if (DTPlatform.isWin()){

                //TODO set path
                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else {

                //TODO other platform
            }
        }
        File root = new File(ROOT_PATH);
        if (!root.exists()){

            root.mkdirs();
        }

        Path outPath = Paths.get(ROOT_PATH, logdir);
        File outFile = outPath.toFile();
        if (!outFile.exists()){

            outFile.mkdirs();
        }

        Path path = Paths.get(outFile.toString(), logfilename + suffix);
        try{
            File file = new File(path.toString());
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.toString();
    }

    @Deprecated
    public static String getLoggerPath(String name, String suffix) {

        if(ROOT_PATH == null){

            if (DTPlatform.isMac()) {

                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else if (DTPlatform.isLinux()){

                //TODO check this path
                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else if (DTPlatform.isWin()){

                //TODO set path
                ROOT_PATH = "." + DTPlatform.FILE_SEPARATOR + "03results";
            } else {

                //TODO other platform
            }
        }
        File root = new File(ROOT_PATH);
        if (!root.exists()){

            root.mkdirs();
        }
        Path path = Paths.get(ROOT_PATH, name + suffix);
        try{
            File file = new File(path.toString());
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.toString();
    }
}

class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        Date date = new Date();
        String sDate = date.toString();
        return           record.getMessage() + "\n";
    }
}