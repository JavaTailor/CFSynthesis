package dtjvms.analyzer;

import dtjvms.executor.CFM.JvmOutput;

import java.util.HashMap;

public class StdoutFilter extends Filter{

    @Override
    protected void doFilter(HashMap<String, JvmOutput> results) {

        System.out.println("This is StdoutFilter");
    }
}
