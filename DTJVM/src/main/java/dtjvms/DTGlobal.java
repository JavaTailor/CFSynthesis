package dtjvms;

import util.LoggerUtils;

import java.util.logging.Logger;

public class DTGlobal {

	private static long startTime;
    private static Logger logger;
    private static Logger diffLogger;
    private static Logger diffUniqueLogger;
    private static Logger rewardLogger;
    private static Logger dataLogger;
	private static Logger timeLogger;

    public static void setLogger(String timeStamp){

        if (logger == null){
            logger = LoggerUtils.getInstance(timeStamp);
        }
    }
    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime(long startTime) {
        DTGlobal.startTime = startTime;
    }
    public static void setDiffLogger(String timeStamp, String filename){

        if (diffLogger == null){
            diffLogger = LoggerUtils.getInstance(timeStamp, filename, true);
        }
    }

    public static void setDiffUniqueLogger(String timeStamp, String filename){

        if (diffUniqueLogger == null){
            diffUniqueLogger = LoggerUtils.getInstance(timeStamp, filename, true);
        }
    }

    public static void setDataLogger(String timeStamp, String filename){

        if (dataLogger == null){
            dataLogger = LoggerUtils.getInstance(timeStamp, filename, true);
        }
    }

    public static void setTimeLogger(String timeStamp, String filename){

        if (timeLogger == null){
            timeLogger = LoggerUtils.getInstance(timeStamp, filename, true);
        }
    }

    public static void setRewardLogger(String timeStamp, String filename) {
        if (rewardLogger == null){
            rewardLogger = LoggerUtils.getInstance(timeStamp, filename, true);
        }
    }

    public static Logger getLogger(){
        return logger;
    }

    public static Logger getDiffLogger() {
        return diffLogger;
    }

    public static Logger getDiffUniqueLogger() {
        return dataLogger;
    }

    public static Logger getDataLogger() {
        return diffUniqueLogger;
    }

    public static Logger getTimeLogger() {
        return timeLogger;
    }

    public static Logger getRewardLogger() {
        return rewardLogger;
    }
}
