package homeworks.homework2;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ConsoleClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8189;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread clientConsoleThread;

    public static void main(String[] args) {
        new ConsoleClient().start();
    }

    public void start() {
        try (var socket = new Socket(HOST, PORT)) {
            System.out.println("Connected to server");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            startConsoleThread();

            while (true) {
                var message = in.readUTF();
                System.out.println("Received: " + message);
            }
        } catch (SocketException e) {
            System.out.println("Connection to server has been lost");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() throws IOException {
        if (clientConsoleThread.isAlive()) {
            clientConsoleThread.interrupt();
        }
        System.out.println("Client stopped");
    }

    private void startConsoleThread() {
        clientConsoleThread = new Thread(() -> {
            try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.print("Enter message for users >>>> ");
                while (!Thread.currentThread().isInterrupted()) {
                    if (reader.ready()) {
                        var clientMessage = reader.readLine();
                        if (clientMessage.equalsIgnoreCase("/quit")) {
                            out.writeUTF("/end");
                            shutdown();
                        }
                        out.writeUTF(clientMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientConsoleThread.start();
    }
}