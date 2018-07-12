package axity.datalake.ingest.ciscodna.rest.impl;

import axity.datalake.ingest.ciscodna.rest.CiscoDNACenterRestClient;
import axity.datalake.ingest.ciscodna.rest.to.ApiTO;
import axity.datalake.ingest.ciscodna.rest.to.LoginTO;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class CiscoDNACenterRestClientImpl implements CiscoDNACenterRestClient{

    private static final Logger logger = LogManager.getLogger(CiscoDNACenterRestClientImpl.class);

    private String token = null;

    private Gson gson = new Gson();

    private TrustManager[] noopTrustManager = new TrustManager[]{
        new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };

    @Override
    public void invokeLogin(LoginTO loginTO)  {
        try {
          SSLContext sc = SSLContext.getInstance("ssl");
            sc.init(null, noopTrustManager, null);

            HttpClient client = HttpClient.newBuilder().sslContext(sc).authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(loginTO.getUsername(), loginTO.getPassword().toCharArray());
                }
            }).build();


            byte[] base64 = Base64.getEncoder().encode((loginTO.getUsername().concat(":").concat(loginTO.getPassword()).getBytes()));

            logger.info("base {}", new String(base64));
            HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(loginTO.getUrl()))
                        .header("Authorization","Basic ".concat(new String(base64)))
                        .POST(HttpRequest.BodyProcessor.fromString(""))
                        .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());

            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            token = gson.fromJson(jsonObject.get("Token"),String.class);
            String json =response.body();
            logger.info("json: {}{}",json,token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> invokeApi(ApiTO api) {
        try {
            List<String> jsonBody = new ArrayList<>();
            SSLContext sc = SSLContext.getInstance("ssl");
            sc.init(null, noopTrustManager, null);
            JsonParser jsonParser = new JsonParser();
            api.getServices().forEach(s->{
                HttpClient client = HttpClient.newBuilder().sslContext(sc).build();
                HttpRequest request = null;
                try {
                    StringBuilder querys=new StringBuilder();
                    if(s.getQuery()!=null&& s.getQuery().size()>1){
                        querys.append("?");
                        final int[] index = {1};
                        s.getQuery().forEach((k,v)->{
                            if(index[0] >1){
                                querys.append("&");
                            }
                            String value =(v!=null&&v.length()>1)?"=".concat(v):"";
                            querys.append(k.concat(value));
                            index[0]++;
                        });
                    }
                    String url = api.getUrlbase().concat(s.getUrl()).concat(querys.toString());
                    logger.info("url {}",url);
                    HttpRequest.Builder requestRaw = HttpRequest.newBuilder()
                        .uri(new URI(url));
                    requestRaw.header("X-Auth-Token",token);
                    s.getHeaders().forEach((k,v)->{
                        logger.info(" k {} v {}",k,v);
                        requestRaw.header(k,v);
                    });

                    request =requestRaw.GET().build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
                    String body =response.body();
                    //logger.info("json: {}",body);

                    JsonObject jsonObj =(JsonObject) jsonParser.parse(body);
                    JsonElement jreponse = jsonObj.get("response");
                    if(jreponse.isJsonObject()){
                        jreponse=jsonObj.get("response").getAsJsonObject();
                    }else if(jreponse.isJsonArray()){
                        jreponse=jsonObj.get("response").getAsJsonArray();
                    }
                    JsonObject newJson = new JsonObject();
                    newJson.add("data",jreponse);
                    newJson.addProperty("service",s.getUrl());
                    jreponse = newJson;
                    //logger.info("json: {}",jreponse);
                    String resp =jreponse.toString();
                        //jsonObj.get("latestHealthScore").getAsString();//gson.fromJson(jsonObj.get("response"),String.class);
                    logger.info("json: {}",resp);
                    jsonBody.add(resp);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return jsonBody;
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }
}
