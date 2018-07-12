package axity.datalake.ingest.appdynamics.clientrest.to;

import java.io.Serializable;

public class AttributeTO implements Serializable {
    private String id;
    private String index;
    private String rest;
    private String metricpath;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    public String getMetricpath() {
        return metricpath;
    }

    public void setMetricpath(String metricpath) {
        this.metricpath = metricpath;
    }

    @Override
    public String toString() {
        return "AttributeTO{" +
            "id='" + id + '\'' +
            ", index='" + index + '\'' +
            ", rest='" + rest + '\'' +
            ", metricpath='" + metricpath + '\'' +
            '}';
    }
}
