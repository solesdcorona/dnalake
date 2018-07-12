package axity.datalake.ingest.appdynamics.clientrest.to;

import java.io.Serializable;
import  java.util.List;

public class ServerTO implements Serializable {
    private String id;
    private String urlbase;
    private List<AttributeTO> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlbase() {
        return urlbase;
    }

    public void setUrlbase(String urlbase) {
        this.urlbase = urlbase;
    }

    public List<AttributeTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeTO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ServerTO{" +
            "id='" + id + '\'' +
            ", urlbase='" + urlbase + '\'' +
            ", attributes=" + attributes +
            '}';
    }
}
