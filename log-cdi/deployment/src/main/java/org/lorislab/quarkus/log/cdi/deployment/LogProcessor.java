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
package org.lorislab.quarkus.log.cdi.deployment;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.jboss.logging.Logger;
import org.lorislab.quarkus.log.cdi.LogExclude;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.runtime.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public class LogProcessor {

    private static final Logger LOGGER = Logger.getLogger(LogProcessor.class);

    static final String FEATURE_NAME = "cdi-log";

    private static final DotName EXCLUDE = DotName.createSimple(LogExclude.class.getName());
    private static final DotName LOG_SERVICE = DotName.createSimple(LogService.class.getName());
    private static final DotName APPLICATION_SCOPED = DotName.createSimple(ApplicationScoped.class.getName());
    private static final DotName SINGLETON = DotName.createSimple(Singleton.class.getName());
    private static final DotName REQUEST_SCOPED = DotName.createSimple(RequestScoped.class.getName());

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem(FEATURE_NAME);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void configureRuntimeProperties(LogRecorder recorder,
                                    LogRuntimeTimeConfig logRuntimeTimeConfig,
                                    LogClassesConfigBuildItem logClassesConfigBuildItem,
                                    BeanContainerBuildItem beanContainer) {
        BeanContainer container = beanContainer.getValue();
        recorder.init(container, logRuntimeTimeConfig, logClassesConfigBuildItem.getClasses());
    }

    @BuildStep
    public void capability(LogBuildTimeConfig logBuildTimeConfig,
                           BuildProducer<CapabilityBuildItem> capability) {
        capability.produce(new CapabilityBuildItem(FEATURE_NAME));
    }

    @BuildStep
    public FeatureBuildItem build() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep
    private LogClassesConfigBuildItem registerLogClassesConfig(LogBuildTimeConfig buildTimeConfig, BeanArchiveIndexBuildItem combinedIndexBuildItem) {
        Map<String, LogClassRuntimeConfig> classes = new HashMap<>();
        IndexView index = combinedIndexBuildItem.getIndex();

        Pattern pattern = null;
        if (buildTimeConfig.exclude.isPresent()) {
            LOGGER.debug("Exclude: " + buildTimeConfig.exclude.get());
            pattern = Pattern.compile(buildTimeConfig.exclude.get());
        }
        for (ClassInfo ci : index.getKnownClasses()) {
            boolean tmp = checkClass(ci);
            if (tmp) {
                readClassInfo(index, ci, buildTimeConfig, classes, pattern);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            classes.keySet().forEach(LOGGER::debug);
        }
        return new LogClassesConfigBuildItem(classes);
    }

    private static void readClassInfo(IndexView index, ClassInfo ci, LogBuildTimeConfig config, Map<String, LogClassRuntimeConfig> classes, Pattern pattern) {
        // check packages only if the list if defined
        if (config.packages != null && !config.packages.isEmpty()) {
            Optional<String> add = config.packages.stream().filter(x -> ci.name().toString().startsWith(x)).findFirst();
            if (add.isEmpty()) {
                return;
            }
        }

        // check exclude regex
        if (pattern != null) {
            if (pattern.matcher(ci.name().toString()).matches()) {
                LOGGER.debug("Regex exclude class: " + ci.name().toString());
                return;
            }
        }

        // skip exclude classes
        if (ci.annotations().containsKey(EXCLUDE)) {
            return;
        }

        // add class config
        LogClassRuntimeConfig classConfig;
        AnnotationInstance ano = ci.classAnnotation(LOG_SERVICE);
        if (ano != null) {
            classConfig = create(index, ano);
        } else {
            classConfig = LogClassRuntimeConfig.create();
        }

        // check class methods
        boolean findMethod = findMethods(index, ci, classConfig, ci.name(), classes, config, pattern);
        DotName superClass = ci.superName();
        DotName objectClass = DotName.createSimple(Object.class.getName());
        while (superClass != null && !objectClass.equals(superClass)) {
            ClassInfo item = index.getClassByName(superClass);
            findMethod = findMethod || findMethods(index, item, classConfig, ci.name(), classes, config, pattern);
            superClass = item.superName();
        }

        if (findMethod) {
            classes.put(ci.name().toString(), classConfig);
        }
    }

    private static boolean findMethods(IndexView index, ClassInfo clazz, LogClassRuntimeConfig classConfig,
                                       DotName className, Map<String, LogClassRuntimeConfig> classes,
                                       LogBuildTimeConfig config, Pattern pattern) {
        boolean findMethod = false;
        for (MethodInfo method : clazz.methods()) {

            String methodKey = methodKey(className, method);
            boolean check = checkMethod(method, config);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Class: " + clazz + ",method:" + method + ",interceptor:");
            }
            // skip method
            if (!check) {
                continue;
            }

            // check exclude regex for method
            if (pattern != null) {
                if (pattern.matcher(methodKey).matches()) {
                    LOGGER.debug("Regex exclude method: " + methodKey);
                    continue;
                }
            }
            AnnotationInstance ano = method.annotation(LOG_SERVICE);
            if (ano != null) {
                LogClassRuntimeConfig methodConfig = create(index, ano);
                classes.put(methodKey, methodConfig);
            } else {
                classes.put(methodKey, classConfig);
            }
            findMethod = true;
        }
        return findMethod;
    }

    private static LogClassRuntimeConfig create(IndexView index, AnnotationInstance ano) {
        LogClassRuntimeConfig classConfig = LogClassRuntimeConfig.create();
        ano.valuesWithDefaults(index).forEach(a -> {
            switch (a.name()) {
                case "enabled":
                    classConfig.enabled = a.asBoolean();
                    break;
                case "stacktrace":
                    classConfig.stacktrace = a.asBoolean();
                    break;
            }
        });
        return classConfig;
    }

    @BuildStep
    public void interceptorBinding(
            LogBuildTimeConfig config,
            LogClassesConfigBuildItem logClassesConfigBuildItem,
            BuildProducer<AnnotationsTransformerBuildItem> annotationsTransformerBuildItemBuildProducer) {

        annotationsTransformerBuildItemBuildProducer.produce(new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return kind == AnnotationTarget.Kind.METHOD;
            }

            public void transform(TransformationContext context) {
                MethodInfo mi = context.getTarget().asMethod();
                if (checkMethod(mi, config)) {
                    ClassInfo ci = mi.declaringClass();
                    if (logClassesConfigBuildItem.getClasses().containsKey(methodKey(ci.name(), mi))) {
                        context.transform().add(LogService.class).done();
                    }
                }
            }
        }));
    }

    private static String methodKey(DotName className, MethodInfo method) {
        return className + "." + method.name();
    }

    private static boolean checkClass(ClassInfo classInfo) {
        if (classInfo.classAnnotation(APPLICATION_SCOPED) != null) {
            return true;
        }
        if (classInfo.classAnnotation(SINGLETON) != null) {
            return true;
        }
        if (classInfo.classAnnotation(REQUEST_SCOPED) != null) {
            return true;
        }
        return false;
    }

    private static boolean checkMethod(MethodInfo method, LogBuildTimeConfig config) {
        // skip other check for @LogService annotation
        if (method.annotation(LOG_SERVICE) != null) {
            return true;
        }
        // skip method which contains @LogExclude
        if (method.annotation(EXCLUDE) != null) {
            return false;
        }
        // ignore static method
        if (!config.staticMethod) {
            if (Modifier.isStatic(method.flags())) {
                return false;
            }
        }
        // skip constructor
        if ("<init>".equals(method.name())) {
            return false;
        }
        // ignore none public methods
        if (config.onlyPublicMethod) {
            if (!Modifier.isPublic(method.flags())){
                return false;
            }
        }
        return true;
    }
}

