package axity.datalake.ingest.appdynamics.service.to;

public class ColumnValueTO {
    private String indexColumn;
    private String valueColumn;

    public String getIndexColumn() {
        return indexColumn;
    }

    public void setIndexColumn(String indexColumn) {
        this.indexColumn = indexColumn;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }
}
