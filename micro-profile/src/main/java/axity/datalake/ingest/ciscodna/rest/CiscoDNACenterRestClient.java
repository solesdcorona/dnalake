package axity.datalake.ingest.ciscodna.rest;

import axity.datalake.ingest.ciscodna.rest.to.ApiTO;
import axity.datalake.ingest.ciscodna.rest.to.LoginTO;

import java.io.IOException;
import java.util.List;

public interface CiscoDNACenterRestClient {

    void invokeLogin(LoginTO loginTO);

    List<String> invokeApi(ApiTO api);
}
