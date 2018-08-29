package axity.datalake.ingest.common;

import axity.datalake.ingest.appdynamics.clientrest.to.ProjectTO;
import axity.datalake.ingest.ciscodna.to.ApiTO;

public interface ConfigService {


    ProjectTO getConfigFile();

    <T> T  getConfigFile(Class<T> clazz,String nameConfigFile, String nameSpace);

    ApiTO getConfigFile(String nameConfigFile, String namespace);
}
