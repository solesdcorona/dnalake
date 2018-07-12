package axity.datalake.ingest.ciscodna.rest.remedy.impl;

import axity.datalake.ingest.ciscodna.rest.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.rest.remedy.RemedyService;
import axity.datalake.ingest.ciscodna.rest.to.RemedyTO;
import com.google.gson.Gson;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RemedyServiceImpl implements RemedyService {
    private static final Logger logger = LogManager.getLogger(RemedyServiceImpl.class);

    private Gson gson = new Gson();


    @Override
    public void pushData(RemedyTO remedyTO) {
        HttpClient client = HttpClient.newBuilder().build();



        HttpRequest request = null;
        try {
            String postJson = gson.toJson(remedyTO);
            logger.info("push Remedy {}",postJson);
            request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:28337/api/tickets"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyProcessor.fromString(postJson))
                .build();
            logger.info("esperando ...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
            logger.info("respuesta {}",response.body());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}