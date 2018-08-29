package axity.datalake.ingest.ciscodna.remedy.impl;

import axity.datalake.ingest.ciscodna.DataStoreService;
import axity.datalake.ingest.ciscodna.google.AnalizeSentimenService;
import axity.datalake.ingest.ciscodna.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.remedy.RemedyService;
import axity.datalake.ingest.ciscodna.to.RemedyTO;
import axity.datalake.ingest.ciscodna.to.SentimentVO;
import com.google.api.gax.core.CredentialsProvider;
import com.google.gson.*;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassificationDataServiceImpl implements ClasificationDataService {
    private static final Logger logger = LogManager.getLogger(ClassificationDataServiceImpl.class);

    @Inject
    @Named("mongoServices")
    private DataStoreService storeGCPService;


    @Inject
    private RemedyService remedyService;

    @Inject
    private AnalizeSentimenService analizeSentimenService;


    public  CredentialsProvider credentialsProvider;

    @Override
    public void buildClasificationData(List<String> ids) {

        //List<String> documents = storeGCPService.getDocumentJson(ids);
        JsonParser jsonParser = new JsonParser();
        ids.forEach(d->{
            JsonObject json =(JsonObject) jsonParser.parse(d);
            logger.info("json {}",json);
            String service = json.get("@url").getAsString();
            logger.info("service {}" , service);
            switch (service){
                case "api/v1/discovery/6/network-device":
                    discoveryService(json);
                    break;
                case "api/ndp/v1/data/entities/metrics":
                    handleService(json);
                    break;
                case "sim/v2/user/metrics/query/machines":
                    handleAppdynamics(json);
                    break;
            }


        });
    }

    private void handleAppdynamics(JsonObject json) {
        JsonObject obj = json.get("alarmsDTO").getAsJsonObject();
        List<RemedyTO> remedis = new ArrayList<>();
        RemedyTO dataRemedy= new RemedyTO();
        logger.info("json : {}"+json.toString());
        dataRemedy.setTitle(obj.get("deviceName").getAsString());
        String text = obj.get("message").getAsString();
        SentimentVO sentiment =analizeSentimenService.analizaText(text);
        dataRemedy.setDescription(text +" \n" + obj.get("urlMessage").getAsString());
        dataRemedy.setAffectedClient("BMC");
        dataRemedy.setImpact(sentiment.getImpact());
        dataRemedy.setSeverity(sentiment.getUrgencia());
        remedis.add(dataRemedy);


        remedis.forEach(r->{
            //CompletableFuture<Void> future
            //  = CompletableFuture.runAsync(() -> {
            try {
                remedyService.pushData(r);
            }catch (Exception e){
                logger.error("Error en remedy any desk",e);
            }
            //});
        });
    }


    private void discoveryService(JsonObject json){
        System.setProperty("GOOGLE_API_KEY", "AIzaSyBr-KU6V5LIwIQnfX6koT5GY39e_OwbvSA");
        List<RemedyTO> remedis = new ArrayList<>();
        //logger.info("got json {}",json.get("body").getAsJsonObject().get("data"));
        JsonArray jsonArray = json.get("body").getAsJsonObject().get("data").getAsJsonArray();
        jsonArray.forEach(obj->{
            String reachabilityStatus= obj.getAsJsonObject().get("reachabilityStatus").getAsString();
            RemedyTO dataRemedy= null;
            logger.info("datos {}",obj);
            if(reachabilityStatus.toLowerCase().startsWith("su")){
                logger.info("ok");
            }else{
                dataRemedy= new RemedyTO();
                dataRemedy.setTitle(reachabilityStatus.concat(obj.getAsJsonObject().get("managementIpAddress").getAsString()));
                String text = obj.getAsJsonObject().get("reachabilityFailureReason").getAsString();
                SentimentVO sentiment =analizeSentimenService.analizaText(text);

                try {
                    TranslateOptions.newBuilder().
                        setCredentials(credentialsProvider.getCredentials()).
                        setApiKey("AIzaSyBr-KU6V5LIwIQnfX6koT5GY39e_OwbvSA")
                        .build().getService();
                } catch (IOException e) {
                   logger.error("Error al asignar credenciales",e);
                }
                Translate translate = TranslateOptions.getDefaultInstance().getService();

                Translation translation = translate.translate(text,
                    TranslateOption.sourceLanguage("en"),
                    TranslateOption.targetLanguage("es"));
                dataRemedy.setDescription(text.concat("         ").concat("(es)").concat(translation.getTranslatedText()));
                dataRemedy.setAffectedClient("BMC");
                dataRemedy.setImpact(sentiment.getImpact());
                dataRemedy.setSeverity(sentiment.getUrgencia());
                remedis.add(dataRemedy);
            }
        });



        remedis.forEach(r->{
            //CompletableFuture<Void> future
              //  = CompletableFuture.runAsync(() -> {
                try {
                    remedyService.pushData(r);
                }catch (Exception e){
                    logger.error("Error en mongo any desk",e);
                }
            //});
        });
        //remedyService.pushData(remedis.get(0));
    }

    private void handleService(JsonObject json){
        System.setProperty("GOOGLE_API_KEY", "AIzaSyBr-KU6V5LIwIQnfX6koT5GY39e_OwbvSA");
        List<RemedyTO> remedis = new ArrayList<>();
        //logger.info("got json {}",json.get("body").getAsJsonObject().get("data"));
        JsonObject obj = json.get("alarmsDTO").getAsJsonObject();

            String reachabilityStatus= obj.get("severity").getAsString();
            RemedyTO dataRemedy= null;
            logger.info("datos {}",obj);
            if(reachabilityStatus.toLowerCase().startsWith("CL")){
                logger.info("ok");
            }else{
                dataRemedy= new RemedyTO();
                dataRemedy.setTitle("Sensor Temperature device name "+obj.get("deviceName"));
                String text = obj.getAsJsonObject().get("message").getAsString();
                SentimentVO sentiment =analizeSentimenService.analizaText(text);

                try {
                    TranslateOptions.newBuilder().
                        setCredentials(credentialsProvider.getCredentials()).
                        setApiKey("AIzaSyBr-KU6V5LIwIQnfX6koT5GY39e_OwbvSA")
                        .build().getService();
                } catch (IOException e) {
                    logger.error("Error al asignar credenciales",e);
                }
                Translate translate = TranslateOptions.getDefaultInstance().getService();

                Translation translation = translate.translate(text,
                    TranslateOption.sourceLanguage("en"),
                    TranslateOption.targetLanguage("es"));
                dataRemedy.setDescription(text.concat("         ").concat("(es)").concat(translation.getTranslatedText()));
                dataRemedy.setAffectedClient("BMC");
                dataRemedy.setImpact(sentiment.getImpact());
                dataRemedy.setSeverity(sentiment.getUrgencia());
                remedis.add(dataRemedy);
            }




        remedis.forEach(r->{
            //CompletableFuture<Void> future
            //  = CompletableFuture.runAsync(() -> {
            try {
                remedyService.pushData(r);
            }catch (Exception e){
                logger.error("Error en mongo any desk",e);
            }
            //});
        });
        //remedyService.pushData(remedis.get(0));
    }

    @Override
    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }
}
