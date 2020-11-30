/*
 * Copyright 2020 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.quarkus.log.cdi.interceptor;

import org.lorislab.quarkus.log.LogExclude;
import org.lorislab.quarkus.log.LogParamValue;
import org.lorislab.quarkus.log.LogReturnType;
import org.lorislab.quarkus.log.ReturnContext;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Logger builder service.
 */
@LogExclude
@Singleton
public class LogParamValueService {

    /**
     * Gets the map of the mapping function for the class.
     */
    public static Map<Class<?>, Function<Object, String>> INSTANCE_OF = new ConcurrentHashMap<>(JavaTypesLogParamValue.classes());

    /**
     * Gets the map of the mapping function for the assignable class.
     */
    public static Map<Class<?>, Function<Object, String>> ASSIGNABLE_FROM = new HashMap<>(JavaTypesLogParamValue.assignableFrom());

    public static Map<Class<?>, BiFunction<ReturnContext, Object, Object>> RETURN_TYPES = new HashMap<>();

    public static Map<Class<?>, BiFunction<ReturnContext, Object, Object>> RETURN_ASSIGNABLE_TYPES = new HashMap<>();

    @Inject
    @Any
    Instance<LogReturnType> returnTypes;

    @Inject @Any
    Instance<LogParamValue> parameterTypes;

    public void init() {
        if (parameterTypes != null) {
            Map<Class<?>, LogParamValue> classes = new HashMap<>();
            Map<Class<?>, LogParamValue> assignable = new HashMap<>();
            parameterTypes.forEach(p -> priority(p, classes, assignable));
            classes.forEach((x, p) -> INSTANCE_OF.put(x, p.function()));
            assignable.forEach((x, p) -> ASSIGNABLE_FROM.put(x, p.function()));
        }

        if (returnTypes != null) {
            returnTypes.forEach(r -> {
                r.assignableClasses().forEach(x -> RETURN_ASSIGNABLE_TYPES.put(x, r.function()));
                r.instanceOfClasses().forEach(x -> RETURN_TYPES.put(x, r.function()));
            });
        }
    }

    private void priority(LogParamValue p, Map<Class<?>, LogParamValue> classes, Map<Class<?>, LogParamValue> assignable) {
        priority(p, p.classes(), classes);
        priority(p, p.assignableClasses(), assignable);
    }

    private void priority(LogParamValue p, List<Class<?>> classes, Map<Class<?>, LogParamValue> target) {
        classes.forEach(x -> {
            LogParamValue v = target.get(x);
            if (v != null) {
                if (p.priority() > v.priority()) {
                    target.put(x, p);
                }
            } else {
                target.put(x, p);
            }
        });
    }

    /**
     * Gets the method parameter value.
     *
     * @param parameter the method parameter.
     * @return the value from the parameter.
     */
    public String getParameterValue(Object parameter) {
        if (parameter != null) {
            Class<?> clazz = parameter.getClass();
            Function<Object, String> fn = INSTANCE_OF.get(clazz);
            if (fn != null) {
                return fn.apply(parameter);
            }

            for (Map.Entry<Class<?>, Function<Object, String>> entry : ASSIGNABLE_FROM.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    Function<Object, String> fn2 = entry.getValue();
                    INSTANCE_OF.put(clazz, fn2);
                    return fn2.apply(parameter);
                }
            }
        }
        return "" + parameter;
    }

    public BiFunction<ReturnContext, Object, Object> returnType(Object result) {
        if (result != null) {
            Class<?> clazz = result.getClass();
            BiFunction<ReturnContext, Object, Object> fn = RETURN_TYPES.get(clazz);
            if (fn != null) {
                return fn;
            }

            for (Map.Entry<Class<?>, BiFunction<ReturnContext, Object, Object>> entry : RETURN_ASSIGNABLE_TYPES.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    BiFunction<ReturnContext, Object, Object> fn2 = entry.getValue();
                    RETURN_TYPES.put(clazz, fn2);
                    return fn2;
                }
            }
        }
        return null;
    }
}
