package axity.datalake.ingest.ciscodna.rest.to;

import java.util.Map;
import java.util.List;
public class ApiTO {
    private String urlbase;
    private List<ServicesTO> services;


    public List<ServicesTO> getServices() {
        return services;
    }

    public void setServices(List<ServicesTO> services) {
        this.services = services;
    }

    public String getUrlbase() {
        return urlbase;
    }

    public void setUrlbase(String urlbase) {
        this.urlbase = urlbase;
    }


    public class ServicesTO{
        private String url;
        private Map<String,String> headers;
        private Map<String,String> query;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String> getQuery() {
            return query;
        }

        public void setQuery(Map<String, String> query) {
            this.query = query;
        }
    }


}
