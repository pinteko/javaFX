package projectServer;


import projectServer.auth.InDatabaseAuthService;
import projectServer.auth.InMemoryAuthService;
import projectServer.server.Server;

public class App {
    public static void main(String[] args) {
        new Server(new InDatabaseAuthService()).start();
    }
}
