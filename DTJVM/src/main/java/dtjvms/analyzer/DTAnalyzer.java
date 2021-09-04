package dtjvms.analyzer;

import dtjvms.DTConfiguration;

import java.util.*;

public class DTAnalyzer {

    public static HashMap<String, ArrayList<String>> analysis(HashMap<String, ArrayList<String>> result){

        HashMap<String, ArrayList<String>> filter = new HashMap<>();
        /**
         * 00 filter results
         */
        for (String s : result.keySet()) {

            ArrayList<String> out = result.get(s);
            boolean remove = false;
            for (String s1 : out) {

                if (s1.contains("Verifyingclass")
                        || s1.contains("JVMJ9VM007ECommand-lineoptionunrecognised")
                        || s1.contains("Globalflags")
                        || s1.contains("SelectedVM[default]byoption")
                        || s1.contains("CouldnotcreatetheJavaVirtualMachine")){
                    remove = true;
                    break;
                }
            }
            if (!remove){
                filter.put(s, out);
            }
        }
        result = filter;

        HashMap<String, ArrayList<String>> difference = new HashMap<>();

        for (String current : result.keySet()) {

            for (String target : result.keySet()) {

                if (!current.equals(target)){

                    boolean differenceFound = false;

                    ArrayList<String> j1results = safeFilterResults(result.get(current));
                    ArrayList<String> j2results = safeFilterResults(result.get(target));

                    ArrayList<String> j1exceptions = getKeyResults(j1results);
                    ArrayList<String> j2exceptions = getKeyResults(j2results);

                    /**
                     * 01 exception
                     */
                    if (j1exceptions.size() > 0 || j2exceptions.size() >0){

                        if ((j1exceptions.size() == 0 && j2exceptions.size() != 0) || (j1exceptions.size() != 0 && j2exceptions.size() == 0)){

                            if (!difference.keySet().contains(current)){
                                difference.put(current,getExceptions(j1exceptions));
                            }
                            if (!difference.keySet().contains(target)){
                                difference.put(target,getExceptions(j2exceptions));
                            }
                        }

                        int min = j1exceptions.size() < j2exceptions.size() ? j1exceptions.size() : j2exceptions.size();
                        int bound = min >= 3 ? 3 : min;
                        for (int i = 0; i < bound; i++){

                            if (!j1exceptions.get(i).equals(j2exceptions.get(i))){

                                if (!difference.keySet().contains(current)){
                                    difference.put(current,j1results);
                                }
                                if (!difference.keySet().contains(target)){
                                    difference.put(target,j2results);
                                }
                                differenceFound = true;
                                break;
                            }
                        }
                    }
                    /**
                     * 02 normal output with different values
                     */
                    if (!differenceFound){

                        int min = j1results.size() < j2results.size() ? j1results.size() : j2results.size();
                        int bound = min >= 3 ? 3 : min;
                        for (int i = 0; i < bound; i++){

                            if (!j1results.get(i).equals(j2results.get(i))){

                                if (!difference.keySet().contains(current)){
                                    difference.put(current,j1results);
                                }
                                if (!difference.keySet().contains(target)){
                                    difference.put(target,j2results);

                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return difference;
    }

    public static HashMap<String, ArrayList<String>> outputAnalysisRootCase(HashMap<String, ArrayList<String>> result){

        HashMap<String, ArrayList<String>> difference = new HashMap<>();

        for (String current : result.keySet()) {

            for (String target : result.keySet()) {

                if (!current.equals(target)){

                    boolean differenceFound = false;

                    ArrayList<String> j1results = safeFilterResults(result.get(current));
                    ArrayList<String> j2results = safeFilterResults(result.get(target));

//                    if (j1results.size() != j2results.size()){

                        ArrayList<String> j1exceptions = getKeyResults(j1results);
                        ArrayList<String> j2exceptions = getKeyResults(j2results);

                        /**
                         * 01 exception
                         */
                        if (j1exceptions.size() > 0 || j2exceptions.size() >0){

                            if ((j1exceptions.size() == 0 && j2exceptions.size() != 0) || (j1exceptions.size() != 0 && j2exceptions.size() == 0)){

                                if (!difference.keySet().contains(current)){
                                    difference.put(current,getExceptions(j1exceptions));
                                }
                                if (!difference.keySet().contains(target)){
                                    difference.put(target,getExceptions(j2exceptions));
                                }
                            }
//                            if (j1exceptions.size() != j2exceptions.size()){
//
//                                if (!difference.keySet().contains(current)){
//                                    difference.put(current,getExceptions(j1exceptions));
//                                }
//                                if (!difference.keySet().contains(target)){
//                                    difference.put(target,getExceptions(j2exceptions));
//                                }
//                            }else{

                                int min = j1exceptions.size() < j2exceptions.size() ? j1exceptions.size() : j2exceptions.size();
                                int bound = min >= 3 ? 3 : min;
                                for (int i = 0; i < bound; i++){

                                    if (!j1exceptions.get(i).equals(j2exceptions.get(i))){

                                        if (!difference.keySet().contains(current)){
//                                            difference.put(current,getExceptions(j1exceptions));
                                            difference.put(current,j1results);
                                        }
                                        if (!difference.keySet().contains(target)){
//                                            difference.put(current,getExceptions(j1exceptions));
                                            difference.put(target,j2results);
                                        }
                                        differenceFound = true;
                                        break;
                                    }
                                }
//                            }
                        }
                        /**
                         * 02 normal output with different values
                         */
                        if (!differenceFound){

                            int min = j1results.size() < j2results.size() ? j1results.size() : j2results.size();
                            int bound = min >= 3 ? 3 : min;
                            for (int i = 0; i < bound; i++){

                                if (!j1results.get(i).equals(j2results.get(i))){

                                    if (!difference.keySet().contains(current)){
//                                        difference.put(current,getExceptions(j1results));
                                        difference.put(current,j1results);
                                    }
                                    if (!difference.keySet().contains(target)){
//                                        difference.put(target,getExceptions(j2results));
                                        difference.put(target,j2results);

                                    }
                                    break;
                                }
                            }
                        }
                }
            }
        }
        return difference;
    }

    /**
     * unsafe
     * @param results
     * @return
     */
    @Deprecated
    public static ArrayList<String> filterResults(ArrayList<String> results){

        Iterator<String> iterator = results.iterator();

        while (iterator.hasNext()){
            String str = iterator.next();
            DTConfiguration.FILTER_WORDS.forEach(filterword -> {
                if (str.contains(filterword)){
                    iterator.remove();
                }
            });
        }
        return results;
    }

    public static ArrayList<String> safeFilterResults(ArrayList<String> results){

        Iterator<String> iterator = results.iterator();
        ArrayList<String> ret = new ArrayList<>();
        while (iterator.hasNext()){

            String str = iterator.next();
            boolean containsFlag = false;
            if (str != null){

                for (String filterWord : DTConfiguration.FILTER_WORDS) {

                    if (str.contains(filterWord)){
                        containsFlag = true;
                        break;
                    }
                }
                if (!containsFlag){
                    //TODO check windows
                    if (str.contains("Exception") || str.contains("Error")){
                        ret.add(str.replace(".", "/"));
                    } else {
                        ret.add(str);
                    }
                }
            }
        }
        return ret;
    }

    public static ArrayList<String> getKeyResultsWithRemoving(ArrayList<String> results){

        Iterator<String> iterator = results.iterator();

        while (iterator.hasNext()){
            String str = iterator.next();
            if (!(str.startsWith("java.lang.") && (str.contains("Exception") || str.contains("Error")))){
                iterator.remove();
            }
        }
        return results;
    }

    public static ArrayList<String> getKeyResults(ArrayList<String> results){

        ArrayList<String> tmp = new ArrayList<>();
        Iterator<String> iterator = results.iterator();

        while (iterator.hasNext()){
            String str = iterator.next();
            if (str.startsWith("java.lang.") && (str.contains("Exception") || str.contains("Error"))){
                tmp.add(str);
            }
        }
        return tmp;
    }

    public static ArrayList<String> getExceptions(ArrayList<String> results){

        ArrayList<String> tmp = new ArrayList<>();

        int bound = results.size() >= 3 ? 3 : results.size();
        for (int i = 0; i < bound; i++){

            tmp.add(results.get(i));
        }
        return tmp;
    }

    public static ArrayList<String> deletetmp(ArrayList<String> t){

        Iterator<String> iterator = t.iterator();

        while (iterator.hasNext()){
            String str = iterator.next();
            if (str.equals("2")){
                iterator.remove();
            }
        }
        return t;
    }
}
