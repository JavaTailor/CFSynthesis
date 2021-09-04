package JimpleMixer.blocks;

import soot.Unit;
import soot.toolkits.graph.Block;

public class ReturnStmtBlockInfo extends BlockInfo{

    private Unit returnStmt;
    private Unit newGotoStmt;

    public ReturnStmtBlockInfo(String className, Block currentBlock) {
        super(className, currentBlock);
    }

    public Unit getNewGotoStmt() {
        return newGotoStmt;
    }

    public void setNewGotoStmt(Unit newGotoStmt) {
        this.newGotoStmt = newGotoStmt;
    }

    public Unit getReturnStmt() {
        return returnStmt;
    }

    public void setReturnStmt(Unit returnStmt) {
        this.returnStmt = returnStmt;
    }

    @Override
    public String toString() {
        return "ReturnStmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + getDepensLocals() +
                "Valid Stmts: " + getValidStmts() +
                "Return Stmt: " + (newGotoStmt == null ? returnStmt : newGotoStmt);
    }
}
