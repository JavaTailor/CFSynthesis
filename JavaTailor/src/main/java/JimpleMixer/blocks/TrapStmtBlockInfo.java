package JimpleMixer.blocks;

import soot.SootClass;
import soot.Trap;
import soot.Unit;
import soot.toolkits.graph.Block;

import java.util.List;

public class TrapStmtBlockInfo extends BlockInfo{

    private Trap currentTrap;
    private BlockInfo beginBlockBox;
    private BlockInfo endBlockBox;
    private List<BlockInfo> trapBlocks;
    private Unit beginUnit;
    private Unit endUnit;
    private Unit handlerUnitBox;
    private SootClass excepiton;
    private Trap newTrap;

    public TrapStmtBlockInfo() {
        super(null, null);
    }

    public TrapStmtBlockInfo(String className, Block currentBlock) {
        super(className, currentBlock);
    }

    public BlockInfo getBeginBlockBox() {
        return beginBlockBox;
    }

    public void setBeginBlockBox(BlockInfo beginBlockBox) {
        this.beginBlockBox = beginBlockBox;
    }

    public BlockInfo getEndBlockBox() {
        return endBlockBox;
    }

    public void setEndBlockBox(BlockInfo endBlockBox) {
        this.endBlockBox = endBlockBox;
    }

    public Unit getHandlerUnitBox() {
        return handlerUnitBox;
    }

    public void setHandlerUnitBox(Unit handlerUnitBox) {
        this.handlerUnitBox = handlerUnitBox;
    }

    public SootClass getExcepiton() {
        return excepiton;
    }

    public void setExcepiton(SootClass excepiton) {
        this.excepiton = excepiton;
    }

    public List<BlockInfo> getTrapBlocks() {
        return trapBlocks;
    }

    public void setTrapBlocks(List<BlockInfo> trapBlocks) {
        this.trapBlocks = trapBlocks;
    }

    public Unit getBeginUnit() {
        return beginUnit;
    }

    public void setBeginUnit(Unit beginUnit) {
        this.beginUnit = beginUnit;
    }

    public Unit getEndUnit() {
        return endUnit;
    }

    public void setEndUnit(Unit endUnit) {
        this.endUnit = endUnit;
    }

    public Trap getCurrentTrap() {
        return currentTrap;
    }

    public void setCurrentTrap(Trap currentTrap) {
        this.currentTrap = currentTrap;
    }

    public Trap getNewTrap() {
        return newTrap;
    }

    public void setNewTrap(Trap newTrap) {
        this.newTrap = newTrap;
    }

    @Override
    public String toString() {
        return "TrapStmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + getDepensLocals() +
                "Valid Stmts: " + getValidStmts() +
                "Trap Stmt: " + (newTrap == null ? currentTrap : newTrap);
    }
}
