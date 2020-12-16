package org.lorislab.quarkus.log.it.mutiny;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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
    @Path("uni/{p}")
    public Response uni(@PathParam("p") String param) {
        String tmp = service.uni(param).await().indefinitely();
        return Response.ok(tmp).build();
    }

    @GET
    @Path("multi/{p}")
    public Response multi(@PathParam("p") String param) throws InterruptedException {
        String tmp = service.multi(param).collectItems().last().await().indefinitely();
        return Response.ok(tmp).build();
    }

}
