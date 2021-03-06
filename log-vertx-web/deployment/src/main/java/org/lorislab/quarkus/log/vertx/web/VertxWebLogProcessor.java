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
package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.vertx.http.deployment.VertxWebRouterBuildItem;

public class VertxWebLogProcessor {

    static final String FEATURE_NAME = "log-vertx-web";

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void configureRuntimeProperties(VertxWebLogRecorder recorder,
                                    BeanContainerBuildItem beanContainer, VertxWebRouterBuildItem router,
                                    VertxWebLogRuntimeTimeConfig logRuntimeTimeConfig) {
        BeanContainer container = beanContainer.getValue();
        recorder.endpoint(container, router.getRouter(), logRuntimeTimeConfig);
    }

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature){
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

}

