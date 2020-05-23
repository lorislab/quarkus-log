# quarkus-log

Quarkus log extension

[![License](https://img.shields.io/github/license/lorislab/quarkus-log?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/lorislab/quarkus-log/build/master?logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/actions?query=workflow%3Abuild)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/lorislab/quarkus-log?logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.quarkus/log-parent?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.quarkus/log-parent)

Add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-cdi</artifactId>
    <version>0.1.0</version>
</dependency>
```
The standard log method for each parameter is the `toString()` Java method.
You can overwrite this and define a static Java method with annotation `@LogParam`. 
```java
    @LogParam(classes = {Transaction.class}, priority = 100)
    public static String logMessage(Object message) {
        return "~t~";
    }

    @LogParam(assignableFrom = {RoutingContext.class})
    public static String logRoutingContext(Object message) {
        RoutingContext r = (RoutingContext) message;
        return r.normalisedPath();
    }
```
#### Create a release
```bash
mvn semver-release:release-create
```

### Create a patch branch
```bash
mvn semver-release:patch-create -DpatchVersion=x.x.0
```