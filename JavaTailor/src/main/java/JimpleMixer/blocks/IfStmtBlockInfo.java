package JimpleMixer.blocks;

import soot.Local;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.toolkits.graph.Block;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class IfStmtBlockInfo extends BlockInfo{

    private Unit ifBranchStmt;
    private Unit originTarget;
    private Unit newIfBranchStmt;
    private boolean needReCreate = false;

    private HashMap<Unit, ArrayList<Unit>> gotoMap = new HashMap<>();

    public IfStmtBlockInfo(String className, Block currentBlock) {
        super(className, currentBlock);
    }

    public void addGotoMap(Unit gotoStmt, Unit newIfStmt){

        if (!gotoMap.containsKey(newIfStmt)){
            ArrayList<Unit> gotos = new ArrayList<>();
            gotos.add(gotoStmt);
            gotoMap.put(newIfStmt, gotos);
        } else {

            gotoMap.get(newIfStmt).add(gotoStmt);
        }
    }

    public boolean hasGotoMap(Unit newIfStmt){

        if (!gotoMap.keySet().contains(newIfStmt)){
            return false;
        }
        if (gotoMap.get(newIfStmt).size() > 0){
            gotoMap.remove(newIfStmt);
            return true;
        }
        return false;
    }

    public Unit getIfBranchStmt() {
        return ifBranchStmt;
    }

    public void setIfBranchStmt(Unit ifBranchStmt) {
        this.ifBranchStmt = ifBranchStmt;
    }

    public Unit getOriginTarget() {
        return originTarget;
    }

    public void setOriginTarget(Unit originTarget) {
        this.originTarget = originTarget;
        if (originTarget instanceof ReturnStmt ||
                originTarget instanceof ReturnVoidStmt ||
                (originTarget instanceof GotoStmt)){
            needReCreate = true;
        }
        for (Block succ : getCurrentBlock().getSuccs()) {

            if (succ.getHead() == originTarget){
                if (succ.getHead() instanceof ReturnStmt ||
                        succ.getHead() instanceof ReturnVoidStmt ||
                        (succ.getHead() instanceof GotoStmt)){
                    needReCreate = true;
                }
            }
        }

        if (needReCreate){
            JIfStmt ifStmt = new JIfStmt( ((JIfStmt)ifBranchStmt).getCondition(), Jimple.v().newNopStmt());
            this.newIfBranchStmt = ifStmt;
        }
    }

    public boolean isNeedReCreate() {
        return needReCreate;
    }

    public void setNeedReCreate(boolean needReCreate) {
        this.needReCreate = needReCreate;
    }

    public Unit getNewIfBranchStmt() {
        return newIfBranchStmt;
    }

    public void setNewIfBranchStmt(Unit newIfBranchStmt) {
        this.newIfBranchStmt = newIfBranchStmt;
    }

    @Override
    public String toString() {
        return "IfStmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + getDepensLocals() +
                "Valid Stmts: " + (getValidStmts() == null ? "null" : getValidStmts()) +
                "IfStmt: " + (newIfBranchStmt == null ? ifBranchStmt : newIfBranchStmt);
    }
}
