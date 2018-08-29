package axity.chatbot.api.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
public class IntentTO {

    private String name;
    private String firstValue;
    private String secondValue;

    public IntentTO() {
    }

    public IntentTO(String name, String firstValue, String secondValue) {
        this.name = name.toLowerCase();
        this.firstValue = firstValue.toLowerCase();
        this.secondValue = secondValue;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }

    public String getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(String firstValue) {
        this.firstValue = firstValue.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntentTO intentTO = (IntentTO) o;
        return Objects.equals(name, intentTO.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "IntentTO{" +
                "name='" + name + '\'' +
                ", firstValue='" + firstValue + '\'' +
                ", secondValue='" + secondValue + '\'' +
                '}';
    }
}
