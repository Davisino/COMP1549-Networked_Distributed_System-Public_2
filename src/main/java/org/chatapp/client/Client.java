package org.chatapp.client;

import org.chatapp.model.User;
import org.chatapp.utils.RealScannerWrapper;
import org.chatapp.utils.RealSocketFactory;
import org.chatapp.utils.ScannerWrapper;
import org.chatapp.utils.SocketFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.HashMap;
import javax.swing.*;

public class Client {
    private final HashMap<Integer,User> users = new HashMap<>();
    private String serverAddress;
    private final ScannerWrapper in; // make this non-static
    PrintWriter out;
    private ClientUI ui;

    private final SocketFactory socket;

    public void setUI(ClientUI ui) {
        this.ui = ui;
    }

    public Client(String serverAddress, SocketFactory socket, ScannerWrapper in, PrintWriter out) {
        this.serverAddress = serverAddress;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        Socket socket = null;
        try {
            new Thread(() -> {
                String name = ui.getName();
                if (name != null && !name.trim().isEmpty()) {
                    out.println(name);
                }
            }).start();

            while (in.hasNextLine()) {
                String line = in.nextLine();

                if (line.startsWith("NAMEACCEPTED")) {
                    SwingUtilities.invokeLater(() -> {
                       ui.setNameAcceptedDetails(line.substring(13));
                    });
                } else if (line.startsWith("MESSAGE")) {
                    SwingUtilities.invokeLater(() -> ui.displayMessage(line.substring(8)));
                } else if (line.startsWith("PRIVATE")) {
                    SwingUtilities.invokeLater(() -> ui.displayMessage(line.substring(8)));
                }
                else if (line.startsWith("USERS")) {

                    String[] data = line.substring("USERS".length() + 1).split(",");
                    for (int i = 0; i < data.length; i++) {
                        String[] parts = data[i].split(":");
                        String ipAddress = parts[2];
                        if (ipAddress.startsWith("/"))
                            ipAddress = ipAddress.substring(1);
                        User user = new User(Integer.parseInt(parts[0]), parts[1], ipAddress);
                        users.put(user.getId(), user);
                        ui.AddUserToPanel(user);
                    }
                }
                else if (line.startsWith("USER")) {

                    String[] parts = line.substring("USER".length() + 1).split(":");
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String ipAddress = parts[2];
                    if (ipAddress.startsWith("/"))
                        ipAddress = ipAddress.substring(1);
                    boolean status = Boolean.parseBoolean(parts[4]);
                    boolean exists = users.containsKey(id);

                    if (status && !exists){
                        User user = new User(id, name, ipAddress);
                        users.put(user.getId(), user);

                        ui.displayMessage(user.getName() + " has connected.");

                        ui.AddUserToPanel(user);
                    }
                    else if (!status && exists) {
                        users.remove(id);

                        ui.displayMessage(name + " has disconnected.");

                        ui.RemoveUserFromPanel(id);
                    }
                }
            }
        } finally {
            if (socket != null)
                socket.close();
            SwingUtilities.invokeLater(() -> {
                ui.handleFinally();
            });
        }
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = "localhost";
        SocketFactory socketFactory = new RealSocketFactory();
        Socket socket = socketFactory.createSocket(serverAddress, 59001);
        ScannerWrapper in = new RealScannerWrapper(socket.getInputStream());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Client client = new Client(serverAddress, socketFactory, in, out);
        ClientUI ui = new ClientUI(serverAddress, client);
        client.setUI(ui);

        client.run();
    }

}