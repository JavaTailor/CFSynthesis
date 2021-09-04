package vmoptions;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class VMOptions {

    private String vmname;
    private List<Option> NonStandard;
    private List<Option> Advanced;

    public VMOptions(String name) {
        this.vmname = name;
        NonStandard = new ArrayList<>();
        Advanced = new ArrayList<>();
    }
    public void addNonStandardOption(Option option){

        NonStandard.add(option);
    }

    public void addAdvancedOption(Option option){

        Advanced.add(option);
    }

    public String getVmname() {
        return vmname;
    }

    public void setVmname(String vmname) {
        this.vmname = vmname;
    }

    public List<Option> getNonStandard() {
        return NonStandard;
    }

    public void setNonStandard(List<Option> nonStandard) {
        NonStandard = nonStandard;
    }

    public List<Option> getAdvanced() {
        return Advanced;
    }

    public void setAdvanced(List<Option> advanced) {
        Advanced = advanced;
    }
}
