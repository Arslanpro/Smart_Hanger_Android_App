//package com.example.smarthanger;
//
//import com.example.smarthanger.server.ServerRequest;
//
//import org.junit.Test;
//
//import java.util.List;
//
//public class testFormatData {
//    @Test
//    public void testFormat()    {
//        String response = "[{\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/42\",\n" +
//                "    \"location_name\": \"Enschede\",\n" +
//                "    \"description\": \"Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-02T23:23:00Z\",\n" +
//                "    \"latitude\": 52.2215372,\n" +
//                "    \"longitude\": 6.8936619\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/19\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-02T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/45\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-02T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/20\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-06T13:14:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/29\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/30\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/31\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/33\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/28\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/27\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Zonstraat 48B, 7521 HJ Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-10T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2292146,\n" +
//                "    \"longitude\": 6.857974\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/50\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/51\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/52\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/53\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/54\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/47\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/49\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T11:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/59\",\n" +
//                "    \"location_name\": \"Campus UT Huisartsenpraktijk\",\n" +
//                "    \"description\": \"Campuslaan 99, 7522 NE Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T21:11:00Z\",\n" +
//                "    \"latitude\": 52.2461239,\n" +
//                "    \"longitude\": 6.8534207\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/44\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:23:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/46\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:50:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/38\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/40\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/36\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/43\",\n" +
//                "    \"location_name\": \"Universiteit Twente\",\n" +
//                "    \"description\": \"Drienerlolaan 5, 7522 NB Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2396538,\n" +
//                "    \"longitude\": 6.84979\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/39\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-11T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}, {\n" +
//                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/41\",\n" +
//                "    \"location_name\": \"Lidl\",\n" +
//                "    \"description\": \"Hortensiastraat 140, 7531 GX Enschede, Nederland\",\n" +
//                "    \"date_time\": \"2021-11-12T23:59:00Z\",\n" +
//                "    \"latitude\": 52.2296925,\n" +
//                "    \"longitude\": 6.921127299999999\n" +
//                "}]";
//        List<List<String[]>> objects = ServerRequest.formatData(response);
//
//    }
//}
