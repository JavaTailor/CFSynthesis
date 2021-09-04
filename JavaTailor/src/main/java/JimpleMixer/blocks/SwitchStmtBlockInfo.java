package JimpleMixer.blocks;

import soot.Local;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.util.Chain;

import java.util.List;

public class SwitchStmtBlockInfo extends BlockInfo{

    private Unit switchBranchStmt;
    private List<Unit> originTargets;
    private Unit originDefault;
    private Unit newSwitchBranchStmt;
    private boolean needReCreate = false;

    public SwitchStmtBlockInfo(String className, Block currentBlock) {
        super(className, currentBlock);
    }

    public Unit getSwitchBranchStmt() {
        return switchBranchStmt;
    }

    public void setSwitchBranchStmt(Unit switchBranchStmt) {
        this.switchBranchStmt = switchBranchStmt;
    }

    public List<Unit> getOriginTargets() {
        return originTargets;
    }

    public void setOriginTargets(List<Unit> originTargets) {
        this.originTargets = originTargets;
    }

    public Unit getOriginDefault() {
        return originDefault;
    }

    public void setOriginDefault(Unit originDefault) {
        this.originDefault = originDefault;
    }

    public Unit getNewSwitchBranchStmt() {
        return newSwitchBranchStmt;
    }

    public void setNewSwitchBranchStmt(Unit newSwitchBranchStmt) {
        this.newSwitchBranchStmt = newSwitchBranchStmt;
    }

    public boolean isNeedReCreate() {
        return needReCreate;
    }

    public void setNeedReCreate(boolean needReCreate) {
        this.needReCreate = needReCreate;
    }

    @Override
    public String toString() {
        return "SwitchStmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + getDepensLocals() +
                "Valid Stmts: " + getValidStmts() +
                "Switch Stmt: " + (newSwitchBranchStmt == null ? switchBranchStmt : newSwitchBranchStmt);
    }
}
