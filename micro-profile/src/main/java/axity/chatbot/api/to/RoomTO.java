package axity.chatbot.api.to;

import java.util.Objects;

public class RoomTO {
    private String name;


    public RoomTO(String name) {

    }
    public RoomTO(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    private Integer capacity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomTO roomTO = (RoomTO) o;
        return Objects.equals(name, roomTO.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
