package axity.datalake.ingest.ciscodna.transform;

import java.util.List;

public interface IssueService {


    List<String> saveIssue(List<String> json);
}
