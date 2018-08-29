package axity.datalake.ingest.ciscodna.transform.impl;

import axity.datalake.ingest.ciscodna.transform.IssueBase;
import axity.datalake.ingest.ciscodna.transform.IssueService;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;

@Named("issueTemp")
public class TempIssueServiceImpl extends IssueBase implements IssueService {

    private static final Logger logger = LogManager.getLogger(TempIssueServiceImpl.class);


    public static void main(String[] args) {

        // creating regression object, passing true to have intercept term
        SimpleRegression simpleRegression = new SimpleRegression(true);

        // passing data to the model
        // model will be fitted automatically by the class
        simpleRegression.addData(new double[][]{
            {1, 3},
            {2, 3},
            {3, 4},
            {4, 5},
            {5, 6}
        });

        // querying for model parameters
        System.out.println("slope = " + simpleRegression.getSlope());
        System.out.println("intercept = " + simpleRegression.getIntercept());

        // trying to run model for unknown data
        System.out.println("prediction for 1.5 = " + simpleRegression.predict(1.5));
        System.out.println("prediction for 1.5 = " + simpleRegression.predict(2.5));
        System.out.println("prediction for 1.5 = " + simpleRegression.predict(3.5));
        System.out.println("prediction for 1.5 = " + simpleRegression.predict(8.5));

    }

    @Override
    public List<String> saveIssue(List<String> a) {
        List<String> id = new ArrayList<>();
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

            match(Filters.eq("body.service", "api/ndp/v1/data/entities/metrics")),
            project(new Document("tempMax", new Document("$max", "$body.data.records.envtemp_fn")).
                append("tempMin", new Document("$min", "$body.data.records.envtemp_fn")).
                append("service", "$body.service").
                append("sensor", new Document("$arrayElemAt", Arrays.asList("$body.data.request.dimensions.value", 0)))),
            group(new Document().
                append("tempMax", "$tempMax").
                append("tempMin", "$tempMin").
                append("sensor", "$sensor").append("service", "$service"))

            //new Document("$match", new Document("_id", new ObjectId("5b566011c094c7113c6cb28f"))),
            //new Document("$sort", new Document("views.date", 1)),
            //new Document("$limit", 200),
            //new Document("$project",
            //  new Document("_id", 0)
            // .append("url", "$views.url")
            //.append("date", "$views.date"))
        ));

        List<Document> docs = new ArrayList<>();
        resultDocs.forEach((Block<? super Document>) d -> {
            logger.info("json {}", d.toJson());
            Document docId = d.get("_id", Document.class);
            logger.info(" sensor : {}, tempMin:{}  tempMax:{}", docId.get("sensor"), docId.get("tempMin"), docId.get("tempMax"));

            Document docPadre = createAlarm(docId.get("sensor").toString(), docId.get("service").toString(), docId.get("tempMin",Double.class), docId.get("tempMax",Double.class));
            docs.add(docPadre);
        });


        database = mongoClient.getDatabase("prime");
        dnacenter = database.getCollection("alarms");
        dnacenter.insertMany(docs);
        docs.forEach(d -> {
            logger.info("id : {}", d.get("_id"));
        });
        //id.addAll(docs.stream().map(d->d.get("_id").toString()).collect(Collectors.toList()));
        id.addAll(docs.stream().map(d->d.toJson()).collect(Collectors.toList()));
        logger.info("size {}", id.size());
        Calendar day = Calendar.getInstance();
        LocalDateTime dateTime = LocalDateTime.now();

        String asCustomPattern = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String bsCustomPattern = dateTime.format(DateTimeFormatter.ofPattern("ha"));
        Document tickets = new Document();
        tickets.append("counter",id.size());
        tickets.append("datetime",asCustomPattern);
        tickets.append("hour",bsCustomPattern);
        tickets.append("originday",asCustomPattern);

        dnacenter = database.getCollection("aggregate");
        dnacenter.insertOne(tickets);
        //id.add(docPadre.get("_id").toString());
        /*guardar query */


        //json.forEach(j->{

        //Document doc = Document.parse(json.get(1));//recuperamos el 2do

        //JsonObject jsonDoc = gson.fromJson(doc.toJson(), JsonObject.class);
        //logger.info("any desk {}", jsonDoc);
        //JsonArray jsonArray = jsonDoc.getAsJsonObject().get("data").getAsJsonArray();


        //});

        //Document docDevice= Document.parse(doc.get("data", Document.class).toJson());

        //});
        mongoClient.close();
        return id;
    }


    private Document createAlarm(String sensor, String service, Double tempM, Double tempMx) {

        SimpleDateFormat simple = new SimpleDateFormat();
        simple.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //jsonArray.forEach(obj -> {
        Document docPadre = new Document();
        Document docAlarms = new Document();
        docAlarms.append("@displayName", "988221");
        docAlarms.append("@id", "988221");
        docAlarms.append("acknowledgementStatus", false);

        String dateFormat = simple.format(new Date());
        docAlarms.append("alarmFoundAt", dateFormat);
        docAlarms.append("alarmId", System.currentTimeMillis());

        Document category = new Document();
        category.append("ordinal", 168438038);
        category.append("value", "Routers");

        docAlarms.append("category", category);

        Document condition = new Document();
        condition.append("ordinal", 168438038);
        condition.append("value", "Reacheable");

        docAlarms.append("condition", condition);
        docAlarms.append("deviceName", "EDGE-3850.axity.com");
        docAlarms.append("deviceTimestamp", dateFormat);
        docAlarms.append("lastUpdatedAt", dateFormat);

        docAlarms.append("nttyaddrss7_address", "172.16.30.4");


        String message = null;
        if(tempM>=3700D && tempMx>=3800D){
            message= "critical";
        }else if(tempM>=2000D && tempMx<3500D){
            message="critical" ;
        }else {
            message="low" ;
        }
        String reachabilityStatus = message;
        docAlarms.append("message",String.format("Sensor %s . Temperature is %s between  %s and  %s ", sensor, message,tempM, tempMx));

        docAlarms.append("source", "");
        docAlarms.append("timeStamp", dateFormat);
        docAlarms.append("wirelessSpecificAlarmId", "LINK:DOWN" + "EDGE-3850.axity.com");


        String value = "";
        if (reachabilityStatus.toLowerCase().startsWith("lo")) {
            value = "LOW";
        } else {
            value = "CRITICAL";
        }


        docAlarms.append("severity", value);

        docPadre.append("@dtoType", "dnaCenterDTO");
        docPadre.append("@type", "Alarms");
        docPadre.append("@url", service);
        docPadre.append("alarmsDTO", docAlarms);
        return docPadre;
    }
}
