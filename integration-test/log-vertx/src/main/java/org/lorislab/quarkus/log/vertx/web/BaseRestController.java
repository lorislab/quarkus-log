package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;

import static org.lorislab.quarkus.log.vertx.web.BaseRestController.APPLICATION_JSON;


@ApplicationScoped
@RouteBase(path = "test", produces = APPLICATION_JSON)
public class BaseRestController {

    public static final String APPLICATION_JSON = "application/json";

    @Route(path = "get1/:id", methods = HttpMethod.GET)
    public void get1(RoutingContext rc) {
        rc.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .setStatusCode(200).end("GET"+rc.pathParam("id"));
    }

    @Route(path = "exclude/:id", methods = HttpMethod.GET)
    public void get2(RoutingContext rc) {
        rc.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .setStatusCode(200).end("GET"+rc.pathParam("id"));
    }

    @Route(path = "post1/:id", methods = HttpMethod.POST)
    public void post1(RoutingContext rc) {
        rc.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .setStatusCode(200).end("POST"+rc.pathParam("id") + rc.getBodyAsString());
    }
}
