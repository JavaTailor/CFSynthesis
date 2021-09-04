package core;

import soot.Printer;
import soot.SootClass;
import soot.baf.BafASMBackend;
import soot.options.Options;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ClassInfo {

    private String originClassName;
    private String originClassPath;
    private String className;
    private String pathToClass;
    private boolean isJunit;
    private int mutationOrder;
    private int mutationTimes;

    public ClassInfo() {
    }

    public ClassInfo(String originClassName, String className, int mutationOrder, int mutationTimes) {
        this.originClassName = originClassName;
        this.className = className;
        this.isJunit = false;
        this.mutationOrder = mutationOrder;
        this.mutationTimes = mutationTimes;
    }

    public ClassInfo(String originClassName, String className, String pathToClass, int mutationOrder, int mutationTimes) {
        this.originClassName = originClassName;
        this.className = className;
        this.pathToClass = pathToClass;
        this.isJunit = false;
        this.mutationOrder = mutationOrder;
        this.mutationTimes = mutationTimes;
    }

    public ClassInfo(String originClassName, String originClassPath, String className, String pathToClass, int mutationOrder, int mutationTimes) {
        this.originClassName = originClassName;
        this.originClassPath = originClassPath;
        this.className = className;
        this.pathToClass = pathToClass;
        this.isJunit = false;
        this.mutationOrder = mutationOrder;
        this.mutationTimes = mutationTimes;
    }

    public ClassInfo(String originClassName, String originClassPath, String className, String pathToClass, Boolean isJunit ,int mutationOrder, int mutationTimes) {
        this.originClassName = originClassName;
        this.originClassPath = originClassPath;
        this.className = className;
        this.pathToClass = pathToClass;
        this.isJunit = isJunit;
        this.mutationOrder = mutationOrder;
        this.mutationTimes = mutationTimes;
    }

    public boolean hasCovered(){
        return mutationTimes != 0;
    }

    public boolean isOriginClass(){
        return originClassName.equals(className);
    }

    public void storeToCoverOriginClass() {

        try {
            File sourceFile = new File(pathToClass);
            File targetFile = new File(originClassPath);
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getOriginClassPath() {
        return originClassPath;
    }

    public void setOriginClassPath(String originClassPath) {
        this.originClassPath = originClassPath;
    }

    public String getOriginClassName() {
        return originClassName;
    }

    public String generateMutateClassFilename(){

        return originClassName + "_" +
                String.format("%02d", mutationOrder + 1) +
                String.format("%02d", mutationTimes) +
                "@" +
                System.currentTimeMillis() +
                ".class";
    }

    public void saveSootClassToFile(SootClass seedClass) {

        try{
            OutputStream jimpleStreamOut = new FileOutputStream(pathToClass.replace(".class", ".jimple"));
            PrintWriter jimpleWriterOut = new PrintWriter(new OutputStreamWriter(jimpleStreamOut));
            Printer.v().printTo(seedClass, jimpleWriterOut);
            jimpleStreamOut.flush();
            jimpleWriterOut.flush();
            jimpleStreamOut.close();

            OutputStream classStreamOut = new FileOutputStream(pathToClass);
            BafASMBackend backend = new BafASMBackend(seedClass, Options.v().java_version());
            backend.generateClassFile(classStreamOut);
            classStreamOut.flush();
            classStreamOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveSootClassToTargetPath(SootClass seedClass, String path) {

        try{
            OutputStream jimpleStreamOut = new FileOutputStream(path.replace(".class", ".jimple"));
            PrintWriter jimpleWriterOut = new PrintWriter(new OutputStreamWriter(jimpleStreamOut));
            Printer.v().printTo(seedClass, jimpleWriterOut);
            jimpleStreamOut.flush();
            jimpleWriterOut.flush();
            jimpleStreamOut.close();

            OutputStream classStreamOut = new FileOutputStream(path);
            BafASMBackend backend = new BafASMBackend(seedClass, Options.v().java_version());
            backend.generateClassFile(classStreamOut);
            classStreamOut.flush();
            classStreamOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mutationTimesIncrease(){

        this.mutationTimes++;
    }

    public boolean isJunit() {
        return isJunit;
    }

    public void setJunit(boolean junit) {
        isJunit = junit;
    }

    public void setOriginClassName(String originClassName) {
        this.originClassName = originClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPathToClass() {
        return pathToClass;
    }

    public void setPathToClass(String pathToClass) {
        this.pathToClass = pathToClass;
    }

    public int getMutationOrder() {
        return mutationOrder;
    }

    public void setMutationOrder(int mutationOrder) {
        this.mutationOrder = mutationOrder;
    }

    public int getMutationTimes() {
        return mutationTimes;
    }

    public void setMutationTimes(int mutationTimes) {
        this.mutationTimes = mutationTimes;
    }
}
