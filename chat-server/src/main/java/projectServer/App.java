package projectServer;


import projectServer.auth.InMemoryAuthService;
import projectServer.server.Server;

public class App {
    public static void main(String[] args) {
        new Server(new InMemoryAuthService()).start();
    }
}
