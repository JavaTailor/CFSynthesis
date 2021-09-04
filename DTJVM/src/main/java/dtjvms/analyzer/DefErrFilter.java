package dtjvms.analyzer;

import dtjvms.executor.CFM.JvmOutput;

import java.util.*;

public class DefErrFilter extends Filter{

    private static final List<String> FILTER_STRINGS = new ArrayList<>();
    private static final HashMap<String, ArrayList<String>> DEF_EE_PAIR = new HashMap<>();

    static {
        FILTER_STRINGS.add("ExceptionInInitializerError");
        FILTER_STRINGS.add("ensureError");
		FILTER_STRINGS.add("ImportError");
        FILTER_STRINGS.add("recordInitializationFailure");
//        DEF_EE_PAIR.put("1", new ArrayList<String>(Arrays.asList("NegativeArraySizeException", "IllegalAccessError")));
//        DEF_EE_PAIR.put("2", new ArrayList<String>(Arrays.asList("ClassFormatError", "LinkageError")));
//        DEF_EE_PAIR.put("3", new ArrayList<String>(Arrays.asList("VerifyError", "RuntimeException")));
    }

    @Override
    protected void doFilter(HashMap<String, JvmOutput> results) {

        Set<String> allFEE = new HashSet<>();
        for (String key : results.keySet()) {

            JvmOutput jvmOut = results.get(key);

            allFEE.addAll(jvmOut.getFEEInfo());
            for (String filterString : FILTER_STRINGS) {

                if (jvmOut.getFailures().contains(filterString)){
                    jvmOut.getFailures().remove(filterString);
                }
                if (jvmOut.getExceptions().contains(filterString)){
                    jvmOut.getExceptions().remove(filterString);
                }
                if (jvmOut.getErrors().contains(filterString)){
                    jvmOut.getErrors().remove(filterString);
                }
            }
        }
        //EEF pair filter
        for (String s : DEF_EE_PAIR.keySet()) {

            ArrayList<String> defPair = DEF_EE_PAIR.get(s);
            if (equalCheck(defPair, allFEE)){

                for (String key : results.keySet()) {

                    JvmOutput jvmOut = results.get(key);
                    for (String s1 : defPair) {
                        if (jvmOut.getFailures().contains(s1)){
                            jvmOut.getFailures().remove(s1);
                        }
                        if (jvmOut.getExceptions().contains(s1)){
                            jvmOut.getExceptions().remove(s1);
                        }
                        if (jvmOut.getErrors().contains(s1)){
                            jvmOut.getErrors().remove(s1);
                        }
                    }
                }
            }
        }
    }

    public boolean equalCheck(List<String> defPair, Set<String> currentFEE){

//        if (defPair.size() == currentFEE.size()){
            boolean isSame = true;
            for (String s : defPair) {

                if (!currentFEE.contains(s)){
                    isSame = false;
                    break;
                }
            }
            return isSame;
//        } else {
//            return false;
//        }
    }
}
