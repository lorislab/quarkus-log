# quarkus-log

Quarkus log interceptor extension

[![License](https://img.shields.io/github/license/lorislab/quarkus-log?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/lorislab/quarkus-log/build/master?logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/actions?query=workflow%3Abuild)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/lorislab/quarkus-log?logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.quarkus/log-parent?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.quarkus/log-parent)

Add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-cdi</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
The standard log method for each parameter is the `toString()` Java method.
You can overwrite this and define a producer method which returns `LogParamValue` with annotation `@Produces`. 
```java
    @Produces
    public LogParamValue model() {
       return assignable((v) -> "model:" + ((Model)v).param, Model.class);
    }
```
For `mutiny` add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-mutiny</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
For `resteasy` add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-resteasy</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
For `vertx-web` add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-vertx-web</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```