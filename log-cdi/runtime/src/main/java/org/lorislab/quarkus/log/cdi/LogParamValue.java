package org.lorislab.quarkus.log.cdi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface LogParamValue {

    /**
     * The list of classes for which is this mapping method.
     *
     * @return the list of classes for which is this mapping method.
     */
    default List<Class<?>> classes()  {
        return Collections.emptyList();
    }

    /**
     * The list of  assignable classes for which is this mapping method.
     *
     * @return the list of  assignable classes for which is this mapping method.
     */
    default List<Class<?>> assignableClasses()  {
        return Collections.emptyList();
    }

    /**
     * The priority of this method.
     *
     * @return the priority of this method.
     */
    default int priority() { return 0; }

    Function<Object, String> function();

    static LogParamValue assignable(Function<Object, String> fun, Class<?> ... clazz) {
        return assignable(fun, 0, clazz);
    }

    static LogParamValue assignable(Function<Object, String> fun, int priority, Class<?> ... clazz) {
        return new LogParamValue() {
            @Override
            public List<Class<?>> assignableClasses() {
                return Arrays.asList(clazz);
            }

            @Override
            public Function<Object, String> function() {
                return fun;
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }

    static LogParamValue instanceOf(Function<Object, String> fun, Class<?> ... clazz) {
        return instanceOf(fun, 0, clazz);
    }

    static LogParamValue instanceOf(Function<Object, String> fun, int priority, Class<?> ... clazz) {
        return new LogParamValue() {
            @Override
            public List<Class<?>> classes() {
                return Arrays.asList(clazz);
            }

            @Override
            public Function<Object, String> function() {
                return fun;
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }
}

