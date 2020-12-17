package org.lorislab.quarkus.log.it.cdi;

import test.ExternalService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("model")
public class ModelRestController {

    @Inject
    ModelService modelService;

    @GET
    @Path("{p}")
    public Response model(@PathParam("p") String p) {
        Model m = modelService.model(new Model(p));
        return Response.ok(m.param).build();
    }

    @GET
    @Path("sub/{p}/{p2}")
    public Response subModel(@PathParam("p") String p, @PathParam("p2") String p2) {
        SubModel m = modelService.subModel(new SubModel(p, p2));
        return Response.ok(m.param).build();
    }

    @GET
    @Path("string/{p}")
    public Response modelToString(@PathParam("p") String p) {
        ModelToString m = modelService.modeToString(new ModelToString(p));
        return Response.ok(m.param).build();
    }
}
