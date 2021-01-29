package net.darkhax.sasit.io;

import java.io.PrintStream;

import org.apache.logging.log4j.Logger;

import net.darkhax.sasit.handler.ConfigurationHandler;
import net.minecraftforge.fml.common.TracingPrintStream;

/**
 * PrintStream which filter it's input and redirects it's output to a given logger.
 *
 * @author Arkan
 */
public class TracingPrintStreamFilterable extends TracingPrintStream {

    protected Logger logger;
    protected int BASE_DEPTH = 3;

    public TracingPrintStreamFilterable (Logger logger, PrintStream stream) {
        super(logger, stream);
        this.logger = logger;
    }

    @Override
    public void println (Object o) {
        // We can't simply call .println(String s) because it will break .getPrefix()
        String s = String.valueOf(o);
        if (!ConfigurationHandler.requiresFiltering(s)) {
            logger.info("{}{}", getPrefix(), s);
        }
    }

    @Override
    public void println (String s) {

        if (!ConfigurationHandler.requiresFiltering(s)) {
            logger.info("{}{}", getPrefix(), s);
        }
    }

    @Override
    public void print (String s) {

        if (!ConfigurationHandler.requiresFiltering(s)) {
            super.print(s);
        }
    }
    
    /**
     * @see TracingPrintStream.getPrefix()
     */
    private String getPrefix() {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        StackTraceElement elem = elems[BASE_DEPTH]; // The caller is always at BASE_DEPTH, including this call.
        if (elem.getClassName().startsWith("kotlin.io.")) {
            elem = elems[BASE_DEPTH + 2]; // Kotlins IoPackage masks origins 2 deeper in the stack.
        } else if (elem.getClassName().startsWith("java.lang.Throwable")) {
            elem = elems[BASE_DEPTH + 4];
        }
        return "[" + elem.getClassName() + ":" + elem.getMethodName() + ":" + elem.getLineNumber() + "]: ";
    }
}