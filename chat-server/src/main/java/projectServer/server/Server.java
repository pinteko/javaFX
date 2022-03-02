package projectServer.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projectServer.auth.AuthService;
import props.PropertyReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    public static final String REGEX = "%!%";
    private final int port;
    private final AuthService authService;
    private final List<ClientHandler> clientHandlers;
    private final ExecutorService executor;
    private static final Logger log = LogManager.getLogger("projectServer");
    private static final Logger errorLog = LogManager.getLogger("errors");

    public Server(AuthService authService) {
        port = PropertyReader.getInstance().getPort();
        this.clientHandlers = new ArrayList<>();
        this.authService = authService;
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server start!");
            while (true) {
                log.trace("Waiting for connection......");
                var socket = serverSocket.accept();
                log.info("Client connected");
                var clientHandler = new ClientHandler(socket, this);
                    clientHandler.handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLog.error("Server not started");
        } finally {
            shutdown();
        }
    }

    public void privateMessage(String from, String to, String message) {
        message = "/private" + REGEX + from + REGEX + to + REGEX + message;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUserNick().equals(to)) {
                clientHandler.send(message);
            }
        }
    }

    public void broadcastMessage(String from, String message) {
        message = "/broadcast" + REGEX + from + REGEX + message;
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send(message);
        }
    }
    public void tetATetMessage(String from, String to, String message) {
        message = "/tetATet" + REGEX + from + REGEX + to + REGEX + message;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUserNick().equals(to)) {
                clientHandler.send(message);
            }
        }
    }

    public synchronized void removeAuthorizedClientFromList(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        sendOnlineClients();
    }

    public void ignoreUser(String from, String to, String message) {
        message = "/ignore" + REGEX + from + REGEX + to + REGEX + message;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUserNick().equals(to)) {
                clientHandler.send(message);
            }
        }
    }

    public synchronized void addAuthorizedClientToList(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
        sendOnlineClients();
    }

    public void sendOnlineClients() {
        var sb = new StringBuilder("/list");
        sb.append(REGEX);
        for (ClientHandler clientHandler : clientHandlers) {
            sb.append(clientHandler.getUserNick());
            sb.append(REGEX);
        }
        var message = sb.toString();
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send(message);
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUserNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    private void shutdown() {
        authService.stop();
        executor.shutdown();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public ExecutorService getExecutorService() {
        return executor;
    }
}