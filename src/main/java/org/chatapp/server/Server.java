package org.chatapp.server;
import org.chatapp.utils.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static AtomicInteger clientIdGenerator = new AtomicInteger(1); // Start with 1, so we skip the 0 (broadcast id)

    public static ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        System.out.println("The chat server is running...");

        String ipAddress = "0.0.0.0";
        int port = 59001;

        ExecutorService pool = Executors.newCachedThreadPool();
        InetSocketAddress address = new InetSocketAddress(ipAddress, port);
        ServerSocket listener = new ServerSocket();
        listener.bind(address);

        try {
            while (true) {
                Socket socket = listener.accept();

                int id = clientIdGenerator.getAndIncrement();
                Connection connection = new Connection(id, socket, server);
                server.addConnection(id, connection);
                pool.execute(connection::handleConnection); // Assuming Connection has a method handleConnection
                connection.SendUsers();
            }
        } finally {
            listener.close();
        }
    }

    public void addConnection(Integer id, Connection connection) {
        connections.put(id, connection);
    }

    public void removeConnection(Integer id) {
        connections.remove(id);
    }

    public void broadcast(String message) {
        connections.values().parallelStream().forEach(connection -> {
            try {
                connection.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace(); // Add logging or further error handling here
            }
        });
    }

    public void broadcastJoined(Integer id) {
        connections.values().parallelStream().forEach(connection -> {
            try {
                connection.hasJoined(id);
            } catch (IOException e) {
                e.printStackTrace(); // Add logging or further error handling here
            }
        });
    }

    public void broadcastLeft(Integer id) {
        connections.values().parallelStream().forEach(connection -> {
            try {
                if (connection.getId() != id)
                    connection.hasLeft(id);
            } catch (IOException e) {
                e.printStackTrace(); // Add logging or further error handling here
            }
        });
    }

    public void privateMessage(String message, Integer receiverID) throws IOException {
        Connection recipient = connections.get(receiverID);
        if (recipient != null) {
            recipient.sendPrivateMessage(message);
        }
    }

    public ConcurrentHashMap<Integer, Connection> getConnection() {
        return connections;
    }

    public void personalBroadcast(Integer id, String content) throws IOException {
        Connection personalClient = connections.get(id);
        if (personalClient != null) {
            personalClient.sendMessage(content);
        }
    }
}
