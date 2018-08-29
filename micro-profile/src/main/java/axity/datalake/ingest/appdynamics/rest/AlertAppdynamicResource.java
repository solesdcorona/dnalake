package axity.datalake.ingest.appdynamics.rest;

import axity.datalake.ingest.AppDynamicsService;
import axity.datalake.ingest.ciscodna.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.rest.AlertResource;
import axity.datalake.ingest.ciscodna.transform.IssueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("appdynamics")
public class AlertAppdynamicResource {
    private static final Logger logger = LogManager.getLogger(AlertAppdynamicResource.class);

    @Inject
    @Named("issueAppdynamics")
    private IssueService appDynamicsService;

    @Inject
    private ClasificationDataService clasificationDataService;
    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recibeAlertfromApp( String request) {
        logger.info("***** {}",request);
        List<String> data = appDynamicsService.saveIssue(Arrays.asList(request));
        clasificationDataService.buildClasificationData(data);
        return Response.ok("okN").build();
    }
}
