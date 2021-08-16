package me.nort3x.atomic.logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicLogger {

    final ExecutorService printThread = Executors.newSingleThreadExecutor();
    final private PrintStream ps;
    final Priority priority;

    public BasicLogger(OutputStream outputStream, Priority priority) {
        ps = new PrintStream(outputStream);
        this.priority = priority;
    }

    private void print(String prefix, String s, Priority priority) {
        printThread.submit(() -> {
            if (BasicLogger.this.priority.comparable <= priority.comparable)
                ps.println(prefix + Instant.now().toString() + " " + s);
        });
    }

    public void info(String s, Priority priority) {
        print(Resources.Log_Prefix, s, priority);
    }

    public void info(String s, Priority priority, Class<?> c) {
        print(Resources.Log_Prefix, forgeName(c) + s, priority);
    }

    public void warning(String s, Priority priority) {
        print(Resources.Warning_Prefix, s, priority);
    }

    public void warning(String s, Priority priority, Class<?> c) {
        print(Resources.Warning_Prefix, forgeName(c) + s, priority);
    }

    public void fatal(String s, Priority priority) {
        print(Resources.Error_Prefix, s, priority);
    }

    public void fatal(String s, Priority priority, Class<?> c) {
        print(Resources.Error_Prefix, forgeName(c) + s, priority);
    }

    private String forgeName(Class<?> c) {
        return "[" +
                (c.isAnonymousClass() ? "AnonymousType - " + c.getName() : c.getSimpleName())
                + "] ";
    }
}
