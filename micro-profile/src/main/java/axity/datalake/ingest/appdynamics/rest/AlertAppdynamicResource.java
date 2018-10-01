package axity.datalake.ingest.appdynamics.rest;

import axity.datalake.ingest.AppDynamicsService;
import axity.datalake.ingest.ciscodna.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.rest.AlertResource;
import axity.datalake.ingest.ciscodna.transform.IssueService;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recibeAlertfromApp( String request) {
        logger.info("***** {}",request);
        try {
            List<String> data = appDynamicsService.saveIssue(Arrays.asList(request));
            clasificationDataService.buildClasificationData(data);
            return Response.ok("okN").build();
        }catch (Exception e){
            logger.error("Error al consultar app ",e);
        }
        return Response.ok("Error ").build();
    }

    @POST
    @Path("remedy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response invokeRemedy(String request) {
        logger.info("***** {}",request);
        HttpClient client = HttpClient.newBuilder().build();
        try {
            logger.info("push Remedy {}",request);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:28337/api/tickets"))
                    .headers("Content-Type", "application/json;charset=UTF-8")
                    .POST(HttpRequest.BodyProcessor.fromString(request))
                    .build();
            logger.info("esperando ...");
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandler.asString());
            logger.info("respuesta {}",response.body());
            return Response.ok(response.body()).build();
        }catch (Exception e){
            logger.error("Error al consultar app ",e);
        }
        return Response.ok("Error ").build();
    }
}
