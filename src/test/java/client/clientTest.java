package client;

import org.chatapp.client.Client;
import org.chatapp.client.ClientUI;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.*;
import java.net.Socket;

import org.chatapp.utils.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.chatapp.utils.ScannerWrapper;
import org.mockito.MockitoAnnotations;


public class clientTest {

    @Mock
    private SocketFactory mockSocketFactory;

    @Mock
    private Socket mockSocket;

    @Mock
    private ScannerWrapper mockScannerWrapper;

    @Mock
    private PrintWriter mockPrintWriter;

    @Mock
    private ClientUI mockUI;

    private Client client;


    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockSocketFactory.createSocket(anyString(), anyInt())).thenReturn(mockSocket);  // Mock the SocketFactory's behavior
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));  // Mock the Socket's InputStream
        when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());  // Mock the Socket's OutputStream
        whenNew(PrintWriter.class).withAnyArguments().thenReturn(mockPrintWriter);  // Mock PrintWriter instantiation
        // Instantiate Client, injecting the mock SocketFactory, mock Socket, mock ScannerWrapper, and mock PrintWriter
        client = new Client("localhost", mockSocketFactory, mockScannerWrapper, mockPrintWriter);
        client.setUI(mockUI);  // set the mock UI
    }




    @Test
    public void testNameAccepted() throws IOException {
        when(mockScannerWrapper.hasNextLine()).thenReturn(true, false);  // Changed to mockScannerWrapper
        when(mockScannerWrapper.nextLine()).thenReturn("NAMEACCEPTED TestName");  // Changed to mockScannerWrapper
        client.run();
        verify(mockUI, times(1)).setNameAcceptedDetails("TestName");
    }

    @Test
    public void testMessageReceived() throws IOException {
        when(mockScannerWrapper.hasNextLine()).thenReturn(true, false);  // Changed to mockScannerWrapper
        when(mockScannerWrapper.nextLine()).thenReturn("MESSAGE Hello");  // Changed to mockScannerWrapper
        client.run();
        verify(mockUI, times(1)).displayMessage("Hello");
    }

    @Test
    public void testPrivateMessageReceived() throws IOException {
        when(mockScannerWrapper.hasNextLine()).thenReturn(true, false);
        when(mockScannerWrapper.nextLine()).thenReturn("PRIVATE HelloPrivately");
        client.run();
        verify(mockUI, times(1)).displayMessage("HelloPrivately");
    }

    @Test
    public void testUsersListUpdate() throws IOException {
        String usersData = "USERS 1:Test:/127.0.0.1,2:Test2:/192.168.1.1";
        when(mockScannerWrapper.hasNextLine()).thenReturn(true, false);
        when(mockScannerWrapper.nextLine()).thenReturn(usersData);
        client.run();
        verify(mockUI, times(2)).AddUserToPanel(any());
    }

    @Test
    public void testUserConnectsAndDisconnects() throws IOException {
        when(mockScannerWrapper.hasNextLine()).thenReturn(true, true, false);
        when(mockScannerWrapper.nextLine()).thenReturn("USER 1:Test:/127.0.0.1:true", "USER 1:Test:/127.0.0.1:false");
        client.run();
        verify(mockUI, times(1)).AddUserToPanel(any());
        verify(mockUI, times(1)).RemoveUserFromPanel(any());
    }

    // Add more tests to cover other scenarios...
}
