package me.nort3x.atomic.logger;

import me.nort3x.atomic.logger.enums.Color;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.logger.res.Quotes;
import me.nort3x.atomic.logger.res.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class BasicLogger extends LoggerAdaptor{

    static final boolean useANSI;

    static {
        if (System.console() != null && System.getenv().get("TERM") != null || true) { // if system supports ANSI // for know :)
            Resources.Error_Prefix = Color.color(Resources.Error_Prefix, Color.ANSI_RED);
            Resources.Warning_Prefix = Color.color(Resources.Warning_Prefix, Color.ANSI_YELLOW);
            Resources.Log_Prefix = Color.color(Resources.Log_Prefix, Color.ANSI_GREEN);
            Resources.Debug_Prefix = Color.color(Resources.Debug_Prefix, Color.ANSI_YELLOW);
            Resources.Trace_Prefix = Color.color(Resources.Debug_Prefix, Color.ANSI_CYAN);
            useANSI = true;
        } else
            useANSI = false;
    }



    PrintStream ps;

    static final ExecutorService printThread = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("Printer Thread");
        return t;
    });

    Priority priority;
    String name;
    protected BasicLogger(PrintStream ps, Priority priority, String name) {
        this.ps = ps;
        this.priority = priority;
        this.name = name;
    }

    private void print(String prefix, String s, Priority priority) {
        printThread.submit(() -> {
            if (BasicLogger.this.priority.comparable <= priority.comparable) {
                if (!useANSI)
                    ps.println(prefix + Instant.now().toString() + " " + s);
                else
                    ps.println(new Color.Rainbow().append(prefix).of(Instant.now().toString(), Color.ANSI_YELLOW)
                            .append(" ").append(s).toString());
            }
        });
    }


    private String forgeName() {
        String s = "[" +
                name
                + "] ";

        return useANSI ? Color.color(s, Color.ANSI_BLUE) : s;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String msg) {
        print(Resources.Trace_Prefix, forgeName() + msg, priority);

    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String msg) {
        print(Resources.Debug_Prefix, forgeName() + msg, priority);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        print(Resources.Log_Prefix, forgeName() + msg, priority);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        print(Resources.Warning_Prefix, forgeName() + msg, priority);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        print(Resources.Error_Prefix, forgeName() + msg, priority);

    }
}
