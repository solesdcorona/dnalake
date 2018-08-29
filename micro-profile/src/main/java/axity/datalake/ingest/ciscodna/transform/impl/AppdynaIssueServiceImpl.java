package axity.datalake.ingest.ciscodna.transform.impl;

import axity.datalake.ingest.ciscodna.transform.IssueBase;
import axity.datalake.ingest.ciscodna.transform.IssueService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;

@Named("issueAppdynamics")
public class AppdynaIssueServiceImpl extends IssueBase implements IssueService {
    private static final Logger logger = LogManager.getLogger(AppdynaIssueServiceImpl.class);

    private Gson gson = new Gson();
    @Override
    public List<String> saveIssue(List<String> json) {

        JsonObject appAlert = gson.fromJson(json.get(0), JsonObject.class);
        List<String> jsondocs = new ArrayList<>();
        System.setProperty("DEBUG.MONGO", "true");

        // Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");
        //MongoClient mongoClient = new MongoClient(new ServerAddress("127.0.0.1", 27017), Arrays.asList(credential));

        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        MongoClient mongoClient = new MongoClient(uri);

        /*Consultar query */
        MongoDatabase database = mongoClient.getDatabase("datalake");
        MongoCollection<Document> dnacenter = database.getCollection("dnacenter");



        AggregateIterable<Document> resultDocs = dnacenter.aggregate(Arrays.asList(

            match(Filters.eq("body.service", "sim/v2/user/metrics/query/machines")),
            sort(new Document("date",-1)),
            limit(1),
            project(new Document("metric", new Document("$objectToArray", "$body.data.data.2.4964.metricData")).append("_id",0)),
            unwind("$metric"),
            unwind("$metric.v.simMeasurementDtos"),//new Document("$avg","$metric.v.simMeasurementDtos.metricValue.value")
            group(new Document("name","$metric.k"),Accumulators.avg("promedio","$metric.v.simMeasurementDtos.metricValue.value")),
            project(new Document("_id",0).
                append("name","$_id.name")
                .append("promedio","$promedio")
                .append("tipo",new Document("$arrayElemAt",Arrays.asList(
                    new Document("$split",Arrays.asList("$_id.name","|")),1)
                )))

        ));


        List<Document> docs = new ArrayList<>();
        resultDocs.forEach((Block<? super Document>) d->{
            logger.info(" json {}",d.toJson());
            Document doc =null;
            String name = d.get("name", String.class);
            if(d.get("tipo", String.class).equals("Memory")&& !name.contains("Total")){

                if(d.get("promedio", Double.class)>30 ){
                    doc = createAlarm(d.get("tipo", String.class),
                        d.get("promedio", Double.class),
                        d.get("name", String.class), "timbrado2",
                        "sim/v2/user/metrics/query/machines",appAlert.get("metric").getAsString(),
                        appAlert.get("incidencia").getAsString());
                    docs.add(doc);
                }
            }

            //logger.info(" json",);
        });



        database = mongoClient.getDatabase("prime");
        dnacenter = database.getCollection("alarms");
        docs.forEach(d -> {
            logger.info(" json {}", d.toJson());
            jsondocs.add(d.toJson());
        });
        dnacenter.insertMany(docs);

        //id.addAll(docs.stream().map(d->d.get("_id").toString()).collect(Collectors.toList()));
        //docs.addAll(docs.stream().map(d->d.toJson()).collect(Collectors.toList()));
        //logger.info("size {}", id.size());
        Calendar day = Calendar.getInstance();
        LocalDateTime dateTime = LocalDateTime.now();

        String asCustomPattern = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String bsCustomPattern = dateTime.format(DateTimeFormatter.ofPattern("ha"));
        Document tickets = new Document();
        tickets.append("counter",1);
        tickets.append("datetime",asCustomPattern);
        tickets.append("hour",bsCustomPattern);
        tickets.append("originday",asCustomPattern);

        dnacenter = database.getCollection("aggregate");
        dnacenter.insertOne(tickets);



        return jsondocs;
    }


    private Document createAlarm(String tipo, Double promedio,String name,String serverName,String service,String metric,String urlMessage) {

        SimpleDateFormat simple = new SimpleDateFormat();
        simple.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //jsonArray.forEach(obj -> {
        Document docPadre = new Document();
        Document docAlarms = new Document();
        docAlarms.append("@displayName", "1000121");
        docAlarms.append("@id", "988221");
        docAlarms.append("acknowledgementStatus", false);

        String dateFormat = simple.format(new Date());
        docAlarms.append("alarmFoundAt", dateFormat);
        docAlarms.append("alarmId", System.currentTimeMillis());

        Document category = new Document();
        category.append("ordinal", 168438038);
        category.append("value", "Appdynamics Server");

        docAlarms.append("category", category);

        Document condition = new Document();
        condition.append("ordinal", 168438038);
        condition.append("value", "Reacheable");

        docAlarms.append("condition", condition);
        docAlarms.append("deviceName", serverName);
        docAlarms.append("deviceTimestamp", dateFormat);
        docAlarms.append("lastUpdatedAt", dateFormat);

        docAlarms.append("nttyaddrss7_address", "172.16.30.4");

        String message = "ok";
        if(tipo.equals("Memory")){
            if(promedio>20){
                message="nok";
            }
        }



        String reachabilityStatus = message;
        docAlarms.append("message",String.format("%s with values %s in %s",metric,promedio,tipo));
        docAlarms.append("urlMessage", urlMessage);
        docAlarms.append("source", "");
        docAlarms.append("timeStamp", dateFormat);
        docAlarms.append("wirelessSpecificAlarmId", "SERVER-"+ tipo.toUpperCase() +":DOWN" + serverName);


        String value = "";
        if (reachabilityStatus.toLowerCase().startsWith("nok")) {
            value = "CRITICAL";
        } else {
            value = "CLEARED";
        }


        docAlarms.append("severity", value);

        docPadre.append("@dtoType", "dnaCenterDTO");
        docPadre.append("@type", "Alarms");
        docPadre.append("@url", service);
        docPadre.append("alarmsDTO", docAlarms);
        return docPadre;
    }
}
