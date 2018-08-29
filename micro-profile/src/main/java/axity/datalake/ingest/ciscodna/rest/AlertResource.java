package axity.datalake.ingest.ciscodna.rest;

import axity.datalake.ingest.ciscodna.mongo.MongoRepository;
import axity.datalake.ingest.ciscodna.transform.impl.TempIssueServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("alarms")
public class AlertResource {
    private static final Logger logger = LogManager.getLogger(AlertResource.class);

    @Inject
    private MongoRepository mongoRepository;



    @GET
    @Path("/{time}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCommentsByCredit(@PathParam("time") String request) {
        logger.info("***** {}",request);
        List<Document> resultados = mongoRepository.findAllAlarms();
        return Response.ok(resultados).build();
    }

    @GET
    @Path("/tickets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTickets() {
        List<Document> resultados = mongoRepository.findAllTickets();
        return Response.ok(resultados).build();
    }


    @GET
    @Path("/appdynamics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        List<Document> resultados = mongoRepository.findInfoAppdyanmics();
        Document documento = new Document();
        documento.append("hostId","timbrado");
        documento.append("so","Linux");
        documento.append("data",resultados);
        return Response.ok(documento).build();
    }
}
