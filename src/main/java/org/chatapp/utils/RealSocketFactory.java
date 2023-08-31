package org.chatapp.utils;

import java.io.IOException;
import java.net.Socket;

public class RealSocketFactory implements SocketFactory {
    @Override
    public Socket createSocket(String address, int port) throws IOException {
        return new Socket(address, port);
    }
}
