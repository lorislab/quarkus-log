package org.lorislab.quarkus.log.cdi.runtime;

import io.quarkus.builder.item.SimpleBuildItem;

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
