package axity.datalake.ingest.ciscodna.mongo.impl;

import axity.datalake.ingest.ciscodna.google.Impl.MongoDBServiceImpl;
import axity.datalake.ingest.ciscodna.mongo.MongoRepository;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.project;

public class MongoRepositoryImpl implements MongoRepository {

    private static final Logger logger = LogManager.getLogger(MongoDBServiceImpl.class);

    Properties prop = new Properties();

    @PostConstruct
    public void init() {
        //Logger mongoLogger = LogManager.getLogger( "org.mongodb.driver" );
        //mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        logger.info("|          CARGANDO        |");

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("connect-mongo.properties");
            logger.info("===================properties loaded====================== {}", inputStream);
            prop.load(inputStream);
            logger.info("===================properties loaded======================");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public List<Document> findAllAlarms() {
        List<Document> doc = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        //MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("prime");
        MongoCollection<Document> coll = database.getCollection("alarms");

        FindIterable<Document> datos = coll.find().sort(new Document("alarmsDTO.timeStamp", -1)).limit(4);
        datos.forEach((Block<? super Document>) d->{
            doc.add(d);
        });
        return doc;
    }

    @Override
    public List<Document> findAllTickets() {
        List<Document> doc = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        //MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("prime");
        MongoCollection<Document> coll = database.getCollection("aggregate");

        FindIterable<Document> datos = coll.find();
        datos.forEach((Block<? super Document>) d->{
            doc.add(d);
        });
        return doc;
    }

    @Override
    public List<Document> findInfoAppdyanmics() {
        List<Document> jsondocs = new ArrayList<>();

        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        MongoClient mongoClient = new MongoClient(uri);

        /*Consultar query */
        MongoDatabase database = mongoClient.getDatabase("datalake");
        MongoCollection<Document> dnacenter = database.getCollection("dnacenter");


        List<Bson> datos = Arrays.asList(

                match(Filters.eq("body.service", "sim/v2/user/metrics/query/machines")),
                sort(new Document("date", -1)),
                limit(1),
                project(new Document("metric", new Document("$objectToArray", "$body.data.data.2.5105.metricData")).append("_id", 0)),
                unwind("$metric"),
                unwind("$metric.v.simMeasurementDtos"),//new Document("$avg","$metric.v.simMeasurementDtos.metricValue.value")
                group(new Document("name", "$metric.k"), Accumulators.avg("promedio", "$metric.v.simMeasurementDtos.metricValue.value")),
                project(new Document("_id", 0).
                        append("name", "$_id.name")
                        .append("promedio", "$promedio")
                        .append("tipo", new Document("$arrayElemAt", Arrays.asList(
                                new Document("$split", Arrays.asList("$_id.name", "|")), 1)
                        ))));
        AggregateIterable<Document> resultDocs = dnacenter.aggregate(datos);


        resultDocs.forEach((Block<? super Document>) d->{
            jsondocs.add(d);
            logger.info(" json",d.toJson());
        });

        return jsondocs;
    }
}
