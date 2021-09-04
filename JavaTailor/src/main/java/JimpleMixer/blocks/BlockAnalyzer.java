package JimpleMixer.blocks;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.ConditionExprBox;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.Block;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockAnalyzer {

    public static BlockInfo initialBlock(SootClass sootClass, Block block, Body methodBody){

        BlockInfo currentBlock = null;

        List<Unit> allBlockStmts = new ArrayList<>();
        List<Local> depensLocals = new ArrayList<>();
        List<Unit> validBlockStmts = new ArrayList<>();
        Chain<Unit> methodUnits = methodBody.getUnits();
        Iterator<Unit> blockUnits = methodUnits.iterator(block.getHead(), block.getTail());
        while (blockUnits.hasNext()){
            allBlockStmts.add(blockUnits.next());
        }
        depensLocals = getDepnsLocals(allBlockStmts, methodBody.getLocals());

        for (Unit stmt : allBlockStmts) {

            if (!(stmt instanceof JIdentityStmt) ){

                if (stmt instanceof IfStmt){

                    if (currentBlock == null){
                        currentBlock = new IfStmtBlockInfo(sootClass.getName(), block);
                        ((IfStmtBlockInfo)currentBlock).setIfBranchStmt(stmt);
                        ((IfStmtBlockInfo)currentBlock).setOriginTarget(((IfStmt) stmt).getTarget());
                    }
                } else if (stmt instanceof SwitchStmt){

                    if (currentBlock == null){
                        currentBlock = new SwitchStmtBlockInfo(sootClass.getName(), block);
                        ((SwitchStmtBlockInfo)currentBlock).setSwitchBranchStmt(stmt);
                        ((SwitchStmtBlockInfo)currentBlock).setOriginTargets(((SwitchStmt) stmt).getTargets());
                        ((SwitchStmtBlockInfo)currentBlock).setOriginDefault(((SwitchStmt) stmt).getDefaultTarget());
                    }
                } else if (stmt instanceof GotoStmt){

                    if (currentBlock == null){
                        currentBlock = new StmtBlockInfo(sootClass.getName(), block);
                        ((StmtBlockInfo)currentBlock).setGotoStmt(stmt);
                    }
                } else if (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt){

                    if (currentBlock == null){

                        if (validBlockStmts.size() > 0){

                            currentBlock = new StmtBlockInfo(sootClass.getName(), block);
                        } else {
                            currentBlock = new ReturnStmtBlockInfo(sootClass.getName(), block);
                            ((ReturnStmtBlockInfo) currentBlock).setReturnStmt(stmt);
                        }
                    }
                } else {
                    validBlockStmts.add(stmt);
                }
            } else {

                if (((JIdentityStmt) stmt).getRightOpBox().getValue() instanceof JCaughtExceptionRef){

                    if (currentBlock == null){

                        currentBlock = new TrapStmtBlockInfo(sootClass.getName(), block);
                        ((TrapStmtBlockInfo) currentBlock).setHandlerUnitBox(stmt);
                    }
                }
            }
        }
        if (currentBlock == null){

            currentBlock = new StmtBlockInfo(sootClass.getName(), block);
        }
        currentBlock.setDepensLocals(depensLocals);
        currentBlock.setValidStmts(validBlockStmts);
        currentBlock.setAllStmts(allBlockStmts);

        return currentBlock;
    }


    public static BlockInfo initialLoopBlock(Body methodBody, List<BlockInfo> allBlocks, Loop loop) {

        List<BlockInfo> loopBlocks = new ArrayList<>();
        for (Stmt loopStatement : loop.getLoopStatements()) {
            for (BlockInfo block : allBlocks) {

                if (block.getAllStmts().contains(loopStatement)){
                    if (!loopBlocks.contains(block)){
                        loopBlocks.add(block);
                    }
                    break;
                }
            }
        }
        if (loopBlocks.size() >= 2 ){

            LoopStmtBlockInfo currentBlock = new LoopStmtBlockInfo(methodBody.getMethod().getDeclaringClass().getName(), null);
            currentBlock.setHeadBlock(loopBlocks.get(0));
            currentBlock.setTailBlock(loopBlocks.get(loopBlocks.size() - 1));
            currentBlock.setLoopBlocks(loopBlocks);
            return currentBlock;
        }
        return null;
    }

    public static BlockInfo initialTrapBlock(Body methodBody, List<BlockInfo> allBlocks, Trap trap) {

        TrapStmtBlockInfo trapBlock = null;

        trap.getHandlerUnit();

        for (BlockInfo block : allBlocks) {

            if (block.getAllStmts().contains(trap.getHandlerUnit())){
                trapBlock = (TrapStmtBlockInfo) block;
                break;
            }
        }
        if (trapBlock != null){

            //01 begin
            for (BlockInfo block : allBlocks) {

                if (block.getAllStmts().contains(trap.getBeginUnit())){

                    trapBlock.setBeginUnit(trap.getBeginUnit());
                    trapBlock.setBeginBlockBox(block);
                    continue;
                }
                if (block.getAllStmts().contains(trap.getEndUnit())){

                    trapBlock.setEndUnit(trap.getEndUnit());
                    trapBlock.setEndBlockBox(block);
                    continue;
                }
            }
            trapBlock.setExcepiton(trap.getException());
            trapBlock.setCurrentTrap(trap);
            if (trapBlock.getBeginBlockBox() != null){

                // get all trap blocks
                List<BlockInfo> trapBlocks = new ArrayList<>();
                getAllTrapBlocks(trapBlocks, allBlocks, trapBlock.getBeginBlockBox(), trapBlock.getEndBlockBox());
                trapBlock.setTrapBlocks(trapBlocks);
            }
        }
        return trapBlock;
    }

    public static void getAllTrapBlocks(List<BlockInfo> trapBlocks,
                                        List<BlockInfo> allBlocks,
                                        BlockInfo currentBlock,
                                        BlockInfo endBlock){

        if (trapBlocks.contains(currentBlock)){
            return;
        } else {
            trapBlocks.add(currentBlock);
        }
        if (currentBlock == endBlock){
            return;
        }
        List<BlockInfo> succesBlocks = getSuccesBlocks(allBlocks, currentBlock);
        for (BlockInfo succesBlock : succesBlocks) {
            getAllTrapBlocks(trapBlocks, allBlocks, succesBlock, endBlock);
        }
    }

    public static List<BlockInfo> getSuccesBlocks(List<BlockInfo> allBlocks, BlockInfo currentBlock){

        List<Block> succsBlocks = currentBlock.getCurrentBlock().getSuccs();
        List<BlockInfo> rets = new ArrayList<>();
        for (Block succsBlock : succsBlocks) {
            for (BlockInfo blockInfo : allBlocks) {
                if (blockInfo.getCurrentBlock() == succsBlock){
                    rets.add(blockInfo);
                }
            }
        }
        return rets;
    }

    public static List<Local> getDepnsLocals(List<Unit> allBlockStmts, Chain<Local> allDependsLocals){

        List<Local> depensLocals = new ArrayList<>();

        for (Unit unit : allBlockStmts) {

            if (!(unit instanceof JIdentityStmt)){

                unit.getUseAndDefBoxes().forEach(valueBox -> {

                    if (valueBox instanceof ConditionExprBox){

                        valueBox.getValue().getUseBoxes().forEach(realValueBox -> {
                            allDependsLocals.forEach(local -> {
                                if (local.getName().equals(realValueBox.getValue().toString()) && !depensLocals.contains(local)){
                                    depensLocals.add(local);
                                }
                            });
                        });
                    } else {

                        allDependsLocals.forEach(local -> {
                            if (local.getName().equals(valueBox.getValue().toString()) && !depensLocals.contains(local)){
                                depensLocals.add(local);
                            }
                        });
                    }
                });
            } else {

                JIdentityStmt stmt = (JIdentityStmt) unit;
                if (stmt.getRightOpBox().getValue() instanceof JCaughtExceptionRef){

                    if (!depensLocals.contains(stmt.getLeftOp())){
                        depensLocals.add((Local) stmt.getLeftOp());
                    }
                } else {

                    unit.getUseAndDefBoxes().forEach(valueBox -> {

                        if (valueBox instanceof JimpleLocalBox){

                            allDependsLocals.forEach(local -> {
                                if (local.getName().equals(valueBox.getValue().toString()) && !depensLocals.contains(local)){
                                    depensLocals.add(local);
                                }
                            });
                        }
                    });
                }
            }
        }
        return depensLocals;
    }
}
