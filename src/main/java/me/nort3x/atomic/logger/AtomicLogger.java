package me.nort3x.atomic.logger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AtomicLogger extends BasicLogger {
    private static OutputStream outputStream = System.out;

    private AtomicLogger() {
        super(outputStream, priority);
    }

    private static AtomicLogger instance;

    public synchronized static AtomicLogger getInstance() {
        if (instance == null)
            instance = new AtomicLogger();
        return instance;
    }

    /**
     * @param outPutStream set OutputStream of AtomicLogger Default: System.Out
     */
    public static void setOutputStream(OutputStream outPutStream) {
        AtomicLogger.outputStream = outPutStream;
    }

    static Priority priority = Priority.VERBOSE;

    public static void setVerbosityLevel(Priority priority) {
        AtomicLogger.priority = priority;
    }

    public static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
