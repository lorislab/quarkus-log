package org.lorislab.quarkus.log.it;


import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.util.stream.Stream;

public abstract class AbstractTest {

    private static final ByteArrayOutputStream BUFFER = new ByteArrayOutputStream();

    private static final PrintStream REAL_OUT = System.out;

    static {
        System.setOut(new PrintStream(BUFFER) {
            @Override
            public void write(int b) {
                REAL_OUT.write(b);
                super.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                REAL_OUT.write(b);
                super.write(b);
            }

            @Override
            public void write(byte[] buf, int off, int len) {
                REAL_OUT.write(buf, off, len);
                super.write(buf, off, len);
            }
        });
    }

    @BeforeEach
    protected void logReset() {
        BUFFER.reset();
    }

    protected String[] logLines() {
        try {
            BUFFER.flush();
            String[] tmp = BUFFER.toString().split("\n");
            BUFFER.reset();
            Stream.of(tmp).forEach(x -> System.out.println("> " + x));
            return tmp;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
