package axity.datalake.ingest.appdynamics.clientrest.impl;

import axity.datalake.ingest.appdynamics.clientrest.ControllerService;
import axity.datalake.ingest.appdynamics.clientrest.to.AttributeTO;
import axity.datalake.ingest.appdynamics.clientrest.to.ProjectTO;
import axity.datalake.ingest.appdynamics.clientrest.to.ServerTO;
import axity.datalake.ingest.appdynamics.service.to.ColumnValueTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.MetricData;
import org.appdynamics.appdrestapi.data.MetricDatas;
import org.appdynamics.appdrestapi.data.MetricValue;
import org.appdynamics.appdrestapi.data.MetricValues;

import javax.enterprise.inject.Default;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Default
public class ControllerServiceImpl implements ControllerService {

    private static final Logger logger = LogManager.getLogger(ControllerServiceImpl.class);

    @Override
    public List<ColumnValueTO> invokeRestService(ProjectTO projectTO) {
        logger.info("invocando projecto {}",projectTO.getId());
        //projectTO.getServers().forEach(s->{
            return getAllAttributes(projectTO.getServers().get(0));
        //});
    }


    private List<ColumnValueTO> getAllAttributes(ServerTO serverTO){
        logger.info("invocando servicios {}",serverTO.getId());
        String urlbase = serverTO.getUrlbase();
        List<ColumnValueTO> column = new ArrayList<>();
        List<CompletableFuture<ColumnValueTO>> complateble = new ArrayList<>();



        serverTO.getAttributes().forEach((AttributeTO a) ->{

            CompletableFuture<ColumnValueTO> future = CompletableFuture.supplyAsync(()->{
                ColumnValueTO columnValueTO= new ColumnValueTO();
                logger.info(" get vendor by ide {}",a.getId());
               Date today = new Date();
               Calendar before = Calendar.getInstance();
               before.add(Calendar.DAY_OF_MONTH,-10);
               logger.info(" ============= "+before.getTime());
                try {
                    String restService = urlbase.concat(a.getRest().concat(a.getMetricpath()));
                    logger.info("get {}",restService);
                    RESTAccess access=new RESTAccess("kofaxity.saas.appdynamics.com",
                        "443",true,"kofaxity","vh1pwo0099gp","kofaxity");
                    MetricDatas metric = access.getRESTGenericMetricQuery(a.getRest(),
                        a.getMetricpath(),
                        before.getTimeInMillis(), today.getTime(), false
                    );
                    //logger.info(" ***+" +metric.getMetric_data().size());
                    MetricData data = metric.getMetric_data().get(0);
                    logger.info(" ***+" +data.getMetricId());
                    logger.info(" ***+" +data.getMetricPath());
                    ArrayList<MetricValues> metricValues = data.getMetricValues();
                    logger.info(" *** size " +metricValues.size());
                    metricValues.forEach(mv->{
                        MetricValue sv = mv.getSingleValue();
                        logger.info("@@ -aa" + mv.getSingleValue());
                        logger.info("@@ -bb size " + mv.getMetricValue().size());
                        logger.info("@@ -bb " + sv.getValue());
                        logger.info("@@ -bb " + sv.getCurrent());
                        columnValueTO.setValueColumn(String.valueOf(sv.getValue()));
                    });
                    columnValueTO.setIndexColumn(a.getIndex());

                    /*HttpClient client = HttpClient.newBuilder()
                        .authenticator(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("kofaxity@kofaxity", "vh1pwo0099gp".toCharArray());
                            }
                        })
                        .build();

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(restService)).
                            header("Authorization","Basic a29mYXhpdHlAa29mYXhpdHk6dmgxcHdvMDA5OWdw").
                            header("User-Agent","Java Client")
                        .GET()
                        .build();
                    HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandler.asString());
                    String json =response.body();
                    logger.info("json: {}",json);
                    */
                }catch (Exception e){
                    logger.error("Error al obtener las metricas ",e);
                }
                return columnValueTO;
            });
            complateble.add(future);
        });
        CompletableFuture current =CompletableFuture.allOf(complateble.toArray(new CompletableFuture[complateble.size()]));
        logger.info("wait...");
        current.join();
        logger.info("done...");

        complateble.forEach(c->{
            try {
                column.add(c.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return column;
    }
}
