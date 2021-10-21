package me.nort3x.atomic.logger;

import me.nort3x.atomic.logger.enums.Color;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.logger.res.Quotes;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.*;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class AtomicLogger implements ILoggerFactory {


    public static String banner =
            Color.color(
                    "      .o.           .                                o8o            oooooooooo.   ooooo \n" +
                            "     .888.        .o8                                `\"'            `888'   `Y8b  `888' \n" +
                            "    .8\"888.     .o888oo  .ooooo.  ooo. .oo.  .oo.   oooo   .ooooo.   888      888  888  \n" +
                            "   .8' `888.      888   d88' `88b `888P\"Y88bP\"Y88b  `888  d88' `\"Y8  888      888  888  \n" +
                            "  .88ooo8888.     888   888   888  888   888   888   888  888        888      888  888  \n" +
                            " .8'     `888.    888 . 888   888  888   888   888   888  888   .o8  888     d88'  888  \n" +
                            "o88o     o8888o   \"888\" `Y8bod8P' o888o o888o o888o o888o `Y8bod8P' o888bood8P'   o888o \n", Color.ANSI_CYAN) + "\n\n" + Color.color(Quotes.getOne(), Color.ANSI_YELLOW) + "\n\n";


    private static OutputStream outputStream = System.out;
    private static String quote;
    private static boolean useEmbeddedLogger = true;
    static boolean silence = false;
    static ILoggerFactory factory = new ILoggerFactory() {
        @Override
        public Logger getLogger(String name) {
            return LoggerFactory.getLogger(name);
        }
    };

    private AtomicLogger() {
        if (!silence) {
            System.out.print(banner);
        }
        Quotes.quotes = null;
        banner = null;
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

    @Override
    public Logger getLogger(String name) {
        return loggerProvider(name, Priority.DEBUG);
    }

    public Logger getLogger(Class<?> c, Priority p) {
        return loggerProvider((c.isAnonymousClass() ? "AnonymousType - " + c.getName() : c.getSimpleName()), p);
    }

    public Logger getLogger(Class<?> c) {
        return loggerProvider((c.isAnonymousClass() ? "AnonymousType - " + c.getName() : c.getSimpleName()), Priority.DEBUG);
    }

    public Logger getLogger(String name, Priority p) {
        return loggerProvider(name, p);
    }

    private Logger loggerProvider(String name, Priority p) {
        if (silence)
            return new LoggerAdaptor(); // total nop
        if (useEmbeddedLogger)
            new BasicLogger(new PrintStream(outputStream), p, name);

        try {
            if (ClassLoader.getSystemResources("org/slf4j/impl/StaticLoggerBinder.class").hasMoreElements())
                factory.getLogger(name);
        } catch (IOException ignored) {

        }
        return new BasicLogger(new PrintStream(outputStream), p, name);
    }

    public static void setSilent(boolean silence) {
        AtomicLogger.silence = silence;
    }

    public static void setBanner(String banner) {
        AtomicLogger.banner = banner;
    }

    public static void setQuote(String quote) {
        AtomicLogger.quote = quote;
    }

    public static void setForceUseEmbeddedLogger(boolean useEmbeddedLogger) {
        AtomicLogger.useEmbeddedLogger = useEmbeddedLogger;
    }

    public static void useCustomLogger(ILoggerFactory loggerFactory) {
        AtomicLogger.factory = loggerFactory;
    }

}
