package axity.datalake.ingest.ciscodna.remedy;

import com.google.api.gax.core.CredentialsProvider;

import  java.util.List;

public interface ClasificationDataService {


    void buildClasificationData(List<String> data);

    void setCredentialsProvider(CredentialsProvider credentialsProvider);

}
