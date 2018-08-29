package axity.datalake.ingest.ciscodna.google.Impl;

import axity.datalake.ingest.ciscodna.DataStoreService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Named("googleServices")
public class DataStoreGCPServiceImpl implements DataStoreService {

    private static final Logger logger = LogManager.getLogger(DataStoreGCPServiceImpl.class);

    @Override
    public List<String> saveDNACenterData(List<String> json) {
        DatastoreOptions options = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("google/datastore-31eef6296a0d.json");
            options = DatastoreOptions.newBuilder()
                .setProjectId("beaming-axon-209123")
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();


            Datastore datastore = options.getService();

            // The kind for the new entity
            String kind = "DNACenter";
            // The name/ID for the new entity
            String name = "dnacenter1";
            // The Cloud Datastore key for the new entity
            Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(name);

            List<Entity> tasks = json.stream().map(j -> {
                return Entity.newBuilder(taskKey).set("json", j).build();
            }).collect(Collectors.toList());
            //Entity task = Entity.newBuilder(taskKey).set("json",j).build();
            // Saves the entity
            datastore.put(tasks.toArray(new Entity[tasks.size()]));

            //System.out.printf("Saved %s: %s%n", task.getKey().getName(), task.getString("description"));
            logger.info("Save entitys");
            //Retrieve entity
            Entity retrieved = datastore.get(taskKey);

            System.out.printf("Retrieved %s: %s%n", taskKey.getName(), retrieved.getString("description"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getDocumentJson(List<String> json) {
        return null;
    }
}
