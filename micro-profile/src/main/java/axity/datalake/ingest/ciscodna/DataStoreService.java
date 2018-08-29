package axity.datalake.ingest.ciscodna;

import java.util.List;

public interface DataStoreService {

    List<String> saveDNACenterData(List<String> json);


    List<String> getDocumentJson(List<String> json);
}
