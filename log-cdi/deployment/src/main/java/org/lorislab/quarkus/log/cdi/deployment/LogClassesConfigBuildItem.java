package org.lorislab.quarkus.log.cdi.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import org.lorislab.quarkus.log.cdi.runtime.LogClassRuntimeConfig;

import java.util.Map;

public final class LogClassesConfigBuildItem extends SimpleBuildItem {

    private final Map<String, LogClassRuntimeConfig> classes;

    public LogClassesConfigBuildItem(Map<String, LogClassRuntimeConfig> classes) {
        this.classes = classes;
    }

    public Map<String, LogClassRuntimeConfig> getClasses() {
        return classes;
    }

}
