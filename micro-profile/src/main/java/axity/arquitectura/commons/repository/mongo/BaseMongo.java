package axity.arquitectura.commons.repository.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class BaseMongo {
    private static final Logger logger = LogManager.getLogger(BaseMongo.class);

    protected  Properties prop = new Properties();

    @PostConstruct
    public void init() {
        //Logger mongoLogger = LogManager.getLogger( "org.mongodb.driver" );
        //mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        logger.info("|          CARGANDO CHAT        |");

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
