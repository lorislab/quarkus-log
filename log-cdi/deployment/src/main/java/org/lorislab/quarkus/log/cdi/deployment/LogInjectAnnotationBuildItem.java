package org.lorislab.quarkus.log.cdi.deployment;

import io.quarkus.builder.item.MultiBuildItem;


public final class LogInjectAnnotationBuildItem extends MultiBuildItem {

    private Class<?> annotation;

    public LogInjectAnnotationBuildItem(Class<?> annotation) {
        this.annotation = annotation;
    }

    public Class<?> getAnnotation() {
        return annotation;
    }
}
