package dtjvms;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProjectInfo {

    private String projectName;
    private String projectRootPath;
    private String libPath;
    private String libsString;
    private String srcClassPath;
    private long totalSrcClassSize;
    private String testClassPath;
    private long totalTestClassSize;
    private String srcJavaPath;
    private long totalSrcJavaSize;
    private String testJavaPath;
    private long totalTestJavaSize;
    private String pClassPath;

    private ArrayList<String> applicationClasses;
    private ArrayList<String> junitClasses;

    private ArrayList<String> vmoptions;
    private ArrayList<String> projoptions;

    private Boolean hasPredefinedClass = false;
    private String predefinedClassPath = "";

    private ArrayList<String> predefinedClasses;

    public ProjectInfo() {
    }

    public ProjectInfo(String projectName, String projectRootPath) {
        this.projectName = projectName;
        this.projectRootPath = projectRootPath;
    }

    public ProjectInfo(String projectName, String projectRootPath, String libPath) {
        this.projectName = projectName;
        this.projectRootPath = projectRootPath;
        this.libPath = libPath;
    }

    public String getpClassPath(){

        if (pClassPath != null){
            return pClassPath;
        }
        ArrayList<String> pClassPathes = new ArrayList<>();
//        if (libPath != null){
//            pClassPathes.add(libPath + DTPlatform.FILE_SEPARATOR + "*");
//        }
        if (libsString != null){
            pClassPathes.add(libsString);
        }
        if (srcClassPath != null){
            pClassPathes.add(srcClassPath);
        }
        if (testClassPath != null){
            pClassPathes.add(testClassPath);
        }
        if (srcClassPath == null && testClassPath == null && projectRootPath != null){
            pClassPathes.add(projectRootPath);
        }
        pClassPath = StringUtils.join(pClassPathes, DTPlatform.PATH_SEPARATOR);
        return pClassPath;
    }

    public Boolean getHasPredefinedClass() {
        return hasPredefinedClass;
    }

    public void setHasPredefinedClass(Boolean hasPredefinedClass) {
        this.hasPredefinedClass = hasPredefinedClass;
    }

    public String getPredefinedClassPath() {
        return predefinedClassPath;
    }

    public void setPredefinedClassPath(String predefinedClassPath) {
        hasPredefinedClass = true;
        this.predefinedClassPath = predefinedClassPath;

//        predefinedClasses = new ArrayList<>();
//        File file = new File(predefinedClassPath);
//        if (file.exists()){
//            try {
//                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//                String line = null;
//                while ((line = bufferedReader.readLine()) != null) {
//                    if (!line.isEmpty()){
//                        predefinedClasses.add(line);
//                    }
//                }
//                bufferedReader.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("Exception found when reading: " + predefinedClassPath + " !");
//            }
//        } else {
//            throw new RuntimeException("Defined Classes Path: " + predefinedClassPath + " not Available!");
//        }
    }

    public ArrayList<String> getPredefinedClasses() {
        return predefinedClasses;
    }

    public void setPredefinedClasses(ArrayList<String> predefinedClasses) {
        this.predefinedClasses = predefinedClasses;
    }

    public ArrayList<String> getApplicationClasses() {
        return applicationClasses;
    }

    public void setApplicationClasses(ArrayList<String> applicationClasses) {
        this.applicationClasses = applicationClasses;
    }

    public long getTotalSrcClassSize() {
        return totalSrcClassSize;
    }

    public void setTotalSrcClassSize(long totalSrcClassSize) {
        this.totalSrcClassSize = totalSrcClassSize;
    }

    public long getTotalTestClassSize() {
        return totalTestClassSize;
    }

    public void setTotalTestClassSize(long totalTestClassSize) {
        this.totalTestClassSize = totalTestClassSize;
    }

    public long getTotalSrcJavaSize() {
        return totalSrcJavaSize;
    }

    public void setTotalSrcJavaSize(long totalSrcJavaSize) {
        this.totalSrcJavaSize = totalSrcJavaSize;
    }

    public long getTotalTestJavaSize() {
        return totalTestJavaSize;
    }

    public void setTotalTestJavaSize(long totalTestJavaSize) {
        this.totalTestJavaSize = totalTestJavaSize;
    }

    public ArrayList<String> getVmoptions() {
        return vmoptions;
    }

    public void setVmoptions(ArrayList<String> vmoptions) {
        this.vmoptions = vmoptions;
    }

    public ArrayList<String> getProjoptions() {
        return projoptions;
    }

    public void setProjoptions(ArrayList<String> projoptions) {
        this.projoptions = projoptions;
    }

    public ArrayList<String> getJunitClasses() {
        return junitClasses;
    }

    public void setJunitClasses(ArrayList<String> junitClasses) {
        this.junitClasses = junitClasses;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectRootPath() {
        return projectRootPath;
    }

    public void setProjectRootPath(String projectRootPath) {
        this.projectRootPath = projectRootPath;
    }

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public String getLibsString() {
        return libsString;
    }

    public void setLibsString(String libsString) {
        this.libsString = libsString;
    }

    public String getSrcClassPath() {
        return srcClassPath;
    }

    public void setSrcClassPath(String srcClassPath) {
        this.srcClassPath = srcClassPath;
    }

    public String getTestClassPath() {
        return testClassPath;
    }

    public void setTestClassPath(String testClassPath) {
        this.testClassPath = testClassPath;
    }

    public String getSrcJavaPath() {
        return srcJavaPath;
    }

    public void setSrcJavaPath(String srcJavaPath) {
        this.srcJavaPath = srcJavaPath;
    }

    public String getTestJavaPath() {
        return testJavaPath;
    }

    public void setTestJavaPath(String testJavaPath) {
        this.testJavaPath = testJavaPath;
    }

    @Override
    public String toString() {
        String titile = String.join("", Collections.nCopies(50,"=")) +
                " Project Information " + String.join("", Collections.nCopies(50,"="));
        return  titile + DTPlatform.LINE_SEPARATOR +
                "Project Path: " + projectRootPath + DTPlatform.LINE_SEPARATOR +
                "Project Name: " + projectName + DTPlatform.LINE_SEPARATOR +
                "         lib: " + libPath + DTPlatform.LINE_SEPARATOR +
                "         src: " + srcJavaPath + DTPlatform.LINE_SEPARATOR +
                "   total src: " + totalSrcJavaSize + DTPlatform.LINE_SEPARATOR +
                "        test: " + testJavaPath + DTPlatform.LINE_SEPARATOR +
                "  total test: " + totalTestJavaSize + DTPlatform.LINE_SEPARATOR +
                "   src class: " + (srcClassPath != null ? srcClassPath : projectRootPath) + DTPlatform.LINE_SEPARATOR +
                "  test class: " + testClassPath + DTPlatform.LINE_SEPARATOR +
                " applicaiton: " + (applicationClasses != null ? applicationClasses.size() : 0) + "/" + totalSrcClassSize + DTPlatform.LINE_SEPARATOR +
                " junit class: " + (junitClasses != null ? junitClasses.size() :  0) + "/" + totalTestClassSize + DTPlatform.LINE_SEPARATOR +
                String.join("", Collections.nCopies(titile.length(), "="));
    }
}
