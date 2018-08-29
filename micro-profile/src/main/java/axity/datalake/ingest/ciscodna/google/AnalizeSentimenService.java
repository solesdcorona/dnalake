package axity.datalake.ingest.ciscodna.google;

import axity.datalake.ingest.ciscodna.to.SentimentVO;

public interface AnalizeSentimenService {

    SentimentVO analizaText(String text);
}
