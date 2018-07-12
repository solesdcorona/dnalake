package axity.datalake.ingest.ciscodna.rest.google.Impl;

import axity.datalake.ingest.ciscodna.rest.google.DataStoreGCPService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import javax.inject.Named;
import javax.print.Doc;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Named("anyDeskMongo")
public class MongoAnyDeskServiceImpl implements DataStoreGCPService {
    private static final Logger logger = LogManager.getLogger(MongoAnyDeskServiceImpl.class);


    private Gson gson = new Gson();

    @Override
    public List<String> saveDNACenterData(List<String> json) {
        List<String>  id= new ArrayList<>();
        System.setProperty("DEBUG.MONGO", "true");

// Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");
        //MongoClient mongoClient = new MongoClient(new ServerAddress("127.0.0.1", 27017), Arrays.asList(credential));
        MongoClientURI uri = new MongoClientURI("mongodb://0.tcp.ngrok.io:12550/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("prime");
        MongoCollection<Document> coll = database.getCollection("alarms");

        //json.forEach(j->{

        Document doc = Document.parse(json.get(1));//recuperamos el 2do

        JsonObject jsonDoc = gson.fromJson(doc.toJson(), JsonObject.class);
        logger.info("any desk {}",jsonDoc);
        JsonArray jsonArray = jsonDoc.getAsJsonObject().get("data").getAsJsonArray();
        SimpleDateFormat simple = new SimpleDateFormat();
        simple.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        jsonArray.forEach(obj->{
            Document docPadre = new Document();
            JsonObject docDevice = obj.getAsJsonObject();
            Document docAlarms = new Document();
            docAlarms.append("@displayName","988221");
            docAlarms.append("@id","988221");
            docAlarms.append("acknowledgementStatus",false);

            String dateFormat = simple.format(new Date());
            docAlarms.append("alarmFoundAt",dateFormat);
            docAlarms.append("alarmId",988221);

            Document category = new Document();
            category.append("ordinal",168438038);
            category.append("value","Routers");

            docAlarms.append("category",category);

            Document condition = new Document();
            condition.append("ordinal",168438038);
            condition.append("value",docDevice.get("reachabilityStatus").getAsString());

            docAlarms.append("condition",condition);
            docAlarms.append("deviceName",docDevice.get("managementIpAddress").getAsString());
            docAlarms.append("deviceTimestamp",dateFormat);
            docAlarms.append("lastUpdatedAt",dateFormat);

            docAlarms.append("nttyaddrss7_address",docDevice.get("managementIpAddress").getAsString());


            docAlarms.append("message",Optional.ofNullable(docDevice.get("reachabilityFailureReason")).map(s->s.getAsString()).orElse("Success"));

            docAlarms.append("source",docDevice.get("managementIpAddress").getAsString());
            docAlarms.append("timeStamp",dateFormat);
            docAlarms.append("wirelessSpecificAlarmId","LINK:DOWN"+docDevice.get("managementIpAddress").getAsString());

            String reachabilityStatus =docDevice.get("reachabilityStatus").getAsString();
            String value = "";
            if(reachabilityStatus.toLowerCase().startsWith("su")){
                value="CLEARED";
            }else{
                value="CRITICAL";
            }


            docAlarms.append("severity",value);

            docPadre.append("@dtoType","dnaCenterDTO");
            docPadre.append("@type","Alarms");
            docPadre.append("@url",doc.get("service"));
            docPadre.append("alarmsDTO",docAlarms);
            coll.insertOne(docPadre);
            id.add(docPadre.get("_id").toString());
        });

            //Document docDevice= Document.parse(doc.get("data", Document.class).toJson());

        //});
        mongoClient.close();
        return id;
    }

    @Override
    public List<String> getDocumentJson(List<String> json) {
        return null;
    }
}
