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
package org.lorislab.quarkus.log.rs;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Build configuration.
 */
@ConfigGroup
public class RestClientLogRuntimeTimeConfig {

    /**
     * Enable java types.
     */
    @ConfigItem(name = "priority", defaultValue = "100")
    public int priority;

    /**
     * Log message configuration
     */
    @ConfigItem(name = "message")
    public RestLogMessageRuntimeConfig message = new RestLogMessageRuntimeConfig();
}
