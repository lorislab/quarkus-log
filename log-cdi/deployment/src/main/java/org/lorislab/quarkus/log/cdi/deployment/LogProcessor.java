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
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.lorislab.quarkus.log.LogExclude;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.lorislab.quarkus.log.cdi.runtime.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class LogProcessor {

    static final String FEATURE_NAME = "cdi-log";

    private static final DotName EXCLUDE = DotName.createSimple(LogExclude.class.getName());

    private static final DotName LOG_SERVICE = DotName.createSimple(LogService.class.getName());

    private static final List<DotName> ANNOTATION_DOT_NAMES = List.of(
        DotName.createSimple(ApplicationScoped.class.getName()),
        DotName.createSimple(Singleton.class.getName()),
        DotName.createSimple(RequestScoped.class.getName())
    );

    LogBuildTimeConfig buildConfig;

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem(FEATURE_NAME);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void configureRuntimeProperties(LogRecorder recorder,
                                    LogRuntimeTimeConfig logRuntimeTimeConfig,
                                    BeanContainerBuildItem beanContainer) {
        recorder.config(logRuntimeTimeConfig);
        BeanContainer container = beanContainer.getValue();
        recorder.init(container);
    }

    @BuildStep
    @Record(STATIC_INIT)
    void build(BuildProducer<FeatureBuildItem> feature, LogRecorder recorder,
               LogClassesConfigBuildItem logClassesConfigBuildItem){
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
        recorder.config(logClassesConfigBuildItem.getClasses());
    }

    @BuildStep
    private LogClassesConfigBuildItem registerLogClassesConfig(BeanArchiveIndexBuildItem combinedIndexBuildItem) {

        Map<String, LogClassRuntimeConfig> classes = new HashMap<>();

        IndexView index = combinedIndexBuildItem.getIndex();
        for (ClassInfo ci : index.getKnownClasses()) {
            Optional<AnnotationInstance> a = ci.classAnnotations().stream().filter(x -> ANNOTATION_DOT_NAMES.contains(x.name())).findFirst();
            if (a.isPresent()) {
                readClassInfo(index, ci, buildConfig.packages, classes);
            }
        }

        classes.forEach((k,v) -> {
            System.out.println(k + " -> " + v.log + " - " + v.stacktrace);
        });
        return new LogClassesConfigBuildItem(classes);
    }

    private static void readClassInfo(IndexView index, ClassInfo ci, List<String> packages, Map<String, LogClassRuntimeConfig> classes) {
        // check packages
        Optional<String> add = packages.stream().filter(x -> ci.name().toString().startsWith(x)).findFirst();
        if (add.isEmpty()) {
            return;
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
        String className = ci.name().toString();
        boolean findMethod = findMethods(index, ci, classConfig, className, classes);
        DotName superClass = ci.superName();
        DotName objectClass = DotName.createSimple(Object.class.getName());
        while (superClass != null && !objectClass.equals(superClass)) {
            ClassInfo item = index.getClassByName(superClass);
            findMethods(index, item, classConfig, className, classes);
            superClass = item.superName();
        }

        if (findMethod) {
            classes.put(ci.name().toString(), classConfig);
        }
    }

    private static boolean findMethods(IndexView index, ClassInfo clazz, LogClassRuntimeConfig classConfig, String className, Map<String, LogClassRuntimeConfig> classes) {
        boolean findMethod = false;
        for (MethodInfo method : clazz.methods()) {

            if (Modifier.isStatic(method.flags())) {
                continue;
            }
            if (!Modifier.isPublic(method.flags())) {
                continue;
            }
            if ("<init>".equals(method.name())) {
                continue;
            }
            if (method.annotation(EXCLUDE) != null) {
                continue;
            }

            AnnotationInstance ano = method.annotation(LOG_SERVICE);
            if (ano != null) {
                LogClassRuntimeConfig methodConfig = create(index, ano);
                classes.put(className + "." + method.name(), methodConfig);
            } else {
                classes.put(className + "." + method.name(), classConfig);
            }
            findMethod = true;
        }
        return findMethod;
    }

    private static LogClassRuntimeConfig create(IndexView index, AnnotationInstance ano) {
        LogClassRuntimeConfig classConfig = LogClassRuntimeConfig.create();
        ano.valuesWithDefaults(index).forEach(a -> {
            switch (a.name()) {
                case "log":
                    classConfig.log = a.asBoolean();
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
            LogClassesConfigBuildItem logClassesConfigBuildItem,
            BuildProducer<AnnotationsTransformerBuildItem> annotationsTransformerBuildItemBuildProducer) {

        annotationsTransformerBuildItemBuildProducer.produce(new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return !buildConfig.disable && kind == AnnotationTarget.Kind.METHOD;
            }

            public void transform(TransformationContext context) {
                MethodInfo mi = context.getTarget().asMethod();
                if (Modifier.isStatic(mi.flags())) {
                    return;
                }
                if (!Modifier.isPublic(mi.flags())) {
                    return;
                }
                if (mi.annotation(EXCLUDE) != null) {
                    return;
                }
                ClassInfo ci = mi.declaringClass();
                if (logClassesConfigBuildItem.getClasses().containsKey(ci.name() + "." + mi.name())) {
                    context.transform().add(LogService.class).done();
                }
            }
        }));
    }

}

