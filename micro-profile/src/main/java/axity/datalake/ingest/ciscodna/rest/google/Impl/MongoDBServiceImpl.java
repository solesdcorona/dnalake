package axity.datalake.ingest.ciscodna.rest.google.Impl;

import axity.datalake.ingest.ciscodna.rest.google.DataStoreGCPService;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Named("mongoServices")
public class MongoDBServiceImpl implements DataStoreGCPService {

    @PostConstruct
    private void init(){
        //Logger mongoLogger = LogManager.getLogger( "org.mongodb.driver" );
        //mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.

    }

    @Override
    public List<String> saveDNACenterData(List<String> json) {
        List<String>  id= new ArrayList<>();
        System.setProperty("DEBUG.MONGO", "true");

// Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");
        //MongoClient mongoClient = new MongoClient(new ServerAddress("127.0.0.1", 27017), Arrays.asList(credential));
        MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("datalake");
        MongoCollection<Document> coll = database.getCollection("dnacenter");

        /*List<Document> collectData = json.stream().map(j -> {
            Document doc = Document.parse(j);
            return doc;
        }).collect(Collectors.toList());
        coll.insertMany(collectData);
        */
        json.forEach(j->{
            Document docPadre = new Document();
            Document doc = Document.parse(j);
            docPadre.append("date",new Date());
            docPadre.append("body",doc);
            coll.insertOne(docPadre);
            id.add(docPadre.get("_id").toString());
        });
         mongoClient.close();
         return id;
    }

    @Override
    public List<String> getDocumentJson(List<String> json) {
        System.setProperty("DEBUG.MONGO", "true");

// Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");
        List<String> documents = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("datalake");
        MongoCollection<Document> coll = database.getCollection("dnacenter");


        List<ObjectId> listObj = json.stream().map(id -> new ObjectId(id)).collect(Collectors.toList());

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", listObj));

        FindIterable<Document> result = coll.find(query);

        result.forEach((Consumer<? super Document>) d->{
            documents.add(d.toJson());
        });
        return documents;
    }
}
