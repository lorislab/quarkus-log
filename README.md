# quarkus-log

Quarkus log interceptor extension

[![License](https://img.shields.io/github/license/lorislab/quarkus-log?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/lorislab/quarkus-log/build/master?logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/actions?query=workflow%3Abuild)
[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/lorislab/quarkus-log?sort=semver&logo=github&style=for-the-badge)](https://github.com/lorislab/quarkus-log/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.quarkus/log-parent?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.quarkus/log-parent)

This library is a Quarkus extension which implements the CDI bean log interceptor.
Example:
```shell
[org.lor.qua.log.it.cdi.ModelService] (executor-thread-1) model(model:param1) started.
[org.lor.qua.log.it.cdi.ModelService] (executor-thread-1) model(model:param1):model:param1 [0.002s] succeed.
```
The default pattern is
```
<method_name>(<input_parameters>) started.
<method_name>(<input_parameters>):<return_type> [execution_time] succeed|failed.
```
Maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-cdi</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
The standard log method for each parameter is the `toString()` Java method.
You can overwrite this default configuration when you define a producer method which returns `org.lorislab.quarkus.log.cdi.LogParamValue` with annotation `@Produces`. 
```java
    @Produces
    public LogParamValue model() {
       return assignable(v -> "model:" + ((Model)v).param, Model.class);
    }
```
## Annotations
* `org.lorislab.quarkus.log.cdi.LogService` - bind class or method to the interceptor. Parameters:
  * `enabled` - disable or enable log. Default: `true`
  * `stacktrace` - log stacktrace of the method. Default: `true`
* `org.lorislab.quarkus.log.cdi.LogExclude` - exclude class or method from the interceptor binding
* `org.lorislab.quarkus.log.cdi.LogReplaceValue` - hide or mask value of the method parameters 

## Configuration

### Build time
* `quarkus.lorislab.log.exclude` - exclude class or method regex. Example: `(.*?)(ExcludeService.*?$|ExcludeMethodService\\.excludeMethod$)`. Default: `empty`
* `quarkus.lorislab.log.packages` - Binding the interceptor to all classes in these packages. Default: `org.lorislab`
* `quarkus.lorislab.log.only-public-method` - Binding the interceptor only to public methods. Default: `true`
* `quarkus.lorislab.log.static-method` - Binding the interceptor to static methods of the bean class. Default: `true`
### Runtime time
* `quarkus.lorislab.log.enabled` - enable or disable interceptor. Default: `true`
* `quarkus.lorislab.log."<fully-qualified name>".enabled` - disable or enable an interceptor for class. Default: `true`
* `quarkus.lorislab.log."<fully-qualified name>".stacktrace` - log stacktrace for the class. Default: `true`
* `quarkus.lorislab.log."<fully-qualified name>.<method_name>".enabled` - disable or enable an interceptor for method. Default: `true`
* `quarkus.lorislab.log."<fully-qualified name>.<method_name>".stacktrace` - log stacktrace for the method. Default: `true`
* `quarkus.lorislab.log.message.return-void` - log `void` representation. Default: `void`
* `quarkus.lorislab.log.message.start` - start log pattern. Default: `{0}({1}) started.` Indexes: 
  * `0` - method name 
  * `1` - input parameters 
* `quarkus.lorislab.log.message.succeed` - succeed log pattern. Default: `{0}({1}):{2} [{3}s] succeed.` Indexes: 
  * `0` - method name 
  * `1` - input parameters 
  * `2` - return value 
  * `3` - execution time
* `quarkus.lorislab.log.message.failed` - failed log pattern. Default: `{0}({1}):{2} [{3}s] failed.` Indexes: 
  * `0` - method name 
  * `1` - input parameters 
  * `2` - return value 
  * `3` - execution time 

## Mutiny

For `mutiny` add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-mutiny</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
Interceptor for all methods which returns the `Uni` or `Multi`.

## RestEasy

Maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-resteasy</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
Example
```shell
[org.lor.qua.log.it.rs.TestRestController] (executor-thread-1) POST http://localhost:8081/test/post/param1 [true] started.
[org.lor.qua.log.it.rs.TestRestController] (executor-thread-1) POST http://localhost:8081/test/post/param1 [0.009s] finished [200-OK,true].
```
The default pattern is
```
<http_method_name> <URI> [<entity?>] started.
<http_method_name> <URI> [execution_time] finished [<response_code>-<response>,<entity?>].
```
Rest client example
```shell
[org.lor.qua.log.rs.RestClientLogInterceptor] (executor-thread-1) GET http://localhost:8081/client/get [false] started.
[org.lor.qua.log.rs.RestClientLogInterceptor] (executor-thread-1) GET http://localhost:8081/client/get [0.004s] finished [200-OK,true].
```
The default pattern for rest-client is
```
<http_method_name> <URI> [<entity?>] started.
<http_method_name> <URI> [execution_time] finished [<response_code>-<response>,<entity?>].
```
### Configuration

* `quarkus.lorislab.log.rs.priority` - interceptor priority. Default: `100`
* `quarkus.lorislab.log.rs.enabled` - enable or disable interceptor. Default: `true`
* `quarkus.lorislab.log.rs.exclude` - exclude URI regex. Default: `empty`
* `quarkus.lorislab.log.rs.message.start` - start request log pattern. Default: `{0} {1} [{2}] started.` Indexes:
    * `0` - HTTP method name
    * `1` - URI
    * `2` - request entity `true` or `false`.
* `quarkus.lorislab.log.rs.message.succeed` - finished request log pattern. Default: `{0} {1} [{2}s] finished [{3}-{4},{5}].` Indexes:
    * `0` - method name
    * `1` - URI
    * `2` - execution time
    * `3` - response code
    * `4` - response
    * `5` - response entity `true` or `false`.
* `quarkus.lorislab.log.rs.client.priority` - rest-client interceptor priority. Default: `100`
* `quarkus.lorislab.log.rs.client.enabled` - rest-client enable or disable interceptor. Default: `true`
* `quarkus.lorislab.log.rs.client.exclude` - rest-client exclude URI regex. Default: `empty`
* `quarkus.lorislab.log.rs.client.message.start` -  rest-client start request log pattern. Default: `{0} {1} [{2}] started.` Indexes:
    * `0` - HTTP method name
    * `1` - URI
    * `2` - request entity `true` or `false`.
* `quarkus.lorislab.log.rs.client.message.succeed` -  rest-client finished request log pattern. Default: `{0} {1} [{2}s] finished [{3}-{4},{5}].` Indexes:
    * `0` - method name
    * `1` - URI
    * `2` - execution time
    * `3` - response code
    * `4` - response
    * `5` - response entity `true` or `false`.
    
## Vertx-Web

For `vertx-web` add maven dependency
```xml
<dependency>
    <groupId>org.lorislab.quarkus</groupId>
    <artifactId>log-vertx-web</artifactId>
    <version>{latest-release-version}</version>
</dependency>
```
Example
```shell
[org.lor.qua.log.ver.web.VertxWebInterceptor] (vert.x-eventloop-thread-11) POST /test/post1/p1 [false] started.
[org.lor.qua.log.ver.web.VertxWebInterceptor] (vert.x-eventloop-thread-11) POST /test/post1/p1 [0.008s] finished [200-OK,true].
```
The default pattern is
```
<http_method_name> <URI> [<entity?>] started.
<http_method_name> <URI> [execution_time] finished [<response_code>-<response>,<entity?>].
```

### Configuration

* `quarkus.lorislab.log.vertx.web.priority` - interceptor priority. Default: `100`
* `quarkus.lorislab.log.vertx.web.enabled` - enable or disable interceptor. Default: `true`
* `quarkus.lorislab.log.vertx.web.exclude` - exclude URI regex. Default: `empty`
* `quarkus.lorislab.log.vertx.web.message.start` - start request log pattern. Default: `{0} {1} [{2}] started.` Indexes:
  * `0` - HTTP method name
  * `1` - URI
  * `2` - request entity `true` or `false`.
* `quarkus.lorislab.log.vertx.web.message.succeed` - finished request log pattern. Default: `{0} {1} [{2}s] finished [{3}-{4},{5}].` Indexes:
  * `0` - method name 
  * `1` - URI 
  * `2` - execution time 
  * `3` - response code 
  * `4` - response 
  * `5` - response entity `true` or `false`. 
