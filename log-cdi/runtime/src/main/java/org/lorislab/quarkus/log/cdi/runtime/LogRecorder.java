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
package org.lorislab.quarkus.log.cdi.runtime;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.lorislab.quarkus.log.cdi.interceptor.LogParamValueService;

import javax.enterprise.inject.Default;
import java.text.MessageFormat;
import java.util.Map;

/**
 * The logger builder interface.
 */
@Recorder
public class LogRecorder {

    public void init(BeanContainer container) {
        LogParamValueService logParamValueService = container.instance(LogParamValueService.class, Default.Literal.INSTANCE);
        logParamValueService.init();
    }

    public void config(Map<String, LogClassRuntimeConfig> classes) {
        LogConfig.config(classes);
    }

    public void config(LogRuntimeTimeConfig config) {
        LogConfig.config(config);
    }
}
