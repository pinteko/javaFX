package projectClient;


import com.sun.javafx.scene.control.InputField;
import projectClient.network.MessageProcessor;
import projectClient.network.NetworkService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MainChatController implements Initializable, MessageProcessor {
    public static final String REGEX = "%!%";

    private String nick;
    private String filePath;
    private NetworkService networkService;
    private String ignore;
    private String tetATet;

    @FXML
    private VBox loginPanel;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox mainChatPanel;

    @FXML
    private TextField inputField;

    @FXML
    private TextArea mainChatArea;

    @FXML
    private ListView contactList;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPassField;

    @FXML
    private VBox changePasswordPanel;

    @FXML
    private TextField newNickField;

    @FXML
    private VBox changeNickPanel;

    @FXML
    private Button btnSend;

    private Object ArrayList;

    public void connectToServer(ActionEvent actionEvent) {
    }

    public void disconnectFromServer(ActionEvent actionEvent) {
    }

    public void mockAction(ActionEvent actionEvent) {
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(1);
    }

    public void showHelp(ActionEvent actionEvent) {
    }

    public void showAbout(ActionEvent actionEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {
        var message = inputField.getText();
        if (message.isBlank()) {
            return;
        }
       var recipient = contactList.getSelectionModel().getSelectedItem();
        if (recipient == null) {
            networkService.sendMessage("/broadcast" + REGEX  + message);
            inputField.clear();
        }
        else if (recipient.equals("ALL")) {
            networkService.sendMessage("/broadcast" + REGEX  + message);
            inputField.clear();
        }
        else {
            networkService.sendMessage("/private" + REGEX + recipient + REGEX + message);
            inputField.clear();
        }
//        mainChatArea.appendText(recipient + ": " + message + System.lineSeparator());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.networkService = new NetworkService(this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseIncomingMessage(message));
    }

    private void parseIncomingMessage(String message) {
        var splitMessage = message.split(REGEX);
        switch (splitMessage[0]) {
            case "/auth_ok" :
                this.nick = splitMessage[1];
                loginPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                printHistory();
                break;
            case "/broadcast" :
                var broadcastMess = splitMessage[1] + ": " + splitMessage[2] + System.lineSeparator();
                mainChatArea.appendText(broadcastMess);
                writeHistory(broadcastMess);
                break;
            case "/error" :
                showError(splitMessage[1]);
                System.out.println("got error " + splitMessage[1]);
                break;
            case "/list" :
                var contacts = new ArrayList<String>();
                contacts.add("ALL");
                for (int i = 1; i < splitMessage.length; i++) {
                    contacts.add(splitMessage[i]);
                }
                contactList.setItems(FXCollections.observableList(contacts));
                contextMenu(contacts);
                break;
            case "/private" :
                var privateMess = "Private from " + splitMessage[1] + ": " + splitMessage[3] + System.lineSeparator();
                mainChatArea.appendText(privateMess);
                writeHistory(privateMess);
                break;
            case "/tetATet" :
                var tetATet = splitMessage[1] + " offer you " + splitMessage[3] + System.lineSeparator();
                mainChatArea.appendText(tetATet);
                writeHistory(tetATet);
                break;
            case "/ignore" :
                var ignore = splitMessage[3] + "by " + splitMessage[1] + System.lineSeparator();
                mainChatArea.appendText(ignore);
                writeHistory(ignore);
                break;
            case "/change_pass_ok":
                changePasswordPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            case "/change_nick_ok":
                changeNickPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            default:
                var def = splitMessage[0] + System.lineSeparator();
                mainChatArea.appendText(def);
                writeHistory(def);
                break;
        }
    }

    private void showError(String message) {
        var alert = new Alert(Alert.AlertType.ERROR,
                "An error occured: " + message,
                ButtonType.OK);
        alert.showAndWait();
    }

    public void sendAuth(ActionEvent actionEvent) {
        var login = loginField.getText();
        var password = passwordField.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        filePath = "History for " + login + ".txt";
        var message = "/auth" + REGEX + login + REGEX + password + REGEX + filePath;
        if (!networkService.isConnected()) {
            try {
                networkService.connect();
            } catch (IOException e) {
                e.printStackTrace();
                showError(e.getMessage());

            }
        }
        networkService.sendMessage(message);
    }

    private void contextMenu(ArrayList<String> contacts) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Private");
        MenuItem item2 = new MenuItem("BlackList");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                var recipient = contactList.getSelectionModel().getSelectedItem();
                tetATet = "/tetATet" + REGEX +  recipient + REGEX + "private chat.";
                networkService.sendMessage(tetATet);
            }
        });
        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                var recipient = contactList.getSelectionModel().getSelectedItem();
                ignore = "/ignore" + REGEX +  recipient + REGEX + "You are blocked ";
                networkService.sendMessage(ignore);
            }
        });
        contextMenu.getItems().addAll(item1, item2);
        contactList.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                contextMenu.show(contactList, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });
        contactList.setItems(FXCollections.observableList(contacts));
    }

    public void sendChangeNick(ActionEvent actionEvent) {
        if (newNickField.getText().isBlank()) return;
        networkService.sendMessage("/change_nick" + REGEX + newNickField.getText());
    }

    public void sendChangePass(ActionEvent actionEvent) {
        if (newPasswordField.getText().isBlank() || oldPassField.getText().isBlank()) return;
        networkService.sendMessage("/change_pass" + REGEX + oldPassField.getText() + REGEX + newPasswordField.getText());
    }

    public void sendEternalLogout(ActionEvent actionEvent) {
        networkService.sendMessage("/remove");
    }

    public void returnToChat(ActionEvent actionEvent) {
        changeNickPanel.setVisible(false);
        changePasswordPanel.setVisible(false);
        mainChatPanel.setVisible(true);
    }

    public void showChangeNick(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changeNickPanel.setVisible(true);
    }

    public void showChangePass(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changePasswordPanel.setVisible(true);
    }

    private void writeHistory(String mess) {
        try (var fos = new FileOutputStream("" + filePath, true)) {
            fos.write(mess.getBytes(StandardCharsets.UTF_8));}
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printHistory() {
        try (RandomAccessFile raf = new RandomAccessFile("" + filePath, "r");) {
            long len = raf.length();
            if (len <= 3) {return;}
            int count = 0;
            long pos = len;
            while (pos > 0) {
                    --pos;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        count++;
                        if (count == 100) {
                            pos++;
                            break; }
                    }
            }
            long firstPosPosition = pos;
            while (pos < len - 1) {
                if (pos == firstPosPosition) {
                    raf.seek(pos);
                    mainChatArea.appendText(raf.readLine() + System.lineSeparator());
                }
                    pos++;
                    raf.seek(pos);
                    if (raf.readByte() == '\n' && pos != len - 1) {
                        mainChatArea.appendText(raf.readLine() + System.lineSeparator());
                    }
                }
        } catch (IOException e) {
                 e.printStackTrace();
         }
        }
    }


