package org.lorislab.quarkus.log.it.mutiny;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("mutiny")
public class MutinyRestController {

    @Inject
    MutinyService service;

    @GET
    @Path("{p}")
    public Response test1(@PathParam("p") String param) {
        String tmp = service.test1(param).await().indefinitely();
        return Response.ok(tmp).build();
    }

}
