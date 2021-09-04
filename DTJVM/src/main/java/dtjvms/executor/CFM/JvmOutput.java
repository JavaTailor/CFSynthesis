package dtjvms.executor.CFM;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import dtjvms.executor.ExecutorHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JvmOutput {

    private String stdout;
    private String stderr;
    private int exitValue;

    private List<String> Exceptions = new ArrayList<>();
    private List<String> Errors = new ArrayList<>();
    private List<String> Failures = new ArrayList<>();

    /**
     * Create an JvmOutput, a utility class for verifying output and exit
     * value from a Process
     *
     * @param process Process to analyze
     * @throws IOException If an I/O error occurs.
     */
    public JvmOutput(Process process) throws IOException {

        JvmOutput output = ExecutorHelper.getJvmOutput(process);
        exitValue = process.exitValue();
        this.stdout = output.getStdout();
        this.stderr = output.getStderr();
    }

    /**
     * Create an JvmOutput
     *
     * @param buf String buffer to analyze
     */
    public JvmOutput(String buf) {
        this(buf, buf);
        exitValue = -1;
    }

    /**
     * Create an JvmOutput
     *
     * @param stdout stdout buffer to analyze
     * @param stderr stderr buffer to analyze
     */
    public JvmOutput(String stdout, String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
        exitValue = -1;
    }

    /**
     * Create an JvmOutput
     *
     * @param stdout stdout buffer to analyze
     * @param stderr stderr buffer to analyze
     * @param exitValue jvm exit value
     */
    public JvmOutput(String stdout, String stderr, int exitValue) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitValue = exitValue;
    }

    /**
     * Get the captured group of the first string matching the pattern.
     * stderr is searched before stdout.
     *
     * @param pattern The multi-line pattern to match
     * @param group The group to capture
     * @return The matched string or null if no match was found
     */
    public String firstMatch(String pattern, int group) {
        Matcher stderrMatcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(stderr);
        Matcher stdoutMatcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(stdout);
        if (stderrMatcher.find()) {
            return stderrMatcher.group(group);
        }
        if (stdoutMatcher.find()) {
            return stdoutMatcher.group(group);
        }
        return null;
    }

    /**
     * Get the first string matching the pattern.
     * stderr is searched before stdout.
     *
     * @param pattern The multi-line pattern to match
     * @return The matched string or null if no match was found
     */
    public String firstMatch(String pattern) {
        return firstMatch(pattern, 0);
    }

    /**
     * Report summary that will help to diagnose the problem
     * Currently includes:
     *  - standard input produced by the process under test
     *  - standard output
     *  - exit code
     *  Note: the command line is printed by the ProcessTools
     */
    public void reportDiagnosticSummary() {
        String msg =
                " stdout: [" + stdout + "];\n" +
                        " stderr: [" + stderr + "]\n" +
                        " exitValue = " + getExitValue() + "\n";

        System.err.println(msg);
    }

    /**
     * Print the stdout buffer to the given {@code PrintStream}.
     *
     * @return this OutputAnalyzer
     */
    public JvmOutput outputTo(PrintStream out) {
        out.println(getStdout());
        return this;
    }

    /**
     * Print the stderr buffer to the given {@code PrintStream}.
     *
     * @return this OutputAnalyzer
     */
    public JvmOutput errorTo(PrintStream out) {
        out.println(getStderr());
        return this;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    /**
     * Get the contents of the output buffer (stdout and stderr)
     *
     * @return Content of the output buffer
     */
    public String getOutput() {
        return stdout + stderr;
    }

    /**
     * Get the contents of the stdout buffer
     *
     * @return Content of the stdout buffer
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * Get the contents of the stderr buffer
     *
     * @return Content of the stderr buffer
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * Get the process exit value
     *
     * @return Process exit value
     */
    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public List<String> getExceptions() {
        return Exceptions;
    }

    public List<String> getErrors() {
        return Errors;
    }

    public List<String> getFailures() {
        return Failures;
    }

    public List<String> getFEEInfo(){
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(Errors);
        tmp.addAll(Exceptions);
        tmp.addAll(Failures);
        return tmp;
    }

    /**
     * Get the contents of the output buffer (stdout and stderr) as list of strings.
     * Output will be split by newlines.
     *
     * @return Contents of the output buffer as list of strings
     */
    public List<String> asLines() {
        return asLines(getOutput());
    }

    public List<String> stderrAsLines(){
        return asLines(getStderr());
    }

    public List<String> stdoutAsLines(){
        return asLines(getStdout());
    }

    private List<String> asLines(String buffer) {
        return Arrays.asList(buffer.split("(\\r\\n|\\n|\\r)"));
    }

    @Override
    public String toString() {

        List<String> results = asLines();
        int outputBound = results.size() < 3 ? results.size() : 3;
        return results.subList(0,outputBound).toString();
    }
}
