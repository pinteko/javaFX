import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainChatController implements Initializable {
    @FXML
    public VBox mainChatPanel;

    @FXML
    public TextArea mainChatArea;

    @FXML
    public ListView contactList;

    @FXML
    public TextField inputField;

    @FXML
    public Button btnSend;

    Label label = new Label();

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
        else if (contactList.getSelectionModel().getSelectedItem() == null) {
            mainChatArea.appendText("To all: " + message + System.lineSeparator());
            inputField.clear();
        }
        else {
            mainChatArea.appendText("To " + contactList.getSelectionModel().getSelectedItem() +  message + System.lineSeparator());
            inputField.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var contacts = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            contacts.add("Contact#" + (i + 1));
        }

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Private");
        MenuItem item2 = new MenuItem("BlackList");


        item1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    mainChatArea.appendText("Hey, " +  contactList.getSelectionModel().getSelectedItem() + ", maybe Private?");

                }
            }
        });

        item2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    mainChatArea.appendText("Hey, " +  contactList.getSelectionModel().getSelectedItem() + ", you are blocked!");
                }
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
}