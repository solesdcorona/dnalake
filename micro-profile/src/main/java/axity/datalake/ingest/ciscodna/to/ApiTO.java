package axity.datalake.ingest.ciscodna.to;

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
        private String system;
        private String url;
        private String urlbase;
        private String method;
        private String body;
        private List<String> response;
        private Map<String,String> headers;
        private Map<String,String> query;

        public ServicesTO() {
        }

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

        public String getUrlbase() {
            return urlbase;
        }

        public void setUrlbase(String urlbase) {
            this.urlbase = urlbase;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public List<String> getResponse() {
            return response;
        }

        public void setResponse(List<String> response) {
            this.response = response;
        }
    }


}
