package axity.chatbot.api.rest;


import axity.chatbot.api.to.DisponibilityTO;
import axity.chatbot.api.to.IntentTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("mailwebhook")
public class MailResource {
    private static final Logger logger = LogManager.getLogger(MailResource.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recibeAlertfromApp(String request) {
        logger.info("webhook {}", request);
        /*List<String> data = meetingBotService.getDisponibility(request);
        List<DisponibilityTO> disponibility = meetingBotService.searchDisponibility(data);
        */
        //List<DisponibilityTO> disponibility = meetingBotService.getDisponibility(request2);
        return Response.ok("ok").build();
    }

}
