package org.lorislab.quarkus.log.it.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("client")
public class ClientTestRestController {

    @GET
    @Path("get")
    public Response get() {
        return Response.ok("CLIENT").build();
    }

    @GET
    @Path("exclude")
    public Response exclude() {
        return Response.ok("CLIENT").build();
    }
}
