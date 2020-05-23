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
import org.lorislab.quarkus.log.cdi.LogParam;
import org.lorislab.quarkus.log.cdi.LogParamValue;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.interceptor.LogParamValueService;
import org.lorislab.quarkus.log.cdi.runtime.LogBuildTimeConfig;
import org.lorislab.quarkus.log.cdi.runtime.LogRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class LogProcessor {

    static final String FEATURE_NAME = "cdi-log";

    private static final String LOG_BUILDER_SERVICE = LogParamValueService.class.getName();

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
    void configureRuntimeProperties(LogRecorder recorder, BeanContainerBuildItem beanContainer) {
        BeanContainer container = beanContainer.getValue();
        recorder.init(container);
    }

    @BuildStep
    @Record(STATIC_INIT)
    void build(BuildProducer<FeatureBuildItem> feature, LogRecorder recorder) throws Exception {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    public AnnotationsTransformerBuildItem interceptorBinding() {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return !buildConfig.disable && kind == AnnotationTarget.Kind.CLASS;
            }

            public void transform(TransformationContext context) {
                ClassInfo target = context.getTarget().asClass();
                Map<DotName, List<AnnotationInstance>> tmp = target.annotations();
                Optional<DotName> dot = ANNOTATION_DOT_NAMES.stream().filter(tmp::containsKey).findFirst();
                if (dot.isPresent()) {
                    String name = target.name().toString();
                    Optional<String> add = buildConfig.packages.stream().filter(name::startsWith).findFirst();
                    if (add.isPresent() && !LOG_BUILDER_SERVICE.equals(name)) {
                        context.transform().add(LogService.class).done();
                    }
                }
            }
        });
    }

}

