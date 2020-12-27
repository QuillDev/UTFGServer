package tech.quilldev;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    // server socket
    private ServerSocket serverSocket;
    private final SocketManager socketManager;
    private final ArrayList<Socket> sockets;
    private final ScheduledExecutorService scheduledExecutor;
    private final boolean active;

    /**
     * Constructor for a new server manager
     * @param port to accept connections on
     */
    public ServerManager(int port) {
        this.socketManager = new SocketManager(port);
        this.sockets = new ArrayList<>();
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.scheduledExecutor.scheduleAtFixedRate(socketManager::checkLifelines, 1, 5, TimeUnit.SECONDS);
        this.socketManager.acceptNewConnections();
        this.start();

        this.active = true;
    }

    /**
     * Start the server on a new thread
     */
    public void start(){
        new Thread( () -> {
            while(true){
                socketManager.handle();
            }
        }).start();

    }


}
