package org.lorislab.quarkus.log;

public interface ReturnContext {

   void errorContext(Throwable t);

    void closeContext(Object value);
}
