package JimpleMixer.blocks;

import soot.Local;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.util.Chain;

public class StmtBlockInfo extends BlockInfo{

    private Unit gotoStmt;
    private BlockInfo targetBlockInfo;
    private Unit newGotoStmt;

    public StmtBlockInfo(String className, Block currentBlock) {
        super(className, currentBlock);
    }

    public BlockInfo getTargetBlockInfo() {
        return targetBlockInfo;
    }

    public void setTargetBlockInfo(BlockInfo targetBlockInfo) {
        this.targetBlockInfo = targetBlockInfo;
    }

    public Unit getGotoStmt() {
        return gotoStmt;
    }

    public void setGotoStmt(Unit gotoStmt) {
        this.gotoStmt = gotoStmt;
    }

    public Unit getNewGotoStmt() {
        return newGotoStmt;
    }

    public void setNewGotoStmt(Unit newGotoStmt) {
        this.newGotoStmt = newGotoStmt;
    }

    @Override
    public String toString() {
        return "StmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + getDepensLocals() +
                "Valid Stmts: " + getValidStmts() +
                "Goto Stmt: " + (newGotoStmt == null ? gotoStmt : newGotoStmt);
    }
}
