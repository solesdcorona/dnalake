package axity.datalake.ingest;

import axity.datalake.ingest.ciscodna.CiscoDNACenterRestClient;
import axity.datalake.ingest.ciscodna.DataStoreService;
import axity.datalake.ingest.ciscodna.remedy.ClasificationDataService;
import axity.datalake.ingest.ciscodna.to.ApiTO;
import axity.datalake.ingest.ciscodna.to.LoginTO;
import axity.datalake.ingest.ciscodna.transform.IssueService;
import axity.datalake.ingest.common.ConfigService;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;

import org.apache.logging.log4j.LogManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.List;

@Singleton
@Startup
public class CiscoDNAService {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CiscoDNAService.class);
    @Resource
    private TimerService timer;
    @Inject
    private ConfigService configService;

    @Inject
    private CiscoDNACenterRestClient ciscoDNAService;

    @Inject
    @Named("mongoServices")
    private DataStoreService storeGCPService;

    @Inject
    @Named("anyDeskMongo")
    private DataStoreService mongoAnyDesk;

    @Inject
    private ClasificationDataService clasificationDataService;

    @Inject
    @Named("issueTemp")
    private IssueService issueService;

    private CredentialsProvider credentialsProvider;


    @PostConstruct
    public void initCron() {
        logger.info("======== Iniciando Singleton...=============");
        System.out.println("======== Iniciando Singleton...=============");

        ScheduleExpression expression = new ScheduleExpression();
        expression.dayOfMonth("*").hour("*").minute("*/4");
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(true);
        timer.createCalendarTimer(expression, timerConfig);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("google/datastore-31eef6296a0d.json");

             credentialsProvider = FixedCredentialsProvider.create(
                ServiceAccountCredentials.fromStream(inputStream));

        } catch (Exception e){
            e.printStackTrace();
        }

    }


    @Timeout
    public void executeGetInfoDynamics() {
        logger.info(" Iniciando cron cada 5 minutos");
        System.out.println("======== ejecutando=============");
        LoginTO loginTO = configService.getConfigFile(LoginTO.class, "json-dnacenter.json", "login");
        ciscoDNAService.invokeLogin(loginTO);
        ApiTO apiTO = configService.getConfigFile("json-dnacenter.json", "apis");
        List<String> bodys = ciscoDNAService.invokeApi(apiTO);
        List<String> ids = storeGCPService.saveDNACenterData(bodys);//searchMeetngs in mongo and return all id's
        ids.clear();
        ids.addAll(issueService.saveIssue(ids));
        /*CompletableFuture<Void> future
            = CompletableFuture.runAsync(() -> {
                try {
                    //mongoAnyDesk.saveDNACenterData(bodys);// searchMeetngs in format alerts
                    issueService.saveIssue(ids);
                }catch (Exception e){
                    logger.error("Error en mongo any desk",e);
                }
        });
        */
        clasificationDataService.setCredentialsProvider(this.credentialsProvider);
        clasificationDataService.buildClasificationData(ids);

    }
}
