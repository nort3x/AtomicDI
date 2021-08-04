package me.nort3x.atomic.logger;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AtomicLogger extends BasicLogger {
    private static OutputStream outputStream = System.out;

    private AtomicLogger() {
        super(outputStream);
    }

    private static AtomicLogger instance;

    public synchronized static AtomicLogger getInstance() {
        if (instance == null)
            instance = new AtomicLogger();
        return instance;
    }

    /**
     * set OutputStream of AtomicLogger Default: System.Out
     *
     * @param _outPutStream
     */
    public static void setOutputStream(OutputStream _outPutStream) {
        outputStream = _outPutStream;
    }

    public void warning(String message) {
        super.warning(message);
    }


    public void info(String message) {
        super.info(message);
    }


    public void fatal(String message, Object... arguments) {
        super.fatal(message, arguments);
    }

    public void complain_NorArgConstructor(Class<?> whichClass) {
        warning("Atomic types must contain No-Args-Constructor but " + whichClass.getName() + " does not, this can lead to Undefined Behavior");
    }

    public void complain_PostConfigurationMethodHasParameter(Class<?> whichClass, Method whichMethod) {
        warning("PostConstruction method should have no args but " + whichMethod.getName() + " at " + whichClass.getName() + " is not!, this can lead to Undefined Behavior");
    }

    public void error_unCastableAtom(Field atWhichField, Class<?> cause) {
        fatal("Wrong Atom detected at: " + atWhichField.getDeclaringClass().getName() + "." + atWhichField.getName() + " ~> defined ConcreteType is " + cause.getName() + " and its not Assignable to " + atWhichField.getType());
    }

    public void facingABug(Class<?> c, Throwable e) {
        fatal("You are facing a bug please report this at: https://github.com/nort3x/AtomicDI/issues ", e);
    }

    public void complain_AtomicNotFound(Class<?> x) {
        warning("Type " + x.getName() + " is Not Atomic But slipped into scanner scope");

    }
}
