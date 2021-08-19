package me.nort3x.atomic.logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicLogger {
    static final boolean useANSI;

    static {
        if (System.console() != null && System.getenv().get("TERM") != null || true) { // if system supports ANSI // for know :)
            Resources.Error_Prefix = Color.color(Resources.Error_Prefix, Color.ANSI_RED);
            Resources.Warning_Prefix = Color.color(Resources.Warning_Prefix, Color.ANSI_YELLOW);
            Resources.Log_Prefix = Color.color(Resources.Log_Prefix, Color.ANSI_GREEN);
            useANSI = true;
        } else
            useANSI = false;
    }


    public static String banner =
            Color.color("      .o.           .                                o8o            oooooooooo.   ooooo \n" +
                    "     .888.        .o8                                `\"'            `888'   `Y8b  `888' \n" +
                    "    .8\"888.     .o888oo  .ooooo.  ooo. .oo.  .oo.   oooo   .ooooo.   888      888  888  \n" +
                    "   .8' `888.      888   d88' `88b `888P\"Y88bP\"Y88b  `888  d88' `\"Y8  888      888  888  \n" +
                    "  .88ooo8888.     888   888   888  888   888   888   888  888        888      888  888  \n" +
                    " .8'     `888.    888 . 888   888  888   888   888   888  888   .o8  888     d88'  888  \n" +
                    "o88o     o8888o   \"888\" `Y8bod8P' o888o o888o o888o o888o `Y8bod8P' o888bood8P'   o888o \n", Color.ANSI_CYAN) + "\n\n" + Color.color(Quotes.getOne(), Color.ANSI_YELLOW) + "\n";


    final ExecutorService printThread = Executors.newSingleThreadExecutor();
    final private PrintStream ps;
    final Priority priority;

    public BasicLogger(OutputStream outputStream, Priority priority) {
        ps = new PrintStream(outputStream);
        this.priority = priority;
        System.out.println(banner);

        // dont hold strings
        Quotes.quotes = null;
        banner = null;
        System.gc();

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
        String s = "[" +
                (c.isAnonymousClass() ? "AnonymousType - " + c.getName() : c.getSimpleName())
                + "] ";

        return useANSI ? Color.color(s, Color.ANSI_BLUE) : s;
    }
}
