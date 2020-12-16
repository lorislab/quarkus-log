package org.lorislab.quarkus.log.cdi;

public interface ReturnContext {

   void errorContext(Throwable t);

    void closeContext(Object value);
}
