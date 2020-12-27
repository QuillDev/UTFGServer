package tech.quilldev;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    // server socket
    private final SocketManager socketManager;
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * Constructor for a new server manager
     * @param port to accept connections on
     */
    public ServerManager(int port) {
        this.socketManager = new SocketManager(port);
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.scheduledExecutor.scheduleAtFixedRate(socketManager::handle, 0 , 20, TimeUnit.MILLISECONDS);
        this.scheduledExecutor.scheduleAtFixedRate(socketManager::checkLifelines, 0 , 1, TimeUnit.SECONDS);
        this.socketManager.acceptNewConnections();
    }


}
