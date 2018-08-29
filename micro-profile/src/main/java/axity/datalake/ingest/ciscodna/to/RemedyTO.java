package axity.datalake.ingest.ciscodna.to;

public class RemedyTO {
    private String title;
    private String description;
    private String affectedClient;
    private String impact;
    private String severity;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getAffectedClient() {
        return affectedClient;
    }

    public void setAffectedClient(String affectedClient) {
        this.affectedClient = affectedClient;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
