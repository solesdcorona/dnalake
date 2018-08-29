package axity.chatbot.api.rest;

import axity.chatbot.api.service.MeetingBotService;
import axity.chatbot.api.to.DisponibilityTO;
import axity.chatbot.api.to.IntentTO;
import axity.chatbot.api.to.SkypeMeetingTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("meetingbot")
public class MeetingResource {


    private static final Logger logger = LogManager.getLogger(MeetingResource.class);

    @Inject
    private MeetingBotService meetingBotService;
    private Gson gson = new Gson();


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recibeAlertfromApp(List<IntentTO> request) {
        Set<IntentTO> intentTOSet= new HashSet(request);
        List<IntentTO> request2 = new ArrayList<>(intentTOSet);
        logger.info("***** {}", request2);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
        List<DisponibilityTO> disponibility = meetingBotService.getDisponibility(request2);
        return Response.ok(disponibility).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newMeeting(String meeting) {
        logger.info("***** {}", meeting);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
        String data = meetingBotService.saveOrUpdateMeeting(meeting);
        return Response.ok(data).build();
    }

    @POST
    @Path("/webex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newWebex(String meeting) {
        logger.info("Webex {}", meeting);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
         String json  = meetingBotService.buildWebex(meeting);
        return Response.ok(json).build();
    }

    @POST
    @Path("/skype")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newSkype(String meeting) {
        logger.info("Skype {}", meeting);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
        String json  = meetingBotService.buildSkype(meeting);
        logger.info("Skype response{}", json);
        return Response.ok(json).build();
    }

    @POST
    @Path("/mailmeeting")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newMailMeeting(String meeting) {
        logger.info("MailMeeting {}", meeting);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
        String json  = meetingBotService.buildMeetingOutlook(meeting);
        logger.info("MailMeeting response{}", json);
        return Response.ok(json).build();
    }

    @POST
    @Path("/coworker")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response coworker(String meeting) {
        logger.info("***** {}", meeting);
        String data = meetingBotService.saveSearchCoworker(meeting);


        return Response.ok(data).build();
    }


}
