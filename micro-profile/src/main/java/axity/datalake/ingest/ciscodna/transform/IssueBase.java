package axity.datalake.ingest.ciscodna.transform;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class IssueBase {


    private static final Logger logger = LogManager.getLogger(IssueBase.class);


    protected Properties prop = new Properties();
    protected Gson gson = new Gson();

    @PostConstruct
    public void init() {
        //Logger mongoLogger = LogManager.getLogger( "org.mongodb.driver" );
        //mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        logger.info("|          CARGANDO        |");

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("connect-mongo.properties");
            logger.info("===================properties loaded====================== {}", inputStream);
            prop.load(inputStream);
            logger.info("===================properties loaded======================");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
