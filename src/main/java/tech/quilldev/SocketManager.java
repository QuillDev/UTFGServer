package tech.quilldev;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SocketManager {

    private final ArrayList<QuillSocket> sockets;
    private final ServerSocket serverSocket;

    public SocketManager(int port) {
        this.sockets = new ArrayList<>();
        this.serverSocket = tryCreateServerSocket(port);

        System.out.println("CREATED SOCKET MANAGER");
    }

    /**
     * Handle any data that passes through the server
     */
    public void handle() {
        this.receiveData();
    }

    /**
     * Check whether sockets are alive, or na!
     */
    public void checkLifelines() {

        // if there are no sockets, return
        if (sockets.size() == 0) {
            return;
        }

        //Make a new thread and check whether sockets are alive
        new Thread(() -> {

            System.out.printf("Checking connections of %s sockets\n", this.sockets.size());

            // Remove dead sockets
            for (var socket : sockets) {
                // if the socket is dead, disconnect it
                if (!socket.alive()) {
                    disconnectSocket(socket);
                }
            }
        }).start();
    }

    /**
     * Receive data from all sockets & process it
     */
    public void receiveData() {

        new Thread(() -> {
            // read data from all of the sockets
            for (var socket : sockets) {

                // read all of the sockets
                socket.readSocketAsync();
            }
        }).start();
    }

    /**
     * Send a string packet (JSON FORMAT)
     * 
     * @param packet to send
     */
    public void sendPacket(String packet) {
        // Write data to all sockets
        for (var socket : sockets) {
            socket.writeLineAsync(packet);
        }
    }

    /**
     * Disconnect the given socket
     * 
     * @param socket to disconnect
     */
    private void disconnectSocket(QuillSocket socket) {
        socket.close();
        sockets.remove(socket);
        System.out.println("SOCKET DISCONNECTED: " + socket.getAddress());
    }

    /**
     * Try to accept new connections
     */
    public void acceptNewConnections() {

        new Thread(() -> {
            while (true) {
                try {

                    // the socket to accept
                    var socket = this.serverSocket.accept();

                    // if the socket is connected print out a connection message!
                    if (socket.isConnected()) {

                        // create a quillsocket from that socket
                        var qSocket = new QuillSocket(socket);

                        // add the socket to the socket list
                        sockets.add(qSocket);

                        // log that we got a new connection
                        System.out.println("SOCKET CONNECTED: " + qSocket.getAddress());
                    }

                } catch (IOException ignored) {
                    System.out.println("Failed to accept the socket!");
                }
            }
        }).start();
    }

    /**
     * Try to create a server socket at the given port
     * 
     * @param port to start listening on
     * @return the port
     */
    public ServerSocket tryCreateServerSocket(int port) {
        try {
            // try to create the socket
            return new ServerSocket(2069);
        }

        // if we failed to create the socket print that we failed.
        catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Failed to create ServerSocket on port: %s\n", port);
            System.exit(0);
            return null;
        }
    }
}
