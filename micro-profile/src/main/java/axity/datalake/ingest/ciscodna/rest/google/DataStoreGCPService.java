package axity.datalake.ingest.ciscodna.rest.google;

import java.util.List;

public interface DataStoreGCPService {

    List<String> saveDNACenterData(List<String> json);


    List<String> getDocumentJson(List<String> json);
}
