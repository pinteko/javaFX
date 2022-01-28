package classworks.classwork2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SuperSimpleConsoleClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8189;

    public static void main(String[] args) {
        try (var socket = new Socket(HOST, PORT);
             var scanner = new Scanner(System.in)) {

            System.out.println("Connected to server");
            var in = new DataInputStream(socket.getInputStream());
            var out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                var message = scanner.nextLine();
                out.writeUTF(message);
                Thread.sleep(100);
                message = in.readUTF();
                System.out.println("Received: " + message);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}