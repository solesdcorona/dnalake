package axity.datalake.ingest.ciscodna.rest.google;

import axity.datalake.ingest.ciscodna.rest.to.SentimentVO;
import com.google.cloud.language.v1.Sentiment;

public interface AnalizeSentimenService {

    SentimentVO analizaText(String text);
}
