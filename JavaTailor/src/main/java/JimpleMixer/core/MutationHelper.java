package JimpleMixer.core;

import JimpleMixer.blocks.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.Block;
import soot.toolkits.scalar.InitAnalysis;
import soot.util.Chain;

import java.io.PrintStream;
import java.util.*;

public class MutationHelper {

    public static Unit lastVariableDef = null;

    public static void insertBlockToMethod(SootClass seedClass, Body targetMethodBody, Unit targetUnit, List<BlockInfo> mutants, BlockInfo mutant){

//        Unit firstUnitInOriginMethod = getFirstValidStmtInUnits(targetMethodBody);
//        if (firstUnitInOriginMethod != null){
//            updateMethodLocalVariables(targetMethodBody, firstUnitInOriginMethod, mutant);
//        } else {
//            updateMethodLocalVariables(targetMethodBody, targetUnit, mutant);
//        }
//        lastVariableDef = targetMethodBody.getUnits().getPredOf(targetUnit);
        updateMethodLocalVariables(targetMethodBody, targetUnit, mutant);

//        for (Local depensLocal : mutant.getDepensLocals()) {
//            targetMethodBody.getLocals().add(depensLocal);
//        }
        if (mutant instanceof StmtBlockInfo){

            insertStmtBlock(targetMethodBody, targetUnit, mutant);
        } else if (mutant instanceof IfStmtBlockInfo) {

            insertIfStmtBlock(targetMethodBody, targetUnit, mutants, mutant);
        } else if (mutant instanceof SwitchStmtBlockInfo) {

            insertSwitchStmtBlock(targetMethodBody, targetUnit, mutants, mutant);
        } else if (mutant instanceof LoopStmtBlockInfo) {

            insertLoopStmtBlock(targetMethodBody, targetUnit, mutants, mutant);
        } else if (mutant instanceof TrapStmtBlockInfo) {

            insertTrapStmtBlock(targetMethodBody, targetUnit, mutants, mutant);
        }
    }

    public static void insertStmtBlock(Body targetMethodBody, Unit targetUnit, BlockInfo mutant){

        if (mutant.getValidStmts().size() > 0){

            for (Unit validStmt : mutant.getValidStmts()) {
                if (targetMethodBody.getUnits().contains(validStmt)) {
                    return;
                } else {
                    targetMethodBody.getUnits().insertBeforeNoRedirect(validStmt, targetUnit);
                }
            }
        } else {

            if (((StmtBlockInfo) mutant).getGotoStmt() != null){

                List<Unit> candidates = getSuccesOf(targetMethodBody, targetUnit);
                Random random = new Random(System.currentTimeMillis());
                IfStmt ifStmt = null;
                if (candidates.size() > 0){
                    Unit gotoTarget = candidates.get(random.nextInt(candidates.size()));
                    ConditionExpr ifcondition = Jimple.v().newLeExpr(PrimitiveProvider.next(IntType.v()), PrimitiveProvider.next(IntType.v()));  // <= then skip switch
                    ifStmt = Jimple.v().newIfStmt(ifcondition, gotoTarget);
                } else {
                    ConditionExpr ifcondition = Jimple.v().newLeExpr(PrimitiveProvider.next(IntType.v()), PrimitiveProvider.next(IntType.v()));  // <= then skip switch
                    ifStmt = Jimple.v().newIfStmt(ifcondition, targetUnit);
                }
                targetMethodBody.getUnits().insertBeforeNoRedirect(ifStmt,targetUnit);
                /**
                 * go to a ramdon unit
                 */
//                List<Unit> candidates = getSuccesOf(targetMethodBody, targetUnit);
//                Random random = new Random(System.currentTimeMillis());
//                GotoStmt newGotoStmt = null;
//                if (candidates.size() > 0){
//                    Unit gotoTarget = candidates.get(random.nextInt(candidates.size()));
//                    newGotoStmt = new JGotoStmt(gotoTarget);
//                } else {
//                    newGotoStmt = new JGotoStmt(targetUnit);
//                }
//                targetMethodBody.getUnits().insertBeforeNoRedirect(newGotoStmt,targetUnit);

//                Unit succs = targetMethodBody.getUnits().getSuccOf(targetUnit);
//                GotoStmt newGotoStmt = new JGotoStmt(succs);
//                targetMethodBody.getUnits().insertBeforeNoRedirect(newGotoStmt,targetUnit);
            }
        }
    }

    public static List<Unit> getSuccesOf(Body methodBody, Unit targetUnit){

        List<Unit> tmp = new ArrayList<>();
        while (methodBody.getUnits().getSuccOf(targetUnit) != null){
            targetUnit = methodBody.getUnits().getSuccOf(targetUnit);
            tmp.add(targetUnit);
        }
        return tmp;
    }

    public static void insertIfStmtBlock(Body targetMethodBody,Unit targetUnit,List<BlockInfo> mutants, BlockInfo mutant){

        IfStmtBlockInfo currentMutant = (IfStmtBlockInfo) mutant;
        List<BlockInfo> succesBlocks = getSuccesBlocks(mutants, mutant);

        for (BlockInfo succesBlock : succesBlocks) {

            updateMethodLocalVariables(targetMethodBody, targetUnit, succesBlock);
//            for (Local precisLocal : succesBlock.getDepensLocals()) {
//                if (!targetMethodBody.getLocals().contains(precisLocal)){
//                    targetMethodBody.getLocals().add(precisLocal);
//                }
//            }
        }
        if (mutant.getValidStmts().size() > 0){

            for (Unit validStmt : mutant.getValidStmts()) {
                if (targetMethodBody.getUnits().contains(validStmt)) {
                    return;
                } else {
                    targetMethodBody.getUnits().insertBeforeNoRedirect(validStmt, targetUnit);
                }
            }
        }
        // 02 insert if stmt
        if (!targetMethodBody.getUnits().contains(currentMutant.getIfBranchStmt())) {

            if ( ! (currentMutant.getNewIfBranchStmt() != null &&
                    targetMethodBody.getUnits().contains(currentMutant.getNewIfBranchStmt()) )){

                IfStmt ifStmt = (IfStmt) currentMutant.getIfBranchStmt();

                if (ifIfBlockContainsReturnTarget(succesBlocks, mutant)){

                    if (currentMutant.getNewIfBranchStmt() != null){

                        if (currentMutant.hasGotoMap(currentMutant.getNewIfBranchStmt())){
                            ifStmt = (IfStmt) currentMutant.getNewIfBranchStmt();
                            currentMutant.setNewIfBranchStmt(ifStmt);
                            currentMutant.setNeedReCreate(true);
                            ifStmt.setTarget(targetUnit);
                            targetMethodBody.getUnits().insertBeforeNoRedirect(ifStmt, targetUnit);
                        } else {
                            ifStmt = new JIfStmt( ((JIfStmt)currentMutant.getIfBranchStmt()).getCondition(), targetUnit);
                            currentMutant.setNewIfBranchStmt(ifStmt);
                            currentMutant.setNeedReCreate(true);
                            targetMethodBody.getUnits().insertBeforeNoRedirect(ifStmt, targetUnit);
                        }
                    } else {
                        ifStmt = new JIfStmt( ((JIfStmt)currentMutant.getIfBranchStmt()).getCondition(), targetUnit);
                        currentMutant.setNewIfBranchStmt(ifStmt);
                        currentMutant.setNeedReCreate(true);
                        targetMethodBody.getUnits().insertBeforeNoRedirect(ifStmt, targetUnit);
                    }
                } else {
                    targetMethodBody.getUnits().insertBeforeNoRedirect(ifStmt, targetUnit);
                }

                for (BlockInfo succesBlock : succesBlocks) {

                    succesBlock.chosenTimesIncrease();
                    insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, succesBlock);
                }
            }
        }
    }

    public static void insertSwitchStmtBlock(Body targetMethodBody, Unit targetUnit, List<BlockInfo> mutants, BlockInfo mutant){

        List<BlockInfo> succesBlocks = getSuccesBlocks(mutants, mutant);

        for (BlockInfo succesBlock : succesBlocks) {

            updateMethodLocalVariables(targetMethodBody, targetUnit, succesBlock);
//            for (Local precisLocal : succesBlock.getDepensLocals()) {
//                if (!targetMethodBody.getLocals().contains(precisLocal)){
//                    targetMethodBody.getLocals().add(precisLocal);
//                }
//            }
        }
        /**
         * 01 insert switch stmt
         */
        if (mutant.getValidStmts().size() > 0){

            for (Unit validStmt : mutant.getValidStmts()) {
                if (targetMethodBody.getUnits().contains(validStmt)) {
                    return;
                } else {
                    targetMethodBody.getUnits().insertBeforeNoRedirect(validStmt, targetUnit);
                }
            }
//            targetMethodBody.getUnits().insertBefore(mutant.getValidStmts(), targetUnit);
        }

        Unit switchUnit =  ((SwitchStmtBlockInfo)mutant).getSwitchBranchStmt();
        if (switchUnit instanceof JLookupSwitchStmt){

            JLookupSwitchStmt switchStmt = (JLookupSwitchStmt)switchUnit;
            if (ifSwitchBlockContainsReturnTarget(succesBlocks,mutant)){
                switchStmt = new JLookupSwitchStmt(switchStmt.getKey(), switchStmt.getLookupValues(), switchStmt.getTargets(), switchStmt.getDefaultTarget());
                ((SwitchStmtBlockInfo) mutant).setNewSwitchBranchStmt(switchStmt);
            }
            if (targetMethodBody.getUnits().contains(switchStmt)){
                return;
            }
            targetMethodBody.getUnits().insertBeforeNoRedirect(switchStmt, targetUnit);
        } else {

            JTableSwitchStmt switchStmt = (JTableSwitchStmt)switchUnit;
            if (ifSwitchBlockContainsReturnTarget(succesBlocks, mutant)){
                switchStmt = new JTableSwitchStmt(switchStmt.getKey(),
                        switchStmt.getLowIndex(),
                        switchStmt.getHighIndex(),
                        switchStmt.getTargets(),
                        switchStmt.getDefaultTarget());
                ((SwitchStmtBlockInfo) mutant).setNewSwitchBranchStmt(switchStmt);
            }
            if (targetMethodBody.getUnits().contains(switchStmt)){
                return;
            }
            targetMethodBody.getUnits().insertBeforeNoRedirect(switchStmt, targetUnit);
        }

        /**
         * 02 insert label codes
         */
        for (BlockInfo succesBlock : succesBlocks) {

            succesBlock.chosenTimesIncrease();
            insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, succesBlock);
        }
    }

    public static void insertLoopStmtBlock(Body targetMethodBody, Unit targetUnit, List<BlockInfo> mutants, BlockInfo mutant){

        List<BlockInfo> loopBlocks = ((LoopStmtBlockInfo) mutant).getLoopBlocks();
        for (BlockInfo succesBlock : loopBlocks) {

            updateMethodLocalVariables(targetMethodBody, targetUnit, succesBlock);

//            for (Local precisLocal : succesBlock.getDepensLocals()) {
//                if (!targetMethodBody.getLocals().contains(precisLocal)){
//                    targetMethodBody.getLocals().add(precisLocal);
//                }
//            }
        }
        for (BlockInfo succesBlock : loopBlocks) {

            succesBlock.chosenTimesIncrease();
            insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, succesBlock);
        }
    }

    public static void insertTrapStmtBlock(Body targetMethodBody, Unit targetUnit, List<BlockInfo> mutants, BlockInfo mutant) {

        List<BlockInfo> trapBlocks = ((TrapStmtBlockInfo) mutant).getTrapBlocks();
        for (BlockInfo succesBlock : trapBlocks) {

            updateMethodLocalVariables(targetMethodBody, targetUnit, succesBlock);
//            for (Local precisLocal : succesBlock.getDepensLocals()) {
//                if (!targetMethodBody.getLocals().contains(precisLocal)){
//                    targetMethodBody.getLocals().add(precisLocal);
//                }
//            }
        }
        for (BlockInfo trapBlock : trapBlocks) {
            trapBlock.chosenTimesIncrease();
            insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, trapBlock);
        }

        if (((TrapStmtBlockInfo) mutant).getEndBlockBox() instanceof TrapStmtBlockInfo || !trapBlocks.contains(((TrapStmtBlockInfo) mutant).getEndBlockBox())){
            updateMethodLocalVariables(targetMethodBody, targetUnit, ((TrapStmtBlockInfo) mutant).getEndBlockBox());

            ((TrapStmtBlockInfo) mutant).getEndBlockBox().chosenTimesIncrease();
            insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, ((TrapStmtBlockInfo) mutant).getEndBlockBox());
        }

        //insert trap block itself
        insertSuccsBlockInfo(mutant, targetMethodBody, targetUnit, mutants, mutant);
    }

    public static boolean ifSwitchBlockContainsReturnTarget(List<BlockInfo> succesBlocks, BlockInfo mutant){

        //check if the if succs needs recreate
        for (BlockInfo succesBlock : succesBlocks) {
            if (succesBlock instanceof IfStmtBlockInfo && ((IfStmtBlockInfo) succesBlock).isNeedReCreate()){
                ((SwitchStmtBlockInfo)mutant).setNeedReCreate(true);
                return true;
            }
        }
        //check  return block
        for (Unit originTarget : ((SwitchStmtBlockInfo) mutant).getOriginTargets()) {

            if (originTarget instanceof ReturnStmt ||
                    originTarget instanceof ReturnVoidStmt){
                ((SwitchStmtBlockInfo)mutant).setNeedReCreate(true);
                return true;
            }
        }
        return false;
    }

    public static boolean ifIfBlockContainsReturnTarget(List<BlockInfo> succesBlocks, BlockInfo mutant){

        Unit originTarget = ((IfStmtBlockInfo) mutant).getOriginTarget();

        // check if-target Return
        if (originTarget instanceof ReturnStmt ||
                originTarget instanceof ReturnVoidStmt ||
                (originTarget instanceof GotoStmt)){
            ((IfStmtBlockInfo) mutant).setNeedReCreate(true);
            return true;
        }
//        //check else only have a return stmt
//        for (BlockInfo succesBlock : succesBlocks) {
//            if (succesBlock instanceof ReturnStmtBlockInfo){
//                ((IfStmtBlockInfo) mutant).setNeedReCreate(true);
//                return true;
//            }
//        }
        //check else only have a goto stmt
        for (BlockInfo succesBlock : succesBlocks) {
            if (succesBlock instanceof StmtBlockInfo &&
                    succesBlock.getValidStmts().size() <=0 &&
                    ((StmtBlockInfo) succesBlock).getGotoStmt() != null &&
                    ((StmtBlockInfo) succesBlock).getGotoStmt() == originTarget){
                ((IfStmtBlockInfo) mutant).setNeedReCreate(true);
                return true;
            }
        }
        //check if has a block which only have a if stmt
        for (BlockInfo succesBlock : succesBlocks) {

            if (succesBlock instanceof IfStmtBlockInfo && ((IfStmtBlockInfo) succesBlock).getIfBranchStmt() == originTarget){

                if (((IfStmtBlockInfo) succesBlock).isNeedReCreate()){
                    ((IfStmtBlockInfo) mutant).setNeedReCreate(true);
                    return true;
                }
            }
//                if (succesBlock instanceof IfStmtBlockInfo && ((IfStmtBlockInfo) succesBlock).isNeedReCreate()){
//                    ((IfStmtBlockInfo) mutant).setNeedReCreate(true);
//                    return true;
//                }
        }
        return false;
    }

    public static void insertSuccsBlockInfo(BlockInfo invokeBlock, Body targetMethodBody, Unit targetUnit,List<BlockInfo> mutants, BlockInfo currentBlock){

        //设置IF -- target
        if (currentBlock instanceof StmtBlockInfo){

            if(currentBlock.getValidStmts().size()  > 0) {

                for (Unit validStmt : currentBlock.getValidStmts()) {
                    if (targetMethodBody.getUnits().contains(validStmt)) {
                        return;
                    } else {
                        targetMethodBody.getUnits().insertBeforeNoRedirect(validStmt, targetUnit);
                    }
                }
            }
            if ( ((StmtBlockInfo)currentBlock).getGotoStmt() != null){

                GotoStmt newGotoStmt = new JGotoStmt(targetUnit);
                targetMethodBody.getUnits().insertBeforeNoRedirect(newGotoStmt, targetUnit);

                if (invokeBlock instanceof IfStmtBlockInfo ){

                    if (currentBlock.getValidStmts().size() <= 0 ) {

                        if(((IfStmtBlockInfo) invokeBlock).getOriginTarget() instanceof GotoStmt){
                            ((IfStmt)((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt()).setTarget(newGotoStmt);
                        }
                    } else {

                        if( ! (((IfStmtBlockInfo) invokeBlock).getOriginTarget() instanceof GotoStmt) &&
                                ((IfStmtBlockInfo) invokeBlock).isNeedReCreate() &&
                                ((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt() != null){
                            ((IfStmt)((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt()).setTarget(((IfStmtBlockInfo) invokeBlock).getOriginTarget());
                        }

                        GotoStmt originGotoStmt = (GotoStmt) ((StmtBlockInfo) currentBlock).getGotoStmt();
                        if (originGotoStmt.getTarget() instanceof IfStmt &&
                                originGotoStmt.getTarget() == ((IfStmtBlockInfo) invokeBlock).getIfBranchStmt() ){

                            if (((IfStmtBlockInfo) invokeBlock).isNeedReCreate() &&
                                    ((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt() != null){

                                newGotoStmt.setTarget(((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt());
                            } else {

                                newGotoStmt.setTarget(((IfStmtBlockInfo) invokeBlock).getIfBranchStmt());
                            }
                        } else {

                            if (invokeBlock.getValidStmts().size() > 0 &&
                                    originGotoStmt.getTarget() == invokeBlock.getValidStmts().get(0)){
                                newGotoStmt.setTarget(originGotoStmt.getTarget());
                            }
                        }
                    }

                }
                else if (invokeBlock instanceof SwitchStmtBlockInfo &&
                        currentBlock.getValidStmts().size() <= 0){

                    updateSwitchLabels(invokeBlock, ((StmtBlockInfo)currentBlock).getGotoStmt() ,newGotoStmt);
                }
                else if (invokeBlock instanceof LoopStmtBlockInfo){

                    Unit originGoToUnit = ((StmtBlockInfo) currentBlock).getGotoStmt();
                    GotoStmt gotoStmt = (GotoStmt) originGoToUnit;

                    if (gotoStmt.getTarget() instanceof IfStmt){

                        IfStmtBlockInfo ifStmtBlockInfo = null;
                        if (((LoopStmtBlockInfo) invokeBlock).getHeadBlock() instanceof IfStmtBlockInfo){
                            ifStmtBlockInfo = (IfStmtBlockInfo) ((LoopStmtBlockInfo) invokeBlock).getHeadBlock();
                        }
                        if (((LoopStmtBlockInfo) invokeBlock).getTailBlock() instanceof IfStmtBlockInfo){
                            ifStmtBlockInfo = (IfStmtBlockInfo) ((LoopStmtBlockInfo) invokeBlock).getTailBlock();
                        }
//                        IfStmtBlockInfo ifStmtBlockInfo = (IfStmtBlockInfo) ((LoopStmtBlockInfo) invokeBlock).getHeadBlock();
                        if (ifStmtBlockInfo != null && ifStmtBlockInfo.isNeedReCreate()){
                            newGotoStmt.setTarget(ifStmtBlockInfo.getNewIfBranchStmt());
                            ifStmtBlockInfo.addGotoMap(newGotoStmt, ifStmtBlockInfo.getNewIfBranchStmt());
                        }else{
                            newGotoStmt.setTarget(gotoStmt.getTarget());
                        }
                    } else {
                        newGotoStmt.setTarget(targetUnit);
                    }
                }
                ((StmtBlockInfo) currentBlock).setNewGotoStmt(newGotoStmt);
            }

        }
        else if (currentBlock instanceof IfStmtBlockInfo){

            insertIfStmtBlock(targetMethodBody, targetUnit, mutants, currentBlock);
            if (invokeBlock instanceof SwitchStmtBlockInfo &&
                    ((IfStmtBlockInfo) currentBlock).isNeedReCreate()){

                updateSwitchLabels(invokeBlock, ((IfStmtBlockInfo) currentBlock).getIfBranchStmt(),((IfStmtBlockInfo) currentBlock).getNewIfBranchStmt());
            }
            if (invokeBlock instanceof IfStmtBlockInfo &&
                    ((IfStmtBlockInfo) currentBlock).isNeedReCreate()){
                updateIfLabels(invokeBlock, currentBlock);
            }
        }
        else if (currentBlock instanceof SwitchStmtBlockInfo){

            insertSwitchStmtBlock(targetMethodBody, targetUnit, mutants, currentBlock);
        }
        else if (currentBlock instanceof TrapStmtBlockInfo) {

            if (invokeBlock instanceof TrapStmtBlockInfo){

                if(invokeBlock == currentBlock) {

                    if (targetMethodBody.getUnits().contains(((TrapStmtBlockInfo) currentBlock).getHandlerUnitBox())){
                        return;
                    } else {
                        targetMethodBody.getUnits().insertBeforeNoRedirect(((TrapStmtBlockInfo) currentBlock).getHandlerUnitBox(), targetUnit);
                        if (currentBlock.getValidStmts().size() > 0){

                            for (Unit validStmt : currentBlock.getValidStmts()) {
                                if (targetMethodBody.getUnits().contains(validStmt)) {
                                    return;
                                } else {
                                    targetMethodBody.getUnits().insertBeforeNoRedirect(validStmt, targetUnit);
                                }
                            }
                        }
                        JTrap newTrap = (JTrap) ((TrapStmtBlockInfo) currentBlock).getCurrentTrap().clone();
                        if (((TrapStmtBlockInfo) currentBlock).getBeginBlockBox() instanceof StmtBlockInfo &&
                                ((StmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).getValidStmts().size() <= 0 &&
                                ((StmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).getNewGotoStmt() != null){
                            newTrap.setBeginUnit(((StmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).getNewGotoStmt());
                        }
                        if (((TrapStmtBlockInfo) currentBlock).getBeginBlockBox() instanceof IfStmtBlockInfo &&
                                ((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).isNeedReCreate() &&
                                ((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).getNewIfBranchStmt() != null){
                            newTrap.setBeginUnit(((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getBeginBlockBox()).getNewIfBranchStmt());
                        }

                        if (((TrapStmtBlockInfo) currentBlock).getEndBlockBox() instanceof StmtBlockInfo &&
                                ((StmtBlockInfo) ((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getValidStmts().size() <= 0 &&
                                ((StmtBlockInfo) ((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewGotoStmt() != null){
                            newTrap.setEndUnit(((StmtBlockInfo) ((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewGotoStmt());
                        }
                        if (((TrapStmtBlockInfo) currentBlock).getEndBlockBox() instanceof ReturnStmtBlockInfo &&
                                ((ReturnStmtBlockInfo) ((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewGotoStmt() != null){
                            newTrap.setEndUnit(((ReturnStmtBlockInfo) ((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewGotoStmt());
                        }
                        if (((TrapStmtBlockInfo) currentBlock).getEndBlockBox() instanceof IfStmtBlockInfo &&
                                ((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).isNeedReCreate() &&
                                ((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewIfBranchStmt() != null){
                            newTrap.setEndUnit(((IfStmtBlockInfo)((TrapStmtBlockInfo) currentBlock).getEndBlockBox()).getNewIfBranchStmt());
                        }
                        targetMethodBody.getTraps().add(newTrap);
                        ((TrapStmtBlockInfo) currentBlock).setNewTrap(newTrap);
                    }
                } else {
                    insertTrapStmtBlock(targetMethodBody, targetUnit, mutants, currentBlock);
                }
            }
        }
        else if (currentBlock instanceof ReturnStmtBlockInfo){

            GotoStmt newGotoStmt = new JGotoStmt(targetUnit);
            ((ReturnStmtBlockInfo) currentBlock).setNewGotoStmt(newGotoStmt);
            targetMethodBody.getUnits().insertBeforeNoRedirect(newGotoStmt, targetUnit);
            if (invokeBlock instanceof IfStmtBlockInfo){

                if (((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt() != null
                        && ((ReturnStmtBlockInfo) currentBlock).getReturnStmt() == ((IfStmtBlockInfo) invokeBlock).getOriginTarget()){
                    ((IfStmt)((IfStmtBlockInfo) invokeBlock).getNewIfBranchStmt()).setTarget(targetUnit);
                }
            } else if (invokeBlock instanceof SwitchStmtBlockInfo){

                updateSwitchLabels(invokeBlock,((ReturnStmtBlockInfo)currentBlock).getReturnStmt() ,newGotoStmt);
            }
            else if (invokeBlock instanceof TrapStmtBlockInfo){
                //do nothing
            }
            else {
                throw new RuntimeException("Unrecognized BlockInfo: " + currentBlock + ", insertSuccsBlockInfo-1");
            }
        }
        else if (currentBlock instanceof LoopStmtBlockInfo){
            //
            insertLoopStmtBlock(targetMethodBody, targetUnit, mutants, currentBlock);
        }
        else{
            throw new RuntimeException("Unrecognized BlockInfo: " + currentBlock + ", insertSuccsBlockInfo-2");
        }
    }

    public static void updateSwitchLabels(BlockInfo switchBlock, Unit oldStmt, Unit newStmt ){

        Unit switchUnit = ((SwitchStmtBlockInfo)switchBlock).getSwitchBranchStmt();

        if (switchUnit instanceof JLookupSwitchStmt){

            JLookupSwitchStmt switchStmt = (JLookupSwitchStmt)switchUnit;

            if (((SwitchStmtBlockInfo) switchBlock).isNeedReCreate() &&
                    ((SwitchStmtBlockInfo)switchBlock).getNewSwitchBranchStmt() != null){
                switchStmt = (JLookupSwitchStmt) ((SwitchStmtBlockInfo)switchBlock).getNewSwitchBranchStmt();
            }
            List<Unit> originTargets = ((SwitchStmtBlockInfo) switchBlock).getOriginTargets();

            // targets
            for (int i = 0; i < originTargets.size(); i++){

                Unit target = originTargets.get(i);
                if (target == oldStmt){
                    switchStmt.setTarget(i, newStmt);
//                    return;
                }
            }
            // default target
            Unit defaultTarget = ((SwitchStmtBlockInfo) switchBlock).getOriginDefault();
            if (defaultTarget == oldStmt){
                switchStmt.setDefaultTarget(newStmt);
                return;
            }
        } else {

            JTableSwitchStmt switchStmt = (JTableSwitchStmt)switchUnit;
            if (((SwitchStmtBlockInfo) switchBlock).isNeedReCreate() &&
                    ((SwitchStmtBlockInfo)switchBlock).getNewSwitchBranchStmt() != null){
                switchStmt = (JTableSwitchStmt) ((SwitchStmtBlockInfo)switchBlock).getNewSwitchBranchStmt();
            }
            List<Unit> originTargets = ((SwitchStmtBlockInfo) switchBlock).getOriginTargets();
            // targets
            for (int i = 0; i < originTargets.size(); i++){

                Unit target = originTargets.get(i);
                if (target == oldStmt){
                    switchStmt.setTarget(i, newStmt);
//                    return;
                }
            }
            // default target
            Unit defaultTarget = ((SwitchStmtBlockInfo) switchBlock).getOriginDefault();
            if (defaultTarget == oldStmt){
                switchStmt.setDefaultTarget(newStmt);
                return;
            }
        }
    }

    public static void updateIfLabels(BlockInfo invokeBlock, BlockInfo currentBlock){

        Unit ifUnit = ((IfStmtBlockInfo)invokeBlock).getIfBranchStmt();
        JIfStmt ifStmt = (JIfStmt) ifUnit;

        if (((IfStmtBlockInfo) invokeBlock).isNeedReCreate() &&
                ((IfStmtBlockInfo)invokeBlock).getNewIfBranchStmt() != null){
            ifStmt = (JIfStmt) ((IfStmtBlockInfo)invokeBlock).getNewIfBranchStmt();
        }
        Unit originTargets = ((IfStmtBlockInfo) invokeBlock).getOriginTarget();
        if (originTargets == ((IfStmtBlockInfo) currentBlock).getIfBranchStmt()){

            JIfStmt newStmt = (JIfStmt) ((IfStmtBlockInfo) currentBlock).getNewIfBranchStmt();
            if ( !(newStmt.getTarget() instanceof NopStmt) ){
                ifStmt.setTarget(newStmt);
            } else {
                ifStmt.setTarget(originTargets);
            }
        }
    }

    public static Unit getFirstValidStmtInUnits(Body targetMethodBody){

        Unit retUnit = targetMethodBody.getUnits().getFirst();
        while (retUnit != null && retUnit instanceof JIdentityStmt){

            retUnit = targetMethodBody.getUnits().getSuccOf(retUnit);
        }
        return retUnit;
    }

    public static void updateMethodLocalVariables(Body targetMethodBody, Unit targetUnit, BlockInfo mutant){

        Unit preUnit = targetMethodBody.getUnits().getPredOf(targetUnit);
        if (preUnit != null && preUnit instanceof GotoStmt){
            Unit newTarget = getFirstValidStmtInUnits(targetMethodBody);
            if (newTarget != null){
                targetUnit = newTarget;
            }
        }
        for (Local depensLocal : mutant.getDepensLocals()) {

            if (targetMethodBody.getLocals().contains(depensLocal)){
                continue;
            } else {
//                Local tmp = getVarWithTypeAndUpdateVarName(targetMethodBody.getLocals(), depensLocal);
                Local tmp = getInitializedVarWithTypeAndUpdateVarName(targetMethodBody, targetUnit, targetMethodBody.getLocals(), depensLocal);
                if (tmp != null){

                    targetMethodBody.getUnits().insertBeforeNoRedirect(new JAssignStmt(depensLocal, tmp) ,targetUnit);
                } else {

                    if(PrimitiveProvider.isPrimitiveOrString(depensLocal.getType())){

                        Value newValue = PrimitiveProvider.next(depensLocal.getType());
                        targetMethodBody.getUnits().insertBeforeNoRedirect(new JAssignStmt(depensLocal, newValue) ,targetUnit);
                    } else if (depensLocal.getType() instanceof ArrayType) {

                        ArrayType array = (ArrayType) depensLocal.getType();
                        if (array.numDimensions > 1){

                            List<Value> size = new ArrayList<>();
                            for (int i = 0; i < array.numDimensions; i++){

                                size.add(PrimitiveProvider.nextPositiveInt());
                            }
                            JAssignStmt unit = new JAssignStmt(depensLocal, new JNewMultiArrayExpr(array, size));
                            targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                        } else {

                            JAssignStmt unit = new JAssignStmt(depensLocal, new JNewArrayExpr(array.getElementType(), PrimitiveProvider.next("int")));
                            targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                        }
                    } else {

                        int depth = 0;
                        SootClass sootClass = Scene.v().getSootClass(depensLocal.getType().toString());

                        boolean insertFlag = false;
                        if (!sootClass.isAbstract()){

                            insertFlag = insertInitialStmt(targetMethodBody, targetUnit, sootClass, depensLocal, depth);
                        }
                        if (!insertFlag){

                            JAssignStmt unit = new JAssignStmt(depensLocal, NullConstant.v());
                            targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                        }
                    }
                }
                targetMethodBody.getLocals().add(depensLocal);
            }
        }
    }

    public static boolean insertInitialStmt(Body targetMethodBody, Unit targetUnit, SootClass sootClass, Local dependLocal, int depth){

        int seedAdder = 0;
        depth++;
        if (depth > Configuration.maxDepth){
            return false;
        }
        List<SootMethod> initMethods = getAllInitMethods(sootClass);
        SootMethod constructor = getConstructorWithNoArgs(initMethods);
        if (constructor != null){

            Unit newStmt = Jimple.v().newAssignStmt(dependLocal, new JNewExpr(sootClass.getType()));
            targetMethodBody.getUnits().insertBeforeNoRedirect(newStmt ,targetUnit);
            Unit invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(dependLocal, constructor.makeRef()));
            targetMethodBody.getUnits().insertBeforeNoRedirect(invokeStmt ,targetUnit);
            return true;
        }

        List<SootMethod> constructors = getConstructorWithPrimitiveArgs(initMethods);
        if (constructors.size() > 0){

            int index = new Random(System.currentTimeMillis()).nextInt(constructors.size());
            constructor = constructors.get(index);
            List<Value> methodArgs = new ArrayList<>();
            //01 prepare method args
            for (Type parameterType : constructor.getParameterTypes()) {
//                Local local = Jimple.v().newLocal("NL" + System.currentTimeMillis(), parameterType);
//                targetMethodBody.getLocals().add(local);
//                Value newValue = PrimitiveProvider.next(parameterType);
//                targetMethodBody.getUnits().insertBeforeNoRedirect(new JAssignStmt(local, newValue) ,targetUnit);
//                methodArgs.add(local);
                methodArgs.add(PrimitiveProvider.next(parameterType));
            }
            //02 call constructor
            Unit newStmt = Jimple.v().newAssignStmt(dependLocal, new JNewExpr(sootClass.getType()));
            targetMethodBody.getUnits().insertBeforeNoRedirect(newStmt ,targetUnit);
            Unit invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(dependLocal, constructor.makeRef(), methodArgs));
            targetMethodBody.getUnits().insertBeforeNoRedirect(invokeStmt ,targetUnit);
            return true;
        }
        Random random = new Random(System.currentTimeMillis());
        if(initMethods.size() > 0){
            constructor = initMethods.get(random.nextInt(initMethods.size()));
        } else {
            constructor = null;
        }
        if (constructor != null){

            List<Value> methodArgs = new ArrayList<>();
            //01 prepare method args
            for (Type parameterType : constructor.getParameterTypes()) {

                if (PrimitiveProvider.isPrimitiveOrString(parameterType)){

                    methodArgs.add(PrimitiveProvider.next(parameterType));
                } else if (parameterType instanceof ArrayType) {

                    ArrayType array = (ArrayType) parameterType;
                    Local newLocal;
                    if (array.numDimensions > 1){

                        List<Value> size = new ArrayList<>();
                        for (int i = 0; i < array.numDimensions; i++){

                            size.add(PrimitiveProvider.nextPositiveInt());
                        }

                        newLocal = Jimple.v().newLocal("NL" + random.nextInt(100) + depth + (seedAdder++), parameterType);
                        targetMethodBody.getLocals().add(newLocal);

                        JAssignStmt unit = new JAssignStmt(newLocal, new JNewMultiArrayExpr(array, size));
                        targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                    } else {

                        newLocal = Jimple.v().newLocal("NL" + random.nextInt(100) + depth + (seedAdder++), parameterType);
                        targetMethodBody.getLocals().add(newLocal);

                        JAssignStmt unit = new JAssignStmt(newLocal, new JNewArrayExpr(array.getElementType(), PrimitiveProvider.next("int")));
                        targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                    }
                    methodArgs.add(newLocal);
                } else {

                    Local newLocal = Jimple.v().newLocal("NL" + random.nextInt(100) + depth + (seedAdder++), parameterType);
                    targetMethodBody.getLocals().add(newLocal);
                    SootClass cSootClass = Scene.v().getSootClass(parameterType.toString());

                    boolean insertFlag = insertInitialStmt(targetMethodBody, targetUnit, cSootClass, newLocal, depth);
                    if (!insertFlag){

                        //初始化失败，插入为null
                        JAssignStmt unit = new JAssignStmt(newLocal, NullConstant.v());
                        targetMethodBody.getUnits().insertBeforeNoRedirect(unit, targetUnit);
                    }
                    methodArgs.add(newLocal);
                }
            }
            //02 call constructor
            Unit newStmt = Jimple.v().newAssignStmt(dependLocal, new JNewExpr(sootClass.getType()));
            targetMethodBody.getUnits().insertBeforeNoRedirect(newStmt ,targetUnit);
            Unit invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(dependLocal, constructor.makeRef(), methodArgs));
            targetMethodBody.getUnits().insertBeforeNoRedirect(invokeStmt ,targetUnit);
            return true;
        }
        return false;
    }

    public static SootMethod getConstructorWithNoArgs(List<SootMethod> initMethods){

        for (SootMethod initMethod : initMethods) {

            if (initMethod.getParameterCount() <= 0 && initMethod.isPublic()){
                return initMethod;
            }
        }
        return null;
    }

    public static List<SootMethod> getConstructorWithPrimitiveArgs(List<SootMethod> initMethods){

        List<SootMethod> methods = new ArrayList<>();
        List<Type> parameters = new ArrayList<>();
        for (SootMethod initMethod : initMethods) {

            parameters.clear();
            for (Type parameterType : initMethod.getParameterTypes()) {

                if (PrimitiveProvider.isPrimitiveOrString(parameterType)){
                    parameters.add(parameterType);
                }
            }
            if (parameters.size() == initMethod.getParameterCount() && initMethod.isPublic()){

                methods.add(initMethod);
            }
        }
        return methods;
    }

    public static List<SootMethod> getAllInitMethods(SootClass sootClass){

        List<SootMethod> methods = new ArrayList<>();
        if (!sootClass.isInterface()){

            for (SootMethod method : sootClass.getMethods()) {

                if (method.isConstructor() && method.isPublic()){
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    public static void insertNewInitialStmt(Body targetMethodBody, Unit targetUnit, Local target){

        target.getType();
    }

    public static void getVarDefFromOriginMethod(Local target, BlockInfo mutant){

        Chain<Unit> allUnits = mutant.getCurrentBlock().getBody().getUnits();
        for (Unit unit : allUnits) {

            if (unit instanceof JAssignStmt){

                if (((JAssignStmt) unit).getLeftOp().equals(target)){
                }
            }
        }
    }


    public static Local getVarWithTypeAndUpdateVarName(Chain<Local> vars, Local target){

        Random random = new Random(System.currentTimeMillis());
//        Random random = new Random(1);
        List<Local> rets = new ArrayList<>();
        for (Local var : vars) {

            //update name
            if (var.getName().equals(target.getName())){
                var.setName(var.getName() + "_N");
            }
            //get target var
            if (var.getType() == target.getType()){
                rets.add(var);
            }
        }
        if (rets.size() > 0){

            return rets.get(random.nextInt(rets.size()));
        }
        return null;
    }

    public static Local getInitializedVarWithTypeAndUpdateVarName(Body methodBody, Unit targetUnit, Chain<Local> vars, Local target){

//        Random random = new Random(System.currentTimeMillis());
        Random random = new Random(1);

        List<Unit> validUnits = getUnitsBeforeTargetUnit(methodBody, targetUnit);
        List<Local> rets = new ArrayList<>();
        for (Local var : vars) {

            //update name
            if (var.getName().equals(target.getName())){
                var.setName(var.getName() + "_N");
            }
            //get target var
            if (var.getType() == target.getType() && !var.getName().startsWith("$")){
//            if (var.getType() == target.getType()){
                rets.add(var);
            }
        }
        if (rets.size() > 0){
            List<Local> unInitialiedVars = new ArrayList<>();
            if (validUnits != null && validUnits.size() > 0){
                for (Local ret : rets) {
//                    if (PrimitiveProvider.isPrimitive(ret.getType()) && !ret.getName().startsWith("$")){
//                        //do nothing
//                    } else {
                        boolean initialFlag = false;
                        for (Unit validUnit : validUnits) {
                            if (validUnit instanceof JAssignStmt){
                                JAssignStmt assignStmt = (JAssignStmt) validUnit;
                                if (assignStmt.getLeftOp().equals(ret) &&
                                        assignStmt.getLeftOp().getType() == ret.getType()){
                                    initialFlag = true;
                                    break;
                                }
                            }
                        }
                        //remove
                        if (!initialFlag){
                            unInitialiedVars.add(ret);
                        }
//                    }
                }
            } else {
                return null;
            }
            rets.removeAll(unInitialiedVars);
            if (rets.size() > 0){
                return rets.get(random.nextInt(rets.size()));
            }
        }
        return null;
    }


    public static List<Unit> getUnitsBeforeTargetUnit(Body methodBody, Unit targetUnit){
        
        List<Unit> rets = new ArrayList<>();
        for (Unit unit : methodBody.getUnits()) {
            
            if (unit == targetUnit){
                return rets;
            } else {
                
                rets.add(unit);
            }
        }
        return null;
    }
    
    public static Map<String, BlockInfo> getIfElseBlocks(List<BlockInfo> succsBlocks, BlockInfo mutant){

//        Unit ifUnit = ((IfStmtBlockInfo)mutant).getIfBranchStmt();
//        IfStmt ifStmt = (IfStmt) ifUnit;
        Unit target = ((IfStmtBlockInfo) mutant).getOriginTarget();

        Map<String, BlockInfo> maps = new HashMap<>();
        boolean ifFlag = false;

        for (BlockInfo succsBlock : succsBlocks) {

            ifFlag = false;
            if (succsBlock.getValidStmts().size() > 0){

                for (Unit unit : succsBlock.getValidStmts()) {
                    if (unit == target){
                        ifFlag = true;
                        maps.put("IfBlock", succsBlock);
                        break;
                    }
                }
            } else {
                if (succsBlock instanceof IfStmtBlockInfo){
                    if (((IfStmtBlockInfo) succsBlock).getIfBranchStmt() != null &&
                            ((IfStmtBlockInfo) succsBlock).getIfBranchStmt() == target){
                        ifFlag = true;
                        maps.put("IfBlock", succsBlock);
                    }
                } else if (succsBlock instanceof ReturnStmtBlockInfo){

                    if (((ReturnStmtBlockInfo) succsBlock).getReturnStmt() != null &&
                            ((ReturnStmtBlockInfo) succsBlock).getReturnStmt() == target){
                        ifFlag = true;
                        maps.put("IfBlock", succsBlock);
                    }
                }
            }
            if (!ifFlag){
                maps.put("ElseBlock", succsBlock);
            }
        }
        return maps;
    }

    public static List<BlockInfo> getSuccesBlocks(List<BlockInfo> mutants, BlockInfo mutant){

        List<Block> succsBlocks = mutant.getCurrentBlock().getSuccs();
        List<BlockInfo> rets = new ArrayList<>();
        for (Block succsBlock : succsBlocks) {
            for (BlockInfo blockInfo : mutants) {
                if (blockInfo.getCurrentBlock() == succsBlock){
                    rets.add(blockInfo);
                }
            }
        }
        return rets;
    }
}
