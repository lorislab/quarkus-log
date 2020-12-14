package org.lorislab.quarkus.log.it.rs;

import org.eclipse.microprofile.rest.client.inject.RestClient;

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

    @Inject
    @RestClient
    ClientController client;

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

    @GET
    @Path("client/get")
    public Response clientGet() {
        return Response.fromResponse(client.get()).build();
    }

    @GET
    @Path("client/exclude")
    public Response clientExclude() {
        return Response.fromResponse(client.exclude()).build();
    }

}
