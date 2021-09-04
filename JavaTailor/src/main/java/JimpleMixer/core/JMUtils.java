package JimpleMixer.core;

import soot.Printer;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.baf.BafASMBackend;
import soot.baf.JasminClass;
import soot.options.Options;

import java.io.*;

public class JMUtils {

    public static SootClass loadTargetClass(String classname){

        try {
            SootClass sootClass = Scene.v().forceResolve(classname, SootClass.BODIES);
            Scene.v().loadNecessaryClasses();
            return sootClass;
        } catch (Exception e){

            e.printStackTrace();
            return null;
        }
    }

    public static void saveMutationToPath(String str, String filename){

        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(filename,true));
            out.write(str);
            out.write("\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSootClassToPath(SootClass sootClass, String filename){

        String jimpleFilePath = filename + "/" + sootClass.getName()  + ".jimple";
        String classFilePath = filename + "/" + sootClass.getName()  + ".class";
        try{
            OutputStream jimpleStreamOut = new FileOutputStream(jimpleFilePath);
            PrintWriter jimpleWriterOut = new PrintWriter(new OutputStreamWriter(jimpleStreamOut));
            //01 jimple
            Printer.v().printTo(sootClass, jimpleWriterOut);
            jimpleStreamOut.flush();
            jimpleWriterOut.flush();
            jimpleStreamOut.close();

            OutputStream classStreamOut = new FileOutputStream(classFilePath);
            //class
            BafASMBackend backend = new BafASMBackend(sootClass, Options.v().java_version());
            backend.generateClassFile(classStreamOut);
            classStreamOut.flush();
            classStreamOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean saveSootClassToLocal(SootClass sootClass, int mode){

        boolean successSaved = true;
        String fileName = SourceLocator.v().getFileNameFor(sootClass, mode);
        try{

            OutputStream streamOut = new FileOutputStream(fileName);
            PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
            switch (mode){

                case Options.output_format_jimple:

                    Printer.v().printTo(sootClass, writerOut);
                    break;
                case Options.output_format_class:

                    try {
                        BafASMBackend backend = new BafASMBackend(sootClass, Options.v().java_version());
                        backend.generateClassFile(streamOut);
//                        JasminClass jasminClass = new JasminClass(sootClass);
//                        jasminClass.print(writerOut);
                    }catch (Exception e){
                        e.printStackTrace();

                        successSaved = false;
//                        CFGGenerator.printCFG(sootClass);
//                        fileName = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_jimple);

//                        streamOut = new FileOutputStream("./sootOutput/" + sootClass.getName() + ".jimple" );
//                        writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
//                        Printer.v().printTo(sootClass, writerOut);
                    }
                    break;
                case Options.output_format_jasmin:

                    JasminClass jasminClass = new JasminClass(sootClass);
                    jasminClass.print(writerOut);
                    System.out.println(fileName);
                    break;
                default:

                    jasminClass = new JasminClass(sootClass);
                    jasminClass.print(writerOut);
                    break;
            }
            writerOut.flush();
            streamOut.close();
        } catch (Exception e){

            e.printStackTrace();
        }
        return successSaved;
    }
}
