package axity.datalake.ingest.ciscodna;

import axity.datalake.ingest.ciscodna.to.ApiTO;
import axity.datalake.ingest.ciscodna.to.LoginTO;

import java.util.List;

public interface CiscoDNACenterRestClient {

    void invokeLogin(LoginTO loginTO);

    List<String> invokeApi(ApiTO api);
}
