package vmoptions;

import java.util.HashSet;
import java.util.Set;

public class Option {

    private String name;
    private Set<String> conflict;
    private Set<String> depens;

    public Option(String name) {
        this.name = name;
        conflict = new HashSet<>();
        depens = new HashSet<>();
    }

    public void addConflict(String conflictOption){

        if (conflict == null){
            conflict = new HashSet<>();
        }
        conflict.add(conflictOption);
    }

    public void addDepens(String dependOption){

        if (depens == null){
            depens = new HashSet<>();
        }
        depens.add(dependOption);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getConflict() {
        return conflict;
    }

    public void setConflict(Set<String> conflict) {
        this.conflict = conflict;
    }

    public Set<String> getDepens() {
        return depens;
    }

    public void setDepens(Set<String> depens) {
        this.depens = depens;
    }
}
