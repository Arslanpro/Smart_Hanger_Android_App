package com.example.smarthanger.bluetooth;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.smarthanger.bluetooth.Blue_Object;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ BluetoothSocket.class, BluetoothDevice.class, Blue_Object.class, BluetoothAdapter.class, Activity.class })
@PowerMockIgnore({ "org.powermock.*", "org.mockito.*"})
public class BlueTest {

    private static final String FULL_DEVICE_NAME = "hc05";
    private static final String TRUNCATED_DEVICE_NAME = "hc05";

    private BluetoothAdapter mockAdapter;
    private BluetoothSocket mockSocket;
    private ByteArrayInputStream mockInputStream;
    private ByteArrayOutputStream mockOutputStream;
    private Blue_Object bluetoothComm;



    @Before
    public void setup() {
        //bluetoothComm = PowerMockito.mock(Blue_Object.class);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mockAdapter);
        bluetoothComm = new Blue_Object();
        initializeStreams();
        mockSocket = createMockSocket();

        //Create the mock adapter and attach a set of mock devices to it
        mockAdapter = PowerMockito.mock(BluetoothAdapter.class);
        Set<BluetoothDevice> mockDeviceSet = createMockDeviceSet();
        //expect(mockAdapter.getBondedDevices()).andReturn(mockDeviceSet);
        PowerMockito.when(mockAdapter.getBondedDevices()).thenReturn(mockDeviceSet);
        PowerMockito.when(mockAdapter.getName()).thenReturn("hc05");
        BluetoothDevice mockDevice = createMockDevice("hc05");
        PowerMockito.when(mockSocket.getRemoteDevice()).thenReturn(mockDevice);
        bluetoothComm.btSocket = mockSocket;


        PowerMockito.when(mockAdapter.getRemoteDevice("98:D3:41:F6:09:F7")).thenReturn(mockDevice);
        bluetoothComm.bluetoothAdapter = mockAdapter;
        //bluetoothComm = new Blue();

    }

    private void initializeStreams() {
        mockInputStream = new ByteArrayInputStream(new byte[] {'S', 'a', 'S', 'b', 'N', (byte) 255});
        mockOutputStream = new ByteArrayOutputStream();
    }

    private BluetoothSocket createMockSocket() {
        BluetoothSocket mockSocket = PowerMockito.mock(BluetoothSocket.class);

        try {
            PowerMockito.doNothing().when(mockSocket).connect();
            PowerMockito.when(mockSocket.getInputStream()).thenReturn(mockInputStream);
            PowerMockito.when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        } catch (IOException ignored) { }

        return mockSocket;
    }

    private Set<BluetoothDevice> createMockDeviceSet() {
        Set<BluetoothDevice> btDeviceSet = new LinkedHashSet<>();

        btDeviceSet.add(createMockDevice(TRUNCATED_DEVICE_NAME));
        btDeviceSet.add(createMockDevice("Not a real device"));

        return btDeviceSet;
    }

    private BluetoothDevice createMockDevice(String deviceName) {
        BluetoothDevice btDevice = PowerMockito.mock(BluetoothDevice.class);

        PowerMockito.when(btDevice.getName()).thenReturn(deviceName);
        try {

            //PowerMockito.when(bluetoothComm.getmUUID()).thenReturn("yes"));
            PowerMockito.when(btDevice.createRfcommSocketToServiceRecord(bluetoothComm.getmUUID())).thenReturn(mockSocket);
            PowerMockito.when(btDevice.createRfcommSocketToServiceRecord(bluetoothComm.getmUUID())).thenReturn(mockSocket);
        } catch (IOException ignored) { }

        return btDevice;
    }


    @Test
    public void testConnect() throws Exception {
        BluetoothDevice remoteDevice = mockSocket.getRemoteDevice();
        PowerMockito.when(mockAdapter.getRemoteDevice("98:D3:41:F6:09:F7")).thenReturn(remoteDevice);
        Whitebox.invokeMethod(bluetoothComm, "createConnection");
        String deviceName = bluetoothComm.bluetoothDevice.getName();
        assertEquals(deviceName, TRUNCATED_DEVICE_NAME);
    }

    @Test
    public void testDisconnect() throws Exception {
        BluetoothDevice remoteDevice = mockSocket.getRemoteDevice();
        PowerMockito.when(mockSocket.isConnected()).thenReturn(true);
        bluetoothComm.btSocket = mockSocket;
        PowerMockito.when(mockAdapter.getRemoteDevice("98:D3:41:F6:09:F7")).thenReturn(remoteDevice);
        Whitebox.invokeMethod(bluetoothComm, "createConnection");

        boolean result = Whitebox.invokeMethod(bluetoothComm, "isConnected");
        assertTrue(result);
        bluetoothComm.disconnect();
        PowerMockito.when(mockSocket.isConnected()).thenReturn(false);
        assertFalse(bluetoothComm.isConnected());
        assertFalse(mockSocket.isConnected());
    }

    @Test
    public void testSendString() throws Exception {
        Whitebox.invokeMethod(bluetoothComm, "createConnection");

        Whitebox.invokeMethod(bluetoothComm, "sendMessage", mockOutputStream);

        String sentData = mockOutputStream.toString();

        assertEquals("send info",sentData );
    }


    @Test
    public void testReadMessage() throws Exception {
        bluetoothComm.createConnection();
        //Whitebox.invokeMethod(bluetoothComm, "setInputStream", new ByteArrayInputStream(new byte[] {'S', 'a', 'S', 'b', 'N', (byte) 255}));

        //while(!bluetoothComm.isMessageReady());
        byte[] tempByte = new byte[]{'S', 'a', 'S', 'b', 'N', (byte) 255};
        InputStream tempInput = new ByteArrayInputStream(tempByte);
        String result = Whitebox.invokeMethod(bluetoothComm, "receiveMessage", tempInput);
        assertEquals("\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF", result);
    }


}

