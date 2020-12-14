package org.lorislab.quarkus.log.it.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("test")
public class TestRestController {

    @Inject
    TestService service;

    @GET
    @Path("get")
    public Response get() {
        return Response.ok(service.test1()).build();
    }

    @GET
    @Path("exclude")
    public Response exclude() {
        return Response.ok(service.test1()).build();
    }

    @POST
    @Path("post/{p}")
    public Response post(@PathParam("p") String p, String body) {
        return Response.ok(service.post(p, body)).build();
    }
}
