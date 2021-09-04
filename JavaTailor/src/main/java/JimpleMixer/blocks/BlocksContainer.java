package JimpleMixer.blocks;

import JimpleMixer.core.JMUtils;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.options.Options;
import soot.toolkits.graph.*;
import soot.tools.CFGViewer;
import soot.util.Chain;

import java.util.*;
import java.util.stream.Collectors;

public class BlocksContainer {

    public static boolean containsGoto = false;
    public static Map<String, List<BlockInfo>> allMutationMap = new HashMap<>();
    public static Map<String, List<BlockInfo>> validMutationMap = new HashMap<>();
    public static List<BlockInfo> allMutations = new ArrayList<>();
    public static List<BlockInfo> validMutations = new ArrayList<>();

    static {

        if (allMutationMap == null){
            allMutationMap = new HashMap<>();
        }
        if (validMutationMap == null){
            validMutationMap = new HashMap<>();
        }
        if (allMutations == null){
            allMutations = new ArrayList<>();
        }
        if (validMutations == null){
            validMutations = new ArrayList<>();
        }
    }

    public static void initMutantsFromClasses(List<String> mutants){

        for (String mutation : mutants) {
            SootClass mutantClass = JMUtils.loadTargetClass(mutation);
            List<BlockInfo> blockMutants = BlocksContainer.getBlocksFromSootClass(mutantClass);
            allMutationMap.put(mutation, blockMutants);
            validMutationMap.put(mutation, filterInvalidBlocks(blockMutants));
        }
    }

    public static List<BlockInfo> getBlocksFromSootClass(SootClass sootClass){

        List<BlockInfo> allBlocks = new ArrayList<>();
        List<SootMethod> methodList = sootClass.getMethods();
        Chain<SootField> fieldList = sootClass.getFields();
        for (SootMethod method : methodList) {

            if (!method.isAbstract()){

                Body methodBody = method.retrieveActiveBody();
                if (!method.isConstructor()){

                    // 01 stmt-block, if-block , switch-block, return-block, TrapBlock
                    BlockGraph blockGraph = new ZonedBlockGraph(methodBody);
                    for (Block block : blockGraph.getBlocks()) {
                        BlockInfo currentBlock = BlockAnalyzer.initialBlock(sootClass, block, methodBody);
                        if (currentBlock != null){
                            allBlocks.add(currentBlock);
                        }
                    }
                    //02 for-block
                    LoopNestTree loopNestTree = new LoopNestTree(methodBody);
                    for (Loop loop : loopNestTree) {

                        BlockInfo loopBlock = BlockAnalyzer.initialLoopBlock(methodBody, allBlocks, loop);
                        if (loopBlock != null){
                            allBlocks.add(loopBlock);
                        }
                    }
                    //03 trap-block
                    for (Trap trap : methodBody.getTraps()) {

                        BlockInfo trapBlock = BlockAnalyzer.initialTrapBlock(methodBody, allBlocks, trap);
                    }
                }
            }
        }
        return allBlocks;
    }

    public static List<BlockInfo> filterInvalidBlocks(List<BlockInfo> allBlocks){

        List<BlockInfo> validBlocks =
                allBlocks.stream().filter(blockInfo -> ! (blockInfo instanceof ReturnStmtBlockInfo) &&
                        !(blockInfo.getValidStmts().size() == 1 && blockInfo.getValidStmts().get(0) instanceof JThrowStmt)
                ).collect(Collectors.toList());

        /**
         * add one goto block
         */
        if (!containsGoto){
            List<BlockInfo> gotos = validBlocks.stream().filter(blockInfo -> blockInfo.getValidStmts().size() == 0
                    && blockInfo instanceof StmtBlockInfo
                    && ((StmtBlockInfo)blockInfo).getGotoStmt() != null).collect(Collectors.toList());
            if (gotos.size() > 0){

                validMutations.add(gotos.get(0));
                containsGoto = true;
            }
        }
        validMutations.addAll(validBlocks.stream().filter(blockInfo -> (blockInfo instanceof LoopStmtBlockInfo || blockInfo.getValidStmts().size() > 0)).collect(Collectors.toList()));
        return validBlocks;
    }

    public static Map<String, List<BlockInfo>> getAllMutationMap() {
        return allMutationMap;
    }

    public static Map<String, List<BlockInfo>> getValidMutationMap() {
        return validMutationMap;
    }

    public static List<BlockInfo> getAllMutations() {
        return allMutations;
    }

    public static List<BlockInfo> getValidMutations() {
        return validMutations;
    }
}
