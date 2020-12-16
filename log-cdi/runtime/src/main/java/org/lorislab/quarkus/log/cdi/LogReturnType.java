package org.lorislab.quarkus.log.cdi;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public interface LogReturnType {

    default List<Class<?>> instanceOfClasses() { return Collections.emptyList(); }

    default List<Class<?>> assignableClasses() {
        return Collections.emptyList();
    }

    BiFunction<ReturnContext, Object, Object> function();
}

