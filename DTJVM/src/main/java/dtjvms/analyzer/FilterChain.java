package dtjvms.analyzer;

import dtjvms.executor.CFM.JvmOutput;

import java.util.HashMap;

public class FilterChain {

    public Filter headFilter;
    public Filter currentFilter;

    public FilterChain() {
    }

    public void addFilter(Filter filter){

        if (headFilter == null){
            headFilter = filter;
            currentFilter = filter;
        } else {
            currentFilter.setNextFilter(filter);
            currentFilter = filter;
        }
    }

    public void startFilter(HashMap<String, JvmOutput> results){

        currentFilter = headFilter;

        while (currentFilter !=null){

            currentFilter.doFilter(results);
            currentFilter = currentFilter.nextFilter;
        }
    }
}
