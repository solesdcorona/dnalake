package axity.datalake.ingest.ciscodna.rest.remedy;

import axity.datalake.ingest.ciscodna.rest.to.RemedyTO;

public interface RemedyService {

    void pushData(RemedyTO remedyTO);
}
