package projectServer.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projectServer.error.WrongCredentialsException;
import props.PropertyReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Server server;
    private String user;
    private String currentThread;
    private static final Logger log = LogManager.getLogger("projectServer");
    private static final Logger errorLog = LogManager.getLogger("errors");

    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            log.info("Handler created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        server.getExecutorService().execute(() -> {
                authorize();
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    var message = in.readUTF();
                    handleMessage(message);
                } catch (IOException e) {
                    log.info("Connection broken with user " + user);
                    server.removeAuthorizedClientFromList(this);
                    break;
                }
                finally {
                    Thread.currentThread().interrupt();
                }
            }
        });

        server.getExecutorService().execute(() -> {
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.user == null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("This handlerThread was removed");
            }
        });
    }

    private void handleMessage(String message) {
        var splitMessage = message.split(Server.REGEX);
        try {
            switch (splitMessage[0]) {
                case "/broadcast":
                    server.broadcastMessage(user, splitMessage[1]);
                    break;
                case "/private":
                    server.privateMessage(user, splitMessage[1], splitMessage[2]);
                    break;
                case "/ignore":
                    server.ignoreUser(user, splitMessage[1], splitMessage[2]);
                    break;
                case "/tetATet":
                    server.tetATetMessage(user, splitMessage[1], splitMessage[2]);
                    break;
                case "/change_nick":
                    String nick = server.getAuthService().changeNick(this.user, splitMessage[1]);
                    if (nick.equals(this.user)) {
                        send("/error" + Server.REGEX + "This nick (" + nick + ") already used!");
                        log.error("This nick (" + nick + ") already used!");
                        errorLog.error("This nick (" + nick + ") already used!");
                    }
                    else {
                        server.removeAuthorizedClientFromList(this);
                        this.user = nick;
                        server.addAuthorizedClientToList(this);
                        send("/change_nick_ok");
                    }
                    break;
                case "/change_pass":
                    server.getAuthService().changePassword(this.user, splitMessage[1], splitMessage[2]);
                    send("/change_pass_ok");
                    break;
                case "/remove":
                    server.getAuthService().deleteUser(splitMessage[1], splitMessage[2]);
                    this.socket.close();
                    break;
                case "/register":
                    server.getAuthService().createNewUser(splitMessage[1], splitMessage[2], splitMessage[3], splitMessage[4]);
                    send("register_ok:");
                    break;
            }
        }
            catch (IOException e) {
                send("/error" + Server.REGEX + e.getMessage());
                log.error("/error" + Server.REGEX + e.getMessage());
                errorLog.error("/error" + Server.REGEX + e.getMessage());
            }
        }

    private void authorize() {
        log.info("Authorizing");
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    var message = in.readUTF();
                    if (message.startsWith("/auth")) {
                        var parsedAuthMessage = message.split(Server.REGEX);
                        var response = "";
                        String nickname = null;
                        try {
                            nickname = server.getAuthService().authorizeUserByLoginAndPassword(parsedAuthMessage[1], parsedAuthMessage[2]);
                        } catch (WrongCredentialsException e) {
                            response = "/error" + Server.REGEX + e.getMessage();
                            log.error("Wrong credentials, nick " + parsedAuthMessage[1]);
                            errorLog.error("Wrong credentials, nick " + parsedAuthMessage[1]);
                        }

                        if (server.isNickBusy(nickname)) {
                            response = "/error" + Server.REGEX + "this client already connected";
                            log.error("Nick busy: " + nickname);
                            errorLog.error("Nick busy: " + nickname);
                        }
                        if (!response.equals("")) {
                            send(response);
                        } else {
                            this.user = nickname;
                            server.addAuthorizedClientToList(this);
                            try {
                                var file = new File("" + parsedAuthMessage[3]);
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                            }
                            catch (IOException e) {e.printStackTrace();}
                            send("/auth_ok" + Server.REGEX + nickname);
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void send(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getUserNick() {
        return this.user;
    }
}