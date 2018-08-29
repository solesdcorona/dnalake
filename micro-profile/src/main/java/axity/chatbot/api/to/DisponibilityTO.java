package axity.chatbot.api.to;

import java.util.List;

public class DisponibilityTO {

    private String date;

    private Boolean exactly;

    private String room;

    private List<DisponibilityTimeTO> disponibilityTimeTOS;

    private List<DisponibilityTimeTO> exactlyTime;

    private List<DisponibilityTimeTO> fullDisponibility;

    public DisponibilityTO() {
    }

    public List<DisponibilityTimeTO> getDisponibilityTimeTOS() {
        return disponibilityTimeTOS;
    }

    public void setDisponibilityTimeTOS(List<DisponibilityTimeTO> disponibilityTimeTOS) {
        this.disponibilityTimeTOS = disponibilityTimeTOS;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DisponibilityTimeTO> getExactlyTime() {
        return exactlyTime;
    }

    public void setExactlyTime(List<DisponibilityTimeTO> exactlyTime) {
        this.exactlyTime = exactlyTime;
    }

    public List<DisponibilityTimeTO> getFullDisponibility() {
        return fullDisponibility;
    }

    public void setFullDisponibility(List<DisponibilityTimeTO> fullDisponibility) {
        this.fullDisponibility = fullDisponibility;
    }


    public Boolean getExactly() {
        return exactly;
    }

    public void setExactly(Boolean exactly) {
        this.exactly = exactly;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
