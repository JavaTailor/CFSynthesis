package dtjvms.analyzer;

import dtjvms.executor.CFM.JvmOutput;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StdErrFilter extends Filter{

    private static final String jvmwarningmsg = ".* VM warning:.*";
    private static final String COMMON_EXCEPTION_KEYWORD_PATTERN = "[\\s\\S]*?[^A-Za-z]([A-Za-z]+?Exception)[^A-Za-z][\\s\\S]*";
    private static final String COMMON_ERROR_KEYWORD_PATTERN = "[\\s\\S]*?[^A-Za-z]([A-Za-z]+?Error)[^A-Za-z][\\s\\S]*";
    private static final String COMMON_FAILURE_KEYWORD_PATTERN = "[\\s\\S]*?[^A-Za-z]([A-Za-z]+?Failure)[^A-Za-z][\\s\\S]*";

    @Override
    protected void doFilter(HashMap<String, JvmOutput> results) {

        Pattern errorPattern = Pattern.compile(COMMON_ERROR_KEYWORD_PATTERN, Pattern.MULTILINE);
        Pattern exceptionPattern = Pattern.compile(COMMON_EXCEPTION_KEYWORD_PATTERN, Pattern.MULTILINE);
        Pattern failurePattern = Pattern.compile(COMMON_FAILURE_KEYWORD_PATTERN, Pattern.MULTILINE);

        for (String key : results.keySet()) {

            JvmOutput jvmOut = results.get(key);

            if (!jvmOut.getOutput().isEmpty()){

                List<String> outputs = jvmOut.asLines();
                for (String output : outputs) {

                    output = output + "\n";
                    Matcher errorMatcher = errorPattern.matcher(output);
                    if (errorMatcher.find()){
                        if (!jvmOut.getErrors().contains(errorMatcher.group(1))){
                            jvmOut.getErrors().add(errorMatcher.group(1));
                        }
                    }
                    Matcher exceptionMatcher = exceptionPattern.matcher(output);
                    if (exceptionMatcher.find()){
                        if (!jvmOut.getExceptions().contains(exceptionMatcher.group(1))){
                            jvmOut.getExceptions().add(exceptionMatcher.group(1));
                        }
                    }
                    Matcher failureMatcher = failurePattern.matcher(output);
                    if (failureMatcher.find()){
                        if (!jvmOut.getFailures().contains(failureMatcher.group(1))){
                            jvmOut.getFailures().add(failureMatcher.group(1));
                        }
                    }
                }
            }
        }
    }
}
