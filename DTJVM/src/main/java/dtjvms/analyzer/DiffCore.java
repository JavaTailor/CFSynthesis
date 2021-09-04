package dtjvms.analyzer;

import dtjvms.executor.CFM.JvmOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class DiffCore {

    private int STATE_ID;
    private boolean discard;
    private String diffMessage;
    private String detailedMessage;

    public DiffCore(String diffMessage) {
        this.diffMessage = diffMessage;
    }

    public DiffCore(int STATE_ID, boolean discard, String diffMessage) {
        this.STATE_ID = STATE_ID;
        this.discard = discard;
        this.diffMessage = diffMessage;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    public int getSTATE_ID() {
        return STATE_ID;
    }

    public void setSTATE_ID(int STATE_ID) {
        this.STATE_ID = STATE_ID;
    }

    public boolean isDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    public String getDiffMessage() {
        return diffMessage;
    }

    public void setDiffMessage(String diffMessage) {
        this.diffMessage = diffMessage;
    }
}
