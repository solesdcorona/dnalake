package axity.chatbot.api.repository.impl;

import axity.arquitectura.commons.repository.mongo.BaseMongo;
import axity.chatbot.api.repository.MeetingRepository;
import axity.chatbot.api.service.impl.MeetingBotServiceImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MeetingRepositoryImpl extends BaseMongo implements MeetingRepository {

    private static final Logger logger = LogManager.getLogger(MeetingRepositoryImpl.class);
    @Override
    public List searchMeetngs(List<Bson> aggregates) {

        List<String> doc = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        //MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("chatbot");
        MongoCollection<Document> meeting = database.getCollection("meeting");

        AggregateIterable<Document> resultDocs = meeting.aggregate(aggregates);

        resultDocs.forEach((Block<? super Document>) d->{
            logger.info("json {}",d.toJson());
            doc.add(d.toJson());
        });
        return doc;
    }

    @Override
    public Document save(Document update) {

        List<String> doc = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        //MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("chatbot");
        MongoCollection<Document> meeting = database.getCollection("meeting");

        String idSala =(String)update.get("idSala");
        logger.info("update {}",idSala);
        if(idSala!=null) {
            meeting.updateOne(new BasicDBObject("_id", new ObjectId(idSala)),
                    new BasicDBObject("$set", new BasicDBObject("coworker", update.get("coworker"))));
        }else{
            meeting.insertOne(update);
        }

        return update;
    }


    public Document getById(String id){
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongo-db"));
        //MongoClientURI uri = new MongoClientURI("mongodb://mongoadmin:secret@127.0.0.1/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("chatbot");
        MongoCollection<Document> meeting = database.getCollection("meeting");
        logger.info("id {}",id);
        FindIterable<Document> result = meeting.find(new Document("_id", new ObjectId(id)));

            return result.first();
    }


    @Override
    public List searchMeetingByCoworker(String[] co) {
        return null;
    }
}
