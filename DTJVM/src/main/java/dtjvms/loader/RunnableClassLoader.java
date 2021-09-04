package dtjvms.loader;

import dtjvms.DTConfiguration;
import dtjvms.DTPlatform;
import dtjvms.executor.DTExecutor;
import dtjvms.executor.Executor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;

public class RunnableClassLoader {

    private static final String DOT = ".";
    public static String projectPath;
    public static String projectName;
    public static Boolean debug = false;

    public static boolean hasDefindedClass = false;
    public static String predefinedClassPath;

    public static Set<String> definedClasses = new HashSet<>();

    public static void main(String[] args) {

        if (args.length == 1){

            projectPath = args[0];
        } else if (args.length == 2){

            projectPath = args[0];
            projectName = args[1];
        }else if (args.length == 3) {

            projectPath = args[0];
            projectName = args[1];
            debug = Boolean.valueOf(args[2]);
        } else if (args.length == 4){

            projectPath = args[0];
            projectName = args[1];
            debug = Boolean.valueOf(args[2]);
            predefinedClassPath = args[3];
            hasDefindedClass = true;
            setPredefinedTargetClasses(predefinedClassPath);
        }else{

            throw new RuntimeException("Too many args, please check!");
        }

        File classFile = new File(projectPath);
        Stack<File> stack = new Stack<>();
        stack.push(classFile);

        while (!stack.isEmpty()) {

            File path = stack.pop();
            File[] classFiles = path.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".class");
                }
            });
            if (classFiles == null) {
                break;
            }
            for (File subFile : classFiles) {

                if (subFile.isDirectory()) {
                    stack.push(subFile);
                } else {
                    try {

                        String classAbsolutePath = subFile.getAbsolutePath();
                        String className;

                        if (projectName != null &&
                                (projectPath.endsWith(projectName) || projectPath.endsWith(projectName + DTPlatform.FILE_SEPARATOR))){

                            className = classAbsolutePath.substring(classAbsolutePath.lastIndexOf(projectName) + projectName.length() + 1,
                                    classAbsolutePath.lastIndexOf(DTPlatform.CLASS_SUFFIX));
                        } else {
                            className = classAbsolutePath.replace(projectPath,"").replace(DTPlatform.CLASS_SUFFIX, "").trim();
                        }

                        String[] filePaths = className.split(Matcher.quoteReplacement(DTPlatform.FILE_SEPARATOR));
                        if (filePaths[0].equals("")){
                            filePaths = Arrays.copyOfRange(filePaths, 1, filePaths.length);
                        }
                        className = StringUtils.join(filePaths, DOT);

                        if (debug){
                            System.out.println("current class: " + className);
                        }
                        //TODO unsafe
//                        if (className.startsWith(TestPlatform.FILE_SEPARATOR)){
//                            className = className.replaceFirst(TestPlatform.FILE_SEPARATOR, "");
//                        }
//                        className = className.replace(TestPlatform.FILE_SEPARATOR,".");
                        if (shouldAnalysis(className)){

                            final Class<?>[] clazz = {null};
                            String finalClassName = className;
                            Thread init = new Thread(() -> {
                                try {
                                    clazz[0] = Class.forName(finalClassName);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                            init.start();
                            init.join(1000);

                            if (clazz[0] == null){
                                continue;
                            }
                            boolean junitFlag = false;
                            boolean applicationFlag = false;

                            if (junit.framework.TestCase.class.isAssignableFrom(clazz[0])){

                                System.out.println("Junit@" + className);
                                continue;
                            }

                            Method[] allMethods = null;
                            try {

                                allMethods = clazz[0].getMethods();
                            }catch (Exception e){

                                //for those classes which would cause the whole program down
                                //do nothing;
                                e.printStackTrace();
                            } catch (Error error){

                                //for those classes which would cause the whole program down
                                //do nothing
                                error.printStackTrace();
                            }

                            if (allMethods == null){
                                continue;
                            }

                            for (Method method : allMethods) {

                                /**
                                 * commons use org.junit.jupiter.api as junit dependency
                                 */
                                if ( (method.getAnnotationsByType(org.junit.Test.class).length > 0
                                        || method.getAnnotationsByType(org.junit.jupiter.api.Test.class).length > 0)
                                        && !junitFlag) {
                                    System.out.println("Junit@" + className);
                                    junitFlag = true;
                                }
                                if (method.getName().equals("main") && !applicationFlag) {
                                    System.out.println("Application@" + className);
                                    applicationFlag = true;
                                }
                            }
                        }
//                        for (Annotation annotation : clazz.getAnnotationsByType(Test.class)) {
//                            System.out.println(annotation);
//                        }
//                        List<FrameworkMethod> methods = new TestClass(clazz).getAnnotatedMethods(Test.class);
//                        if (methods.size() > 0){
//                            System.out.println("Junit@"+className);
//                            continue;
//                        }
//                        for (Method method : clazz.getMethods()) {
//                            System.out.println("method.getAnnotation(Test.class): " +method.getAnnotation(Test.class));
//                            System.out.println("method.getAnnotatedReturnType(): " + method.getAnnotatedReturnType());
//                            if (method.getName().equals("main")){
//
//                                System.out.println("Application@"+className);
//                                break;
//                            }
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * load predefined target classes
     * @param filepath
     */
    public static void setPredefinedTargetClasses(String filepath){

        File file = new File(filepath);
        if (file.exists()){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.isEmpty()){
                        definedClasses.add(line);
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Exception found when reading: " + filepath + " !");
            }
        } else {
            throw new RuntimeException("Defined Classes Path: " + filepath + " not Available!");
        }
    }

    public static boolean shouldAnalysis(String className){

        if (className == null){
            return false;
        }
        if (hasDefindedClass){

            for (String targetClass : definedClasses) {

                String tmp = targetClass.trim();
                if (className.equals(tmp)){
                    return true;
                }
            }
            return false;
        } else {

            for (String projectsFilterClass : DTConfiguration.PROJECTS_FILTER_CLASSES) {

                String tmp = projectsFilterClass.trim();
                if (className.startsWith(tmp)){
                    return false;
                }
            }
            return true;
        }
    }
}
