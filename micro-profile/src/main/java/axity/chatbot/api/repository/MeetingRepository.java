package axity.chatbot.api.repository;

import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.List;


public interface MeetingRepository {

     List searchMeetngs(List<Bson> aggregates);
     Document save(Document update);

    List searchMeetingByCoworker(String[] co);

    Document getById(String id);
}
