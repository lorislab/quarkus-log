package org.lorislab.quarkus.log.it.rs;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.lorislab.quarkus.log.cdi.LogService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("client")
@LogService
@RegisterRestClient(configKey = "test")
public interface ClientController {

    @GET
    @Path("get")
    Response get();

    @GET
    @Path("exclude")
    Response exclude();
}
