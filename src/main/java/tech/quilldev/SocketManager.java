package tech.quilldev;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SocketManager {

    private final ArrayList<Socket> sockets;
    private final ServerSocket serverSocket;

    public SocketManager(int port) {
        this.sockets = new ArrayList<>();
        this.serverSocket = tryCreateServerSocket(port);

        System.out.println("CREATED SOCKET MANAGER");
    }

    /**
     * Handle any data that passes through the server
     */
    public void handle(){
        this.receiveData();
    }

    /**
     * Check whether sockets are alive, or na!
     */
    public void checkLifelines(){
        new Thread( () -> {
            System.out.printf("Checking connections of %s sockets\n", this.sockets.size());
            this.sendPacket("meme\n");
        }).start();;
    }

    /**
     * Receive data from all sockets & process it
     */
    public void receiveData() {

        //read data from all of the sockets
        for (var socket : sockets) {

            try {

                //Read all available bites from the client
                var stream = socket.getInputStream();
                var bytes = stream.readNBytes(stream.available());

                //string builder for building bytes into strings
                var byteString = new StringBuilder();

                //decode bytes to chars
                for (var b : bytes) {
                    byteString.append((char) b);
                }

                //just send that shit right back, but... to all sockets~!
                this.sendPacket(byteString.toString());
            }
            catch (IOException e){
                this.disconnectSocket(socket);
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a string packet (JSON FORMAT)
     * @param packet to send
     */
    public void sendPacket(String packet){
        //Write data to all sockets
        for(var socket : sockets) {
            try {

                //get the output stream
                var stream = socket.getOutputStream();

                //try to write data to the socket
                stream.write(packet.getBytes(StandardCharsets.UTF_8));

                //flush the stream
                stream.flush();
            } catch (IOException e) {
                this.disconnectSocket(socket);
            }
        }
    }

    /**
     * Disconnect the given socket
     * @param socket to disconnect
     */
    private void disconnectSocket(Socket socket){
        sockets.remove(socket);
        System.out.println("SOCKET DISCONNECTED: " + socket.getInetAddress().toString() + ":" + socket.getPort());
    }

    /**
     * Try to accept new connections
     */
    public void acceptNewConnections() {

        new Thread( () -> {
            while(true){
                try {

                    //the socket to accept
                    var socket = this.serverSocket.accept();

                    // if the socket is connected print out a connection message!
                    if (socket.isConnected()) {

                        //add the socket to the socket list
                        sockets.add(socket);

                        //log that we got a new connection
                        System.out.println("SOCKET CONNECTED: " + socket.getInetAddress().toString() + ":" + socket.getPort());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
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
