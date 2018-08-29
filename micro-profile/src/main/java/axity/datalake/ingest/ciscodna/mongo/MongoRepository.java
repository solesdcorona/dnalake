package axity.datalake.ingest.ciscodna.mongo;

import org.bson.Document;

import java.util.List;

public interface MongoRepository {

    List<Document> findAllAlarms();
    List<Document> findAllTickets();

    List<Document> findInfoAppdyanmics();
}
