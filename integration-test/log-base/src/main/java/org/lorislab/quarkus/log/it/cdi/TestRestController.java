package org.lorislab.quarkus.log.it.cdi;

import test.ExternalService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("test")
public class TestRestController {

    @Inject
    TestService service;

    @Inject
    ExternalService externalService;

    @Inject
    ExtTestService extTestService;

    @GET
    @Path("1")
    public Response test1() {
        extTestService.start(100L);
        return Response.ok(service.test1()).build();
    }

    @GET
    @Path("2")
    public Response test2() {
        return Response.ok(service.test1() + "|" + service.test2()).build();
    }

    @GET
    @Path("3")
    public Response test3() {
        return Response.ok(externalService.external()).build();
    }

    @GET
    @Path("param1/{p}")
    public Response param1(@PathParam("p") String p) {
        return Response.ok(service.param1(p)).build();
    }

    @GET
    @Path("static/{p}")
    public Response staticMethod(@PathParam("p") String p) {
        return Response.ok(TestService.staticMethod(p)).build();
    }

    @GET
    @Path("static2/{p}")
    public Response staticMethod2(@PathParam("p") String p) {
        return Response.ok(TestStaticService.staticMethod(p)).build();
    }
}
