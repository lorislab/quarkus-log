package org.lorislab.quarkus.log.it.concurrent;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("concurrent")
public class ConcurrentRestController {

    @Inject
    ConcurrentService service;

    @GET
    @Path("{p}")
    public Response test1(@PathParam("p") String param) {
        service.test1(param).join();
        return Response.ok(param).build();
    }

}
