package axity.datalake.ingest.appdynamics.service;

import axity.datalake.ingest.appdynamics.service.to.ColumnValueTO;
import java.util.List;


public interface ExcelService {


    void writeMetrics(List<ColumnValueTO> columns);
}
