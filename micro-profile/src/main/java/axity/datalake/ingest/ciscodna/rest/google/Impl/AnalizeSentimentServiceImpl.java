package axity.datalake.ingest.ciscodna.rest.google.Impl;

import axity.datalake.ingest.ciscodna.rest.google.AnalizeSentimenService;
import axity.datalake.ingest.ciscodna.rest.to.SentimentVO;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.language.v1.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.InputStream;

public class AnalizeSentimentServiceImpl implements AnalizeSentimenService {

    private static final Logger logger = LogManager.getLogger(AnalizeSentimentServiceImpl.class);

    private Float maxNegative= -1.0F;
    private Float minNegative= -0.25F;

    private Float maxMiidle= -0.25F;
    private Float minMiddle= 0.25F;

    @Override
    public SentimentVO analizaText(String text) {

        CredentialsProvider credentialsProvider=null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("google/datastore-31eef6296a0d.json");

            credentialsProvider
                = FixedCredentialsProvider.create(
                ServiceAccountCredentials.fromStream(inputStream));


            LanguageServiceSettings.Builder languageServiceSettingsBuilder
                = LanguageServiceSettings.newBuilder();

            LanguageServiceSettings languageServiceSettings =
                languageServiceSettingsBuilder.setCredentialsProvider(credentialsProvider).build();


            try (LanguageServiceClient language = LanguageServiceClient.create(languageServiceSettings)) {

                // The text to analyze
                //String text = "Hello, world!";
                Document doc = Document.newBuilder()
                    .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

                // Detects the sentiment of the text
                AnalyzeSentimentResponse analyze = language.analyzeSentiment(doc);
                Sentiment sentiment = analyze.getDocumentSentiment();
                analyze.getSentencesList().forEach(s->{

                    float score = s.getSentiment().getScore();
                    logger.info(" score {} is less {} ={} or grather {} {}" ,score,maxMiidle,(score<=maxMiidle),minMiddle,(score<minMiddle));
                });
                long negative = analyze.getSentencesList().stream().map(se->se.getSentiment()).
                    filter(sent->maxNegative<=sent.getScore()&& sent.getScore()<minNegative).count();
                long middle = analyze.getSentencesList().stream().map(se -> se.getSentiment()).
                    filter(sent -> maxMiidle <= sent.getScore() &&  sent.getScore()<minMiddle).count();
                //Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

                logger.info(" conteo negativo {} middle {}",negative,middle);
                System.out.printf("Text: %s%n", text);
                System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
                SentimentVO sentimentvo = new SentimentVO();
                sentimentvo.setMiddle(middle);
                sentimentvo.setNegative(negative);
                sentimentvo.setScore(sentiment.getScore());
                return sentimentvo;
            } catch (IOException ex) {
                logger.error("Error al recuperar las llaves de Google ", ex);
            }
        }catch (Exception e){
            logger.error("Error analizando {}",e);
        }

        return null;
    }
}
