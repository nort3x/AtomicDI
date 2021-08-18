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


        String banner =
                "\t\t⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣿⣿⣿⣿⢿⣻⣫⢯⣗⣗⣗⣗⣗⡷⡽⣝⡯⣟⢿⢿⣿⣿⣿⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣿⢿⢝⣞⣗⢷⡽⣽⣺⣳⢯⣾⣺⡽⣽⣳⢯⢯⢯⢯⢞⢿⢿⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⢿⣕⡯⣟⣞⡾⣽⢽⣳⣟⣾⣻⣞⣷⣻⣽⣞⣯⢿⡽⣽⢽⢽⢵⣻⣿⣿⣿\n" +
                        "\t\t⣿⣿⢯⣳⣳⢽⣳⢯⡯⣿⣽⣻⡾⣷⢿⣞⣿⣞⣷⣟⣾⢯⣯⢷⣻⡽⣳⢧⡻⣿⣿\n" +
                        "\t\t⣿⣏⣗⢷⢽⡽⣞⣯⣿⣳⢟⣷⢿⣻⣽⣟⣾⣻⣾⣻⢾⢿⣞⣯⣷⣻⢽⣳⣝⣝⣿\n" +
                        "\t\t⣿⣺⣪⢿⢽⡽⣯⢷⡯⢐⠄⠄⣻⣟⣷⢿⣽⣻⡞⠠⡂⠄⢻⣗⣷⢯⣟⣞⡮⣖⣿\n" +
                        "\t\t⣿⡺⣺⢽⢯⣻⣽⢯⣧⠐⠐⡀⣺⣽⣾⢿⣽⣯⣇⢐⠠⠈⣼⣟⣾⣻⣺⣵⡻⡮⣿\n" +
                        "\t\t⣿⡺⡽⡽⡯⣷⣻⣻⣽⣾⣲⢾⣽⢷⣟⣿⣳⣯⡿⣶⢶⣽⣻⣾⣳⡯⣷⡳⣯⡳⣻\n" +
                        "\t\t⣿⡺⣝⡯⡿⡽⣞⣯⣷⢿⣽⢿⣽⢿⣽⢷⣟⣷⢿⣻⡿⣽⢷⣻⢾⣽⣳⢯⣗⢝⣿\n" +
                        "\t\t⣿⣝⣞⡽⡽⡯⣟⣷⠉⡁⠅⠡⠁⠅⠡⠁⠅⠡⢉⠨⠈⠌⡉⣿⢽⣞⣞⣗⡗⣽⣿\n" +
                        "\t\t⣿⣿⡼⣺⢽⢯⣟⣞⡷⡶⡾⡶⣷⢾⡶⣷⢾⣶⢶⣶⢷⡶⡾⣽⢽⣺⢞⡞⣼⣿⣿\n" +
                        "\t\t⣿⣿⣿⣮⢯⣳⡳⡯⡯⣟⣟⣯⢿⡽⣯⣟⣿⣺⡯⣯⡯⣯⢿⢽⢽⡺⡹⣼⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣷⣧⡫⡯⡯⣗⣟⡾⣽⢽⣳⢯⢾⣺⢽⣳⢯⢯⢯⢏⢇⣿⣾⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣿⣿⣿⣾⣽⣕⡳⡫⣗⢟⢾⣝⢯⢯⢻⢪⡫⣽⣼⣾⣿⣿⣿⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣾⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                        "\t\t⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿";
        System.out.println(banner);
    }

    final ExecutorService printThread = Executors.newSingleThreadExecutor();
    final private PrintStream ps;
    final Priority priority;

    public BasicLogger(OutputStream outputStream, Priority priority) {
        ps = new PrintStream(outputStream);
        this.priority = priority;
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
