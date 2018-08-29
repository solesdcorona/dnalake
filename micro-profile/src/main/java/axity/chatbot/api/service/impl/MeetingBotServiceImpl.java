package axity.chatbot.api.service.impl;

import axity.chatbot.api.repository.MeetingRepository;
import axity.chatbot.api.service.MeetingBotService;
import axity.chatbot.api.service.constants.MongoQuerys;
import axity.chatbot.api.to.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeetingBotServiceImpl implements MeetingBotService {
    private static final Logger logger = LogManager.getLogger(MeetingBotServiceImpl.class);
    //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm" );
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    DateTimeFormatter formatterWebex = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    DateTimeFormatter UTCFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss Z");
    @Inject
    private MeetingRepository meetingRepository;

    private List<IntentTO> intentTOS;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private Gson gson = new Gson();


    @Override
    public String saveOrUpdateMeeting(String json) {
        Document recive = Document.parse(json);
        Document document = new Document();
        try {
        String timeStart =String.format("%sT%s+0000",recive.get("date"),recive.get("startTime"));
        String timeEnd =String.format("%sT%s+0000",recive.get("date"),recive.get("endTime"));

        document.append("date",dateFormat.parse((String) recive.get("date")));
        document.append("time_start",dateF.parse(timeStart));
        document.append("time_end",dateF.parse(timeEnd));
        document.append("room",recive.get("room"));
        document.append("capacity",(Integer) recive.get("capacity"));
        document.append("coworker",recive.get("coworker"));
        meetingRepository.save(document);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*{
            "date":new Date("2018-08-06T00:00:01Z"),
                "time_start":new Date("2018-08-06T08:00:00Z"),
                "time_end":new Date("2018-08-06T11:00:00Z"),
                "room":"sala1",
                "capacity":5,
                "coworker":["rogelio.corona","raul.marquez"]
        }
        */
        Document id = new Document("idSala",document.get("_id").toString());
        return id.toJson();
    }

    @Override
    public String buildSkype(String payload) {


        try {
            HttpRequest  request =  HttpRequest.newBuilder()
                    .uri(new URI("http://meetinggenerationservice.azurewebsites.net/api/meeting"))
                    .header("Content-type", "application/json")
                    .POST(HttpRequest.BodyProcessor.fromString(payload))
                    .build();
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = null;
            response = client.send(request, HttpResponse.BodyHandler.asString());
            return response.body();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String buildMeetingOutlook(String payload) {


        try {
            HttpRequest  request =  HttpRequest.newBuilder()
                    .uri(new URI("http://meetinggenerationservice.azurewebsites.net/api/mail"))
                    .header("Content-type", "application/json")
                    .POST(HttpRequest.BodyProcessor.fromString(payload))
                    .build();
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = null;
            response = client.send(request, HttpResponse.BodyHandler.asString());
            return response.body();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String saveSearchCoworker(String json) {
        Document doc = Document.parse(json);
        String idMongo = doc.get("idSala", String.class);
        Document update = meetingRepository.save(doc);



        Document data =update;
        try {
             data = meetingRepository.getById(idMongo);
            logger.info("data {}",data);
            SkypeMeetingTO skypeMeetingTO = new SkypeMeetingTO();
            logger.info(" class {} , {}" ,data.get("coworker").getClass(),data.get("coworker"));
            skypeMeetingTO.setAttendees(data.get("coworker",ArrayList.class));
            skypeMeetingTO.setDescription("Titulo ");//data.get("title",String.class);
            skypeMeetingTO.setSala(data.getString("room"));
            Calendar calS = Calendar.getInstance();
            Calendar calE = Calendar.getInstance();
            calS.setTime(data.get("time_start",Date.class));
            calS.add(Calendar.HOUR,5);
            calE.setTime(data.get("time_end",Date.class));
            calE.add(Calendar.HOUR,5);
            skypeMeetingTO.setFechainicio(dateF.format(calS.getTime()));
            skypeMeetingTO.setFechafin(dateF.format(calE.getTime()));
            skypeMeetingTO.setSubject("Meeting BotSMNYL");
            String payload = gson.toJson(skypeMeetingTO);
            logger.info("json {}",payload);
            String jsonResponse = buildSkype(payload);
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            //String json = response.body();
            logger.info("Response {}", jsonObject);
            data.append("skypemeeting",jsonObject.get("mensajeresonse")==null?"":jsonObject.get("mensajeresonse").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toJson();
    }

    @Override
    public String buildWebex(String json) {


        try {
            JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
            String date = jsonObj.get("startDate").getAsString();
            String duration=jsonObj.get("timeduration").getAsString();
            JsonArray gsonArray = jsonObj.getAsJsonArray("participants");


            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("webexTemplate.xml");

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = null;
            docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(inputStream);
            NodeList nodeParticipants = doc.getElementsByTagName("participants");
            Node attends = doc.createElement("attendees");

            gsonArray.forEach(j->{
                Node attend = doc.createElement("attendee");
                Node person = doc.createElement("person");
                Node mail = doc.createElement("email");
                Node name = doc.createElement("name");
                JsonElement nameJson = j.getAsJsonObject().get("name");
                logger.info("is null {}",nameJson);
                String namev = Optional.ofNullable(nameJson).isPresent()?"":nameJson.getAsString();
                String emailv =j.getAsJsonObject().get("email").getAsString();
                mail.setTextContent(emailv);
                name.setTextContent(namev);
                person.appendChild(name);
                person.appendChild(mail);
                attend.appendChild(person);
                attends.appendChild(attend);
            });
            nodeParticipants.item(0).appendChild(attends);
            NodeList schedule = doc.getElementsByTagName("schedule");
            Node startDate = doc.createElement("startDate");
            Node dura = doc.createElement("duration");
            startDate.setTextContent(date);
            dura.setTextContent(duration);
            schedule.item(0).appendChild(startDate);
            schedule.item(0).appendChild(dura);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

//initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            String xmlString = result.getWriter().toString();
            System.out.println(xmlString);

            HttpClient client = HttpClient.newBuilder().build();



            HttpRequest request = null;


            HttpRequest.Builder serviceMeeting = HttpRequest.newBuilder()
                    .uri(new URI("https://getronicsmxsk.Webex.com/WBXService/XMLService"))
                    .headers("Content-Type", "application/xml;charset=UTF-8");


                request=serviceMeeting.POST(HttpRequest.BodyProcessor.fromString(xmlString))
                        .build();
                logger.info("esperando ...");
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
                logger.info("respuesta {}",response.body());
            //

            docBuilder = docFactory.newDocumentBuilder();

            org.w3c.dom.Document docResponse = docBuilder.parse(new StringBufferInputStream(response.body()));
            String meetingKey = docResponse.getElementsByTagName("meet:meetingkey").item(0).getTextContent();
            logger.info("**** meeting key {}",meetingKey);


            InputStream getMeeting = classLoader.getResourceAsStream("webexTemplateGetMeeting.xml");
            org.w3c.dom.Document docGetMeetingUrl = docBuilder.parse(getMeeting);
            Node nodeMeeting = docGetMeetingUrl.getElementsByTagName("meetingKey").item(0);
            nodeMeeting.setTextContent(meetingKey);

            DOMSource sourceGetMeeting = new DOMSource(docGetMeetingUrl);
            StreamResult resultGet = new StreamResult(new StringWriter());

            transformer.transform(sourceGetMeeting, resultGet);
            String xmlStringResponse = resultGet.getWriter().toString();

             client = HttpClient.newBuilder().build();

            HttpRequest.Builder serviceGetMeeting = HttpRequest.newBuilder()
                    .uri(new URI("https://getronicsmxsk.Webex.com/WBXService/XMLService"))
                    .headers("Content-Type", "application/xml;charset=UTF-8");

            logger.info("get meeting ...");
            System.out.println(xmlStringResponse);
            HttpRequest request2 = serviceGetMeeting.POST(HttpRequest.BodyProcessor.fromString(xmlStringResponse)).build();
            HttpResponse<String> responseGetMeeting = client.send(request2, HttpResponse.BodyHandler.asString());
            logger.info("respuesta {}",responseGetMeeting.body());

            org.w3c.dom.Document docResponseMeeting = docBuilder.parse(new StringBufferInputStream(responseGetMeeting.body()));
            String meetingUrl = docResponseMeeting.getElementsByTagName("meet:meetingLink").item(0).getTextContent();
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("urlMeeting",meetingUrl);
            jsonResponse.addProperty("meetingKey",meetingKey);

            return gson.toJson(jsonResponse);
            //
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<DisponibilityTO>  getDisponibility(List<IntentTO> intents) {
        intentTOS=intents;
        List<String> jsonDoc = new ArrayList<>();
        List<DisponibilityTO> disponibility =new ArrayList<>();
        // definir las entidades, fechas , sala , capacidad


        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
        //      Instant instant = Instant.parse( "2018-08-01T08:00:00Z" );  // `Instant` is always in UTC.


        Optional<IntentTO> dateTime = intentTOS.stream().filter(i -> i.getName().equals("date")).findFirst();
        Optional<IntentTO> room = intentTOS.stream().filter(i -> i.getName().equals("room")).findFirst();

        LocalDate endTimeDate=null;
        LocalDate startTimeDate=null;
        String fDate =null;
        String eDate =null;


        if(dateTime.isPresent()){
            IntentTO i=dateTime.get();
            fDate = String.format("%sT00:00:00-0500",i.getFirstValue());
            eDate = Optional.ofNullable(i.getSecondValue()).map(d->{
                return String.format("%sT23:59:59-0500",i.getSecondValue());
            }).orElse(String.format("%sT23:59:59-0500",i.getFirstValue()));
            logger.info("date {} , dateE {}", fDate, eDate);
        }else{
            fDate="2018-08-13T00:59:59.000Z";
            eDate="2018-08-15T00:59:59.000Z";
        }

        logger.info("zone {}",ZoneId.systemDefault());


        Date dates=null;
        Date datee=null;
        try {
            dates=dateF.parse(fDate);
            datee=dateF.parse(eDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


        logger.info("dateTime {} , dateE {}", dates, datee);
        int days =dates.getDate()==datee.getDate()?1:datee.getDate()-dates.getDate();
        logger.info("days left {}",days);
        List<RoomTO> salas= Arrays.asList(new RoomTO("Pulsar",5),new RoomTO("SuperNova",10),new RoomTO("Galileo",12),new RoomTO("ISS",15),new RoomTO("Orion",20));
        if (!room.isPresent()){
            for (RoomTO roomE: salas) {
                IntentTO roomP = new IntentTO("room", roomE.getName(), "");
                intents.remove(roomP);
                intents.add(roomP);

                for (int index=1;index<=days;index++){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dates);
                    //cal.add(Calendar.HOUR,4);
                    dates=cal.getTime();
                    cal.add(Calendar.HOUR,23);
                    datee=cal.getTime();

                    logger.info("Rooom {} date {} , dateE {}",intents, dates, datee);
                    List<String> meetings =  meetingRepository.searchMeetngs(buildQueryMongoAggregate(intents, dates, datee));

                    if(meetings.size()>0){
                        disponibility.addAll(searchDisponibility(meetings));
                    }else{
                        DisponibilityTO disponibilityTO= new DisponibilityTO();
                        disponibilityTO.setDate(dateFormat.format(dates));
                        disponibilityTO.setRoom(roomE.getName());

                        DisponibilityTimeTO time = new DisponibilityTimeTO();
                        time.setCapacity(roomE.getCapacity());
                        time.setRoom(roomE.getName());
                        time.setDate(disponibilityTO.getDate());
                        time.setsTime(800);
                        time.seteTime(2000);
                        time.setEndTime("20:00");
                        time.setStartTime("08:00");
                        disponibilityTO.setDisponibilityTimeTOS(Arrays.asList(time));
                        disponibility.addAll(Arrays.asList(disponibilityTO));
                    }
                    //cal.add(Calendar.DATE,1);
                    //datee=cal.getTime();
                }

            }
        }else{
            for (int index=1;index<=days;index++){
                Calendar cal = Calendar.getInstance();
                cal.setTime(dates);
                //cal.add(Calendar.HOUR,4);
                dates=cal.getTime();
                cal.add(Calendar.HOUR,23);
                datee=cal.getTime();

                logger.info("date {} , dateE {}", dates, datee);
                List<String> meetings = meetingRepository.searchMeetngs(buildQueryMongoAggregate(intents, dates, datee));
                if(meetings.size()>0){
                    disponibility.addAll(searchDisponibility(meetings));
                }else{
                    DisponibilityTO disponibilityTO= new DisponibilityTO();
                    disponibilityTO.setDate(dateFormat.format(dates));
                    disponibilityTO.setRoom(room.get().getFirstValue());
                    disponibilityTO.setExactly(true);
                    DisponibilityTimeTO time = new DisponibilityTimeTO();
                     List<RoomTO> exist=salas.stream().filter(s -> s.getName().toLowerCase().equals(room.get().getFirstValue().toLowerCase()))
                            .collect(Collectors.toList());
                     if(exist.size()>0){
                         time.setCapacity(exist.get(0).getCapacity());
                         time.setRoom(room.get().getFirstValue());
                         time.setDate(disponibilityTO.getDate());
                         time.setsTime(800);
                         time.seteTime(2000);
                         time.setEndTime("20:00");
                         time.setStartTime("08:00");
                         disponibilityTO.setDisponibilityTimeTOS(Arrays.asList(time));
                         disponibility.addAll(Arrays.asList(disponibilityTO));
                     }

                }
            }
        }


        //jsonDoc.addAll();
        return disponibility;
    }


    private List<Bson> buildQueryMongoAggregate(List<IntentTO> intents, Date startDate, Date endDate){
        List<Bson> aggregagtes = new ArrayList<>();
        intents.forEach(i -> {
            if (i.getName().equals("capacity")) {
                int start = Integer.parseInt(i.getFirstValue());
                int end = Integer.parseInt(Optional.ofNullable(i.getSecondValue()).map(d->d).orElse(i.getFirstValue()));

                aggregagtes.add(Aggregates.match(
                        Filters.and(Arrays.asList(
                                Filters.gte("capacity", start),
                                Filters.lte("capacity", end*2)
                        ))));
            }else if (i.getName().equals("room")) {
                aggregagtes.add(Aggregates.match(Filters.eq("room", i.getFirstValue())));
            }else if (i.getName().equals("date")) {

                aggregagtes.add(Aggregates.sort(new Document("time_start", 1)));

                aggregagtes.add(Aggregates.match(Filters.and(Filters.gte("date", startDate),
                        Filters.lte("date", endDate))));
            }
        });
        aggregagtes.add(MongoQuerys.GROUP);

        return aggregagtes;
    }

    public List<DisponibilityTO> searchDisponibility(List<String> meetings) {
        List<DisponibilityTO> disponibilityList = new ArrayList<>();


        // meetings
        meetings.forEach(j -> {
            DisponibilityTO disponibility = new DisponibilityTO();

            List<DisponibilityTimeTO> disponibilityTimeTOs = new ArrayList<>();
            List<Map<String, Object>> times = JsonPath.read(j, "$.times[*]");

            List<MeetingTO> meetingsss = times.stream().map(t -> {
                MeetingTO meeting = new MeetingTO();
                meeting.setTimeStart((String) t.get("time_start"));
                meeting.setTimeEnd((String) t.get("time_end"));
                meeting.setRoom((String) t.get("room"));
                meeting.setDate((String) t.get("date"));
                meeting.setCapacity(((Number)t.get("capacity")).intValue());
                logger.info(" example ==== {}", meeting.getDate());
                return meeting;
            }).collect(Collectors.toList());


            int startTime = 800;
            int endTime = 800;
            String dateT = null;
            String room = null;
            Integer capacity=0;
            for (MeetingTO time : meetingsss) {
                dateT = time.getDate();
                room =time.getRoom();
                capacity= time.getCapacity();
                LocalTime date = LocalTime.parse(time.getTimeStart(), formatter);
                int minute = date.get(ChronoField.MINUTE_OF_HOUR);
                int hourMinute = (date.get(ChronoField.CLOCK_HOUR_OF_DAY) * 100) + minute;

                logger.info(" hora {} ", hourMinute);

                endTime = hourMinute;
                if (startTime < endTime) {
                    logger.info("Horario Disponible {} a {}", startTime, endTime);
                    /**int h=(startTime / 100);
                    int m= (startTime-(h*100));
                    int he=(endTime / 100);
                    int me=(endTime-(he*100));
                     **/
                    disponibilityTimeTOs.add(new DisponibilityTimeTO( time.getDate(), time.getRoom(),time.getCapacity(), startTime, endTime));
                }
                LocalTime dateEnd = LocalTime.parse(time.getTimeEnd(), formatter);
                int newStartHourMinute = (dateEnd.get(ChronoField.CLOCK_HOUR_OF_DAY) * 100) + dateEnd.get(ChronoField.MINUTE_OF_HOUR);
                logger.info(" horario startTime{}",newStartHourMinute);
                startTime = newStartHourMinute;
            }
            //logger.info(" horario startTime{}",startTime);
            if (startTime < 2000) {// horario final menor a las 20:00 horas
                disponibilityTimeTOs.add(new DisponibilityTimeTO( dateT, room,capacity, startTime, 2000));
            }
            disponibility.setDate(dateT);
            disponibility.setRoom(room);
            disponibility.setDisponibilityTimeTOS(disponibilityTimeTOs);

            disponibilityList.add(disponibility);

        });

        logger.info(" datos {}",intentTOS);
        Optional<IntentTO> timeIntent = intentTOS.stream().filter(i -> i.getName().equals("time")).findFirst();

        if(timeIntent.isPresent()){
            searchTime(disponibilityList,timeIntent.get());
        }

        return disponibilityList;
    }




    private  void searchTime(List<DisponibilityTO> disponibilityTOS, IntentTO intentTO) {
        logger.info("comparando ==========");
        List<DisponibilityTimeTO> disponibilityExactly = new ArrayList<>();
        LocalTime dateStart = LocalTime.parse(intentTO.getFirstValue(), timeFormatter);
        LocalTime dateEnd = LocalTime.parse(intentTO.getSecondValue(), timeFormatter);
        int newStartHourMinute = (dateStart.get(ChronoField.CLOCK_HOUR_OF_DAY) * 100) + dateStart.get(ChronoField.MINUTE_OF_HOUR);
        int newEndHourMinute = (dateEnd.get(ChronoField.CLOCK_HOUR_OF_DAY) * 100) + dateEnd.get(ChronoField.MINUTE_OF_HOUR);

         disponibilityTOS.stream().forEach(ds->{
             logger.info("dispo {}",ds.getDisponibilityTimeTOS().size());
             ds.getDisponibilityTimeTOS().stream().filter(d -> {
                 logger.info("comparando {} por {} valores {}",newStartHourMinute,newEndHourMinute ,(newStartHourMinute >= d.getsTime() && newEndHourMinute <= d.geteTime()));
                 return newStartHourMinute >= d.getsTime() && newEndHourMinute <= d.geteTime();
             }).map(disponibilityExactly::add).collect(Collectors.toList());
            ds.getDisponibilityTimeTOS().removeAll(disponibilityExactly);
         });




         if(disponibilityExactly.size()>0){

             DisponibilityTO exaclty= new DisponibilityTO();
             exaclty.setExactly(true);
             exaclty.setExactlyTime(disponibilityExactly);
             exaclty.setDate(null);
             disponibilityTOS.add(exaclty);
         }

    }



}
