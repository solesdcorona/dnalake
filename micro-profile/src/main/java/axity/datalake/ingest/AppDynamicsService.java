package axity.datalake.ingest;

import axity.datalake.ingest.common.ConfigService;
import axity.datalake.ingest.appdynamics.clientrest.ControllerService;
import axity.datalake.ingest.appdynamics.clientrest.to.ProjectTO;
import axity.datalake.ingest.appdynamics.service.ExcelService;
import axity.datalake.ingest.appdynamics.service.to.ColumnValueTO;
import org.apache.logging.log4j.LogManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.List;

@Singleton
@Startup
public class AppDynamicsService  {

    @Resource
    private TimerService timer;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AppDynamicsService.class);

    @Inject
    private ConfigService configService;
    @Inject
    private ControllerService controllerService;

    @Inject
    private ExcelService excelService;

//    @PostConstruct
    public void initCron(){
        logger.info("======== Iniciando Singleton...=============");
        System.out.println("======== Iniciando Singleton...=============");

        ScheduleExpression expression= new ScheduleExpression();
        expression.dayOfMonth("*").hour("*").minute("*/2");
        TimerConfig timerConfig= new TimerConfig();
        timerConfig.setPersistent(true);
        timer.createCalendarTimer(expression, timerConfig);
    }


    @Timeout
    public void executeGetInfoDynamics(){
        logger.info(" Iniciando cron cada 5 minutos");
        System.out.println("======== ejecutando=============");
        ProjectTO project = configService.getConfigFile();
        List<ColumnValueTO> metrics = controllerService.invokeRestService(project);
        excelService.writeMetrics(metrics);
    }
}
