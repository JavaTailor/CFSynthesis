package dtjvms;

import vmoptions.VMOptions;

import java.util.Collections;

public class JvmInfo {

    private String jvmId;
    private String jvmName;
    private String rootPath;
    private String folderName;
    private String javaCmd;
    private String version;
    private VMOptions vmOptions;

    public JvmInfo(String javaCmd) {
        this.javaCmd = javaCmd;
    }

    public JvmInfo(String rootPath, String folderName, String jvmName, String version, String javaCmd) {
        this.rootPath = rootPath;
        this.folderName = folderName;
        this.jvmName = jvmName;
        this.version = version;
        this.javaCmd = javaCmd;
        this.jvmId = version + "-" + jvmName + "-" + folderName;
    }

    public void setVmOptions(VMOptions vmOptions){

        this.vmOptions = vmOptions;
    }

    public VMOptions getVmOptions(){

        return vmOptions;
    }

    public String getVersion() {
        return version;
    }

    public String getJvmId() {
        return jvmId;
    }

    public String getJvmName() {
        return jvmName;
    }

    public void setJvmName(String jvmName) {
        this.jvmName = jvmName;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getJavaCmd() {
        return javaCmd;
    }

    @Override
    public String toString() {
        String titile = String.join("", Collections.nCopies(50,"=")) +
                " JVM Implementation " + String.join("", Collections.nCopies(50,"="));
        return  titile + DTPlatform.LINE_SEPARATOR +
                "JVM root path: " + rootPath + DTPlatform.LINE_SEPARATOR +
                "  JVM Version: " + folderName + DTPlatform.LINE_SEPARATOR +
                "     Java Cmd: " + javaCmd + DTPlatform.LINE_SEPARATOR +
                String.join("", Collections.nCopies(titile.length(),"="));
    }
}
