package axity.chatbot.api.service;

import axity.chatbot.api.to.DisponibilityTO;
import axity.chatbot.api.to.IntentTO;

import java.text.ParseException;
import java.util.List;
public interface MeetingBotService {

    List<DisponibilityTO> getDisponibility(List<IntentTO> intents);

    List<DisponibilityTO> searchDisponibility(List<String> meeting);



    String saveOrUpdateMeeting(String json);

    String buildSkype(String json);

    String saveSearchCoworker(String json);


    String buildWebex(String json);

    String buildMeetingOutlook(String payload);
}
