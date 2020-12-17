package org.lorislab.quarkus.log.it.cdi;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("exclude")
public class ExcludeRestController {

    @Inject
    ExcludeService excludeService;

    @Inject
    ExcludeMethodService excludeMethodService;

    @GET
    @Path("1/{p}")
    public Response exclude(@PathParam("p") String p) {
        return Response.ok(excludeService.test(p)).build();
    }

    @GET
    @Path("2/{p}")
    public Response excludeMethod(@PathParam("p") String p) {
        String tmp = excludeMethodService.excludeMethod(p);
        return Response.ok(excludeMethodService.method(p) + tmp).build();
    }
}
