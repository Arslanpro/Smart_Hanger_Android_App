package com.example.smarthanger.server;

import android.content.Context;

import com.android.volley.RequestQueue;

import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ServerRequestTest extends TestCase {
    ServerRequest serverRequest;
    ServerRequest serverRequest2;
    String[] correct_event;
    List<List<String[]>> correctObject;
    String response;
    private String response2;
    Context context;

    @Before
    public void setUp() {
        RequestQueue requestQueue = PowerMock.createMock(RequestQueue.class);
        Double latitude = 6.6;
        Double longitude = 7.6;
        Double temperature = 8.6;
        Double gsr_value = 9.6;
        String mac = "0:0:0";
        File file = PowerMockito.mock(File.class);
        this.serverRequest = new ServerRequest(latitude, longitude, temperature,
                gsr_value, mac, file);
        context = PowerMockito.mock(Context.class);
        this.serverRequest2 = new ServerRequest(new File(""), context);
        //setup server response
         correctObject = new LinkedList<>();
        LinkedList<String[]> correctDay = new LinkedList<>();
        correct_event = new String[3];
        correct_event[0] = " Enschede";
        correct_event[1] = "02-11-2021T23:23";
        correct_event[2] = " Enschede";
        correctDay.add(correct_event);
        correctObject.add(correctDay);
        response = "{\n" +
                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/42\",\n" +
                "    \"location_name\": \"Enschede\",\n" +
                "    \"description\": \"Enschede, Nederland\",\n" +
                "    \"date_time\": \"2021-11-02T23:23:00Z\",\n" +
                "    \"latitude\": 52.2215372,\n" +
                "    \"longitude\": 6.8936619\n" +
                "}";

        response2 = "{\n" +
                "    \"url\": \"https://immense-anchorage-52068.herokuapp.com/app/calendar/42\",\n" +
                "    \"location_name\": \"Enschede\",\n" +
                "    \"description\": \"Enschede, Nederland\",\n" +
                "    \"date_time\": \"2021-11-04T23:23:00Z\",\n" +
                "    \"latitude\": 52.2215372,\n" +
                "    \"longitude\": 6.8936619\n" +
                "}";
    }

    @Test
    public void testObjectCreation() throws JSONException {
                assertEquals(6.6, serverRequest.jsonObject.get("latitude"));
        assertEquals(7.6, serverRequest.jsonObject.get("longitude"));
        assertEquals(8.6, serverRequest.jsonObject.get("temperature"));
        assertEquals(9.6, serverRequest.jsonObject.get("gsr_reading"));
        assertEquals("0:0:0", serverRequest.jsonObject.get("mac_address"));
        assertNotNull(serverRequest.requestQueue);

        assertEquals(serverRequest2.context, this.context);
    }


    @Test
    public void testFormat()    {

        String final_response = "[" + response + "]";
        List<List<String[]>> objects = serverRequest.formatData(final_response);
        //assertTrue(Iterables.elementsEqual(objects, correctObject));
        String[] event = objects.get(0).get(0);
        //assertEquals(day, correctDay.get(0));
        assertEquals(event[0], correct_event[0]);
        assertEquals(event[1], correct_event[1]);
        assertEquals(event[2], correct_event[2]);

        //test two events in response
        final_response = "[" + response + ", " + response + "]";
        objects = serverRequest.formatData(final_response);

        event = objects.get(0).get(0);
        //assertEquals(day, correctDay.get(0));
        assertEquals(event[0], correct_event[0]);
        assertEquals(event[1], correct_event[1]);
        assertEquals(event[2], correct_event[2]);

        event = objects.get(0).get(1);
        //assertEquals(day, correctDay.get(0));
        assertEquals(event[0], correct_event[0]);
        assertEquals(event[1], correct_event[1]);
        assertEquals(event[2], correct_event[2]);

        //2 day example


        final_response = "[" + response + ", " + response2 + "]";
        String[] correct_event2 = new String[3];
        correct_event2[0] = " Enschede";
        correct_event2[1] = "04-11-2021T23:23";
        correct_event2[2] = " Enschede";
        objects = serverRequest.formatData(final_response);

        event = objects.get(0).get(0);
        //assertEquals(day, correctDay.get(0));
        assertEquals(correct_event[0], event[0]);
        assertEquals(correct_event[1], event[1]);
        assertEquals(correct_event[2], event[2]);

        event = objects.get(1).get(0);
        //assertEquals(day, correctDay.get(0));
        assertEquals(correct_event2[0], event[0]);
        assertEquals(correct_event2[1], event[1]);
        assertEquals(correct_event2[2], event[2]);
    }

    @Test
    public void testReadFile() throws IOException {
        String final_response = "[" + response + "]";
        BufferedWriter writer = new BufferedWriter(new FileWriter("testText.txt"));
        writer.write(final_response);
        writer.close();
        Context context = PowerMockito.mock(Context.class);
        FileInputStream fileInputStream = new FileInputStream("testText.txt");
        PowerMockito.when(context.openFileInput("event.txt")).thenReturn(fileInputStream);
        String result = serverRequest.readFromFile(context);
        assertEquals("\n" + final_response, result);
    }
}