package axity.chatbot.api.service.constants;

import org.bson.Document;

public class MongoQuerys {

   public static final Document GROUP = Document.parse("\n" +
            "            {'$group':{'_id':{\n" +
            //"                'capacity':'$capacity',\n" +
            "                'date':{'$dateToString':{'date':'$date','format':'%d-%m-%Y'}}\n" +
            "            },\n" +
            "            'times':{\n" +
            "                '$push':{\n" +
            "                    'date':{'$dateToString': { 'date':'$date','format':'%Y-%m-%d'}},\n" +
            "                    'time_start':{\n" +
            "                            '$dateToString':{\n" +
            "                                'date':'$time_start',\n" +
            "                                'format':'%d-%m-%Y %H:%M'\n" +
            "                              }\n" +
            "                        },\n" +
            "                    'time_end':{\n" +
            "                           '$dateToString':{\n" +
            "                                'date':'$time_end',\n" +
            "                                'format':'%d-%m-%Y %H:%M'\n" +
            "                              }\n" +
            "                            },\n" +
            "                        'room':'$room',\n" +
            "                        'capacity':'$capacity'\n" +
            "                }\n" +
            "              }\n" +
            "          }}");

}
