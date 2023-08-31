package org.chatapp.utils;

import java.io.IOException;
import java.net.Socket;

public interface SocketFactory {
    Socket createSocket(String host, int port) throws IOException;
}