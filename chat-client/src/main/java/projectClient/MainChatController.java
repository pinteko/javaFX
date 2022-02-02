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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MainChatController implements Initializable, MessageProcessor {
    public static final String REGEX = "%!%";

    private String nick;
    private NetworkService networkService;

    @FXML
    public VBox loginPanel;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public VBox mainChatPanel;

    @FXML
    public TextField inputField;

    @FXML
    public TextArea mainChatArea;

    @FXML
    public ListView contactList;

    @FXML
    public Button btnSend;

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
//        else {
//            networkService.sendMessage("/private" + REGEX + recipient + REGEX + message);
//            inputField.clear();
//        }
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
                break;
            case "/broadcast" :
                mainChatArea.appendText(splitMessage[1] + ": " + splitMessage[2] + System.lineSeparator());
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
                break;
//            case "/private" :
//                var from = splitMessage[1];
//                var to = splitMessage[2];
//                var mess = splitMessage[3];
//                mainChatArea.appendText("Private from " + splitMessage[1] + ": " + splitMessage[3] + System.lineSeparator());
//                break;
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

        var message = "/auth" + REGEX + login + REGEX + password;

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
}
