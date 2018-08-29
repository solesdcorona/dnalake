package axity.datalake.ingest.common.impl;

import axity.datalake.ingest.ciscodna.to.ApiTO;
import axity.datalake.ingest.common.ConfigService;
import axity.datalake.ingest.appdynamics.clientrest.to.ProjectTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonConfigServiceImpl implements ConfigService{

    private static final Logger logger = LogManager.getLogger(JsonConfigServiceImpl.class);


    @Override
    public ProjectTO getConfigFile() {

        try{

            String path = "json-appdynamics-michoacan.json";
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(path);
            logger.info(" inputStrem {}",inputStream.available());
            BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));
            /*String line = "";
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null) {
                sb.append(line);
                System.out.println(line);
            }
            */
            //InputStreamReader bufferedReader = new BufferedInputStream(reader);
            Gson gson = new Gson();
            ProjectTO json = gson.fromJson(reader, ProjectTO.class);
            logger.info("valor json object {}",json);
            return json;
        }catch (Exception e){
            logger.error("Error al abrir el archivo",e);
        }
    return null;
    }



    @Override
    public <T> T getConfigFile(Class<T> clazz, String nameConfigFile, String namespace) {
        try{
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(nameConfigFile);
            logger.info(" inputStrem {}",inputStream.available());
            BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson( reader, JsonObject.class);
            logger.info("json {}",jsonObject.get(namespace));
            T json = gson.fromJson(jsonObject.get(namespace), clazz);
            logger.info("valor json object {}",json);
            return json;
        }catch (Exception e){
            logger.error("Error al abrir el archivo",e);
        }
        return null;
    }
    @Override
    public ApiTO getConfigFile(String nameConfigFile, String namespace) {
        try{
            ApiTO apiTO = new ApiTO();
            List<ApiTO.ServicesTO> services= new ArrayList<>();
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(nameConfigFile);
            logger.info(" inputStrem {}",inputStream.available());
            BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson( reader, JsonObject.class);//root
            JsonObject subjsonApi = jsonObject.get(namespace).getAsJsonObject();
            logger.info("json {}",subjsonApi);//apis
            apiTO.setUrlbase(Optional.ofNullable(subjsonApi.get("urlbase")).map(s->s.getAsString()).orElse(""));
            JsonArray subjsonServices = subjsonApi.getAsJsonObject().get("services").getAsJsonArray();//services

            int size =subjsonServices.size();
            ApiTO.ServicesTO servicesTO = null;

            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Type typeQuery = new TypeToken<Map<String, String>>(){}.getType();

            for (int i = 0;i<size;i++){
                servicesTO = apiTO.new ServicesTO();
                JsonObject subJsonSer = subjsonServices.get(i).getAsJsonObject();
                servicesTO.setSystem(Optional.ofNullable(subJsonSer.get("system")).map(s->s.getAsString()).
                    orElse(null));
                servicesTO.setUrl(subJsonSer.get("url").getAsString());
                servicesTO.setUrlbase(Optional.ofNullable(subJsonSer.get("urlbase")).map(s->s.getAsString()).orElse(""));
                servicesTO.setMethod(Optional.ofNullable(subJsonSer.get("method")).map(s->s.getAsString()).orElse(""));
                servicesTO.setBody(Optional.ofNullable(subJsonSer.get("body")).map(s->s.getAsJsonObject().toString()).orElse(""));
                List<String> response = new ArrayList<>();
                logger.info(" reponse {}",subJsonSer.get("response"));
                subJsonSer.get("response").getAsJsonArray().forEach(s->{
                    response.add(s.getAsString());
                });
                servicesTO.setResponse(response);
                JsonElement jsonheader = subJsonSer.get("headers");//headers
                logger.info("headers {}",jsonheader);

                Map<String, String> map = gson.fromJson(jsonheader, type);
                servicesTO.setHeaders(map);

                JsonElement jsonquery = subJsonSer.get("query");//headers
                logger.info("query {}",jsonquery);
                Map<String, String> mapQuery = gson.fromJson(jsonquery, typeQuery);
                servicesTO.setHeaders(map);
                servicesTO.setQuery(mapQuery);

                services.add(servicesTO);
            }
            apiTO.setServices(services);
            logger.info("valor json object {}",apiTO);
            return apiTO;
        }catch (Exception e){
            logger.error("Error al abrir el archivo",e);
        }
        return null;
    }
}
