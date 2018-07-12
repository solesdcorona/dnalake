package axity.datalake.ingest.ciscodna.rest.remedy.impl;

import axity.datalake.ingest.ciscodna.rest.google.AnalizeSentimenService;
import axity.datalake.ingest.ciscodna.rest.google.DataStoreGCPService;
import axity.datalake.ingest.ciscodna.rest.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.rest.remedy.RemedyService;
import axity.datalake.ingest.ciscodna.rest.to.RemedyTO;
import axity.datalake.ingest.ciscodna.rest.to.SentimentVO;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.Sentiment;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClassificationDataServiceImpl implements ClasificationDataService {
    private static final Logger logger = LogManager.getLogger(ClassificationDataServiceImpl.class);

    @Inject
    @Named("mongoServices")
    private DataStoreGCPService storeGCPService;


    @Inject
    private RemedyService remedyService;

    @Inject
    private AnalizeSentimenService analizeSentimenService;


    public  CredentialsProvider credentialsProvider;

    @Override
    public void buildClasificationData(List<String> ids) {

        List<String> documents = storeGCPService.getDocumentJson(ids);
        JsonParser jsonParser = new JsonParser();
        documents.forEach(d->{
            JsonObject json =(JsonObject) jsonParser.parse(d);
            String service = json.get("body").getAsJsonObject().get("service").getAsString();

            switch (service){
                case "api/v1/discovery/6/network-device":
                    discoveryService(json);
                    break;
                case "b":
                    break;
            }


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
            CompletableFuture<Void> future
                = CompletableFuture.runAsync(() -> {
                try {
                    remedyService.pushData(r);
                }catch (Exception e){
                    logger.error("Error en mongo any desk",e);
                }
            });
        });
        //remedyService.pushData(remedis.get(0));
    }


    @Override
    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }
}
