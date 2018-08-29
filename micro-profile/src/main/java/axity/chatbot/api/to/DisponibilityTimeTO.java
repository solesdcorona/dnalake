package axity.chatbot.api.to;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties({ "sTime", "eTime" })
public class DisponibilityTimeTO {

    private String startTime;
    private String endTime;
    private String date;
    private String room;
    private Integer capacity;



    private Integer sTime;
    private Integer eTime;

    public DisponibilityTimeTO( ) {

    }
    public DisponibilityTimeTO( String date, String room, Integer capacity, Integer sTime, Integer eTime) {

        this.date = date;
        this.room = room;
        this.capacity=capacity;
        this.sTime = sTime;
        this.eTime = eTime;

        int h=(sTime / 100);
        int m= (sTime-(h*100));
        int he=(eTime / 100);
        int me=(eTime-(he*100));

        this.startTime =h + ":" + (m==0?"00":m);
        this.endTime = he + ":" + (me==0?"00":me);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer geteTime() {
        return eTime;
    }

    public void seteTime(Integer eTime) {
        this.eTime = eTime;
    }

    public Integer getsTime() {
        return sTime;
    }

    public void setsTime(Integer sTime) {
        this.sTime = sTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisponibilityTimeTO that = (DisponibilityTimeTO) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(room, that.room) &&
                Objects.equals(sTime, that.sTime) &&
                Objects.equals(eTime, that.eTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, room, sTime, eTime);
    }
}
