package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.bootstrap.logging.InitialConfigurator;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.ConsoleHandler;
import org.jboss.logmanager.handlers.WriterHandler;
import org.junit.jupiter.api.BeforeAll;

import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.stream.Stream;

public abstract class AbstractTest {

    protected static StringWriter writer = new StringWriter();
    private static WriterHandler handler;

    static {
//        System.setProperty("java.util.logging.manager", org.jboss.logmanager.LogManager.class.getName());

//        Formatter formatter = new PatternFormatter("%d{HH:mm:ss,SSS} %-5p [%c{3.}] %s%e%n");
//        handler = new WriterHandler();
//        handler.setFormatter(formatter);
//        handler.setLevel(Level.INFO);
//        handler.setWriter(writer);
//        InitialConfigurator.DELAYED_HANDLER.addHandler(handler);
    }

    @BeforeAll
    static void setUp() {
//        Stream.of(InitialConfigurator.DELAYED_HANDLER.getHandlers()).forEach(x -> System.out.println(x.getClass()));
        Formatter formatter = InitialConfigurator.DELAYED_HANDLER.getHandlers()[0].getFormatter();
        handler = new WriterHandler();
        handler.setFormatter(formatter);
        handler.setWriter(writer);
        InitialConfigurator.DELAYED_HANDLER.addHandler(handler);
    }

    protected String[] logLines() {
        handler.flush();
        String[] tmp = writer.toString().split("\n");
        writer.getBuffer().setLength(0);
        writer.getBuffer().trimToSize();
        return tmp;
    }
}
