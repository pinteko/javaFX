package homeworks.homework2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler extends Thread{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

  public Handler(Socket socket) throws IOException {
        this.socket = socket;
          System.out.println("Client connected");
          in = new DataInputStream(socket.getInputStream());
          out = new DataOutputStream(socket.getOutputStream());
          start();
    }

    @Override
    public void run() {
      String word;
      try {
          while (true) {
              word = in.readUTF();
              if (word.equalsIgnoreCase("/quit")) {
                  break;
              }
              int id = (int) this.getId();
              if (word.substring(0, 2).equals("/w")) {
                  try {
                      ConsoleServer.clients.get(Integer.parseInt(word.substring(3, 5))).send(word.substring(5), id);
                  }
                  catch (NumberFormatException e) {}
              }
//              for (int i = 0; i < ConsoleServer.clients.size(); i++) {
//                  if (i == id) {
//                      continue;
//                  }
//                      ConsoleServer.clients.get(i).send(word, id);
//              }
              else {
                  for (Handler hd : ConsoleServer.clients) {
                      if (hd.getId() == id) {
                          continue;
                      }
                      hd.send(word, id);
                  }
              }
          }
      }
      catch (IOException e) {

      }
    }
    private void send (String message, int i) {
      try {
          out.writeUTF("From client " + i + ": " + message + "\n");
//          out.writeUTF("From client " + i + ": " + message + "\n");
          out.flush();
      }
      catch (IOException e) {

      }
    }
}
