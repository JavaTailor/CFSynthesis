package JimpleMixer.blocks;

import soot.Unit;
import soot.toolkits.graph.Block;

import java.util.List;

public class LoopStmtBlockInfo extends BlockInfo{

    private BlockInfo headBlock;
    private BlockInfo tailBlock;
    private List<BlockInfo> loopBlocks;

    public LoopStmtBlockInfo() {
        super(null, null);
    }


    public LoopStmtBlockInfo(String className, Block currentBlock) {
        super(className,currentBlock);
    }

    public LoopStmtBlockInfo(BlockInfo headBlock) {
        super(null, null);
    }

    public BlockInfo getHeadBlock() {
        return headBlock;
    }

    public void setHeadBlock(BlockInfo headBlock) {
        this.headBlock = headBlock;
        this.setCurrentBlock(headBlock.getCurrentBlock());
    }

    public BlockInfo getTailBlock() {
        return tailBlock;
    }

    public void setTailBlock(BlockInfo tailBlock) {
        this.tailBlock = tailBlock;
    }

    public List<BlockInfo> getLoopBlocks() {
        return loopBlocks;
    }

    public void setLoopBlocks(List<BlockInfo> loopBlocks) {
        this.loopBlocks = loopBlocks;
    }

    @Override
    public String toString() {
        return "LoopStmtBlockInfo: " +
                "ClassName: " + getClassName() +
                "Local Vars: " + (getDepensLocals() == null ? "null" : getDepensLocals()) +
                "Loop blocks: " + loopBlocks;
    }
}
