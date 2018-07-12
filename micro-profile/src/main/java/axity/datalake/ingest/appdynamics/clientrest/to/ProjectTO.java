package axity.datalake.ingest.appdynamics.clientrest.to;

import java.io.Serializable;
import java.util.List;


public class ProjectTO implements Serializable{
    private String id;
    private List<ServerTO> servers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServerTO> getServers() {
        return servers;
    }

    public void setServers(List<ServerTO> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "ProjectTO{" +
            "id='" + id + '\'' +
            ", servers=" + servers +
            '}';
    }
}
