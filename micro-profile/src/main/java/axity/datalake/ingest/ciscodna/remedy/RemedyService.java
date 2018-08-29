package axity.datalake.ingest.ciscodna.remedy;

import axity.datalake.ingest.ciscodna.to.RemedyTO;

public interface RemedyService {

    void pushData(RemedyTO remedyTO);
}
