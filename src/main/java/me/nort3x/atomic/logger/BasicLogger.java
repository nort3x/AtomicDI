package me.nort3x.atomic.logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Arrays;

public class BasicLogger {

    final private PrintStream ps;


    private void print(String prefix, String s) {
        ps.println(prefix + Instant.now().toString() + "\t " + s);
    }

    public BasicLogger(OutputStream outputStream) {
        ps = new PrintStream(outputStream);
    }

    public void info(String s) {
        print(Resources.Log_Prefix, s);
    }

    protected void warning(String s) {
        print(Resources.Warning_Prefix, s);
    }

    protected void fatal(String s, Object[] arguments) {
        String output = ((StringBuilder) Arrays.stream(arguments).reduce(new StringBuilder(), (o, o2) -> {
            ((StringBuilder) o).append("\n\t").append(o2);
            return o;
        })).toString();
        print(Resources.Error_Prefix, s + " : " + output);
    }
}
