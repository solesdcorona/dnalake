package axity.datalake.ingest.appdynamics.clientrest;
import axity.datalake.ingest.appdynamics.clientrest.to.ProjectTO;
import axity.datalake.ingest.appdynamics.service.to.ColumnValueTO;

import java.util.List;
public interface ControllerService {

    List<ColumnValueTO> invokeRestService(ProjectTO projectTO);


}
