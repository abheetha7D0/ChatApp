package controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Message;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientFormController extends Thread {


    public TextField txtMessage;
    public Socket socket;

    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    public AnchorPane messageControllerPane;
    public Label lblContactName;
    public VBox messageVBox;
    ObservableList<Label> observableList = FXCollections.observableArrayList();
    String imageFilePath;

    public void initialize() {
        System.out.println("Initialized method = " + LogInFormController.userName);
        lblContactName.setText(LogInFormController.userName);
        try {
            socket = new Socket("localhost", 5000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageSend() {
        String msg = txtMessage.getText().trim();
        try {
            if (imageFilePath != null) {
                System.out.println("File path : " + imageFilePath);

                if (msg != null) {

                    objectOutputStream.writeObject(new Message(LogInFormController.userName, msg, imageFilePath));
                    objectOutputStream.flush();

                } else {

                    objectOutputStream.writeObject(new Message(LogInFormController.userName, "", imageFilePath));
                    objectOutputStream.flush();

                }
            } else {
                try {
                    if (msg != null) {

                        objectOutputStream.writeObject(new Message(LogInFormController.userName, msg, null));
                        objectOutputStream.flush();

                    } else {

                        objectOutputStream.writeObject(new Message(LogInFormController.userName, "", null));
                        objectOutputStream.flush();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("flushed");

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageFilePath = null;
        txtMessage.setText("");
        if (msg.equalsIgnoreCase("Bye") || (msg.equalsIgnoreCase("logout"))) {
            System.exit(0);
        }
    }

    @Override
    public void run() {

        try {
            System.out.println("returned");

            while (true) {
                Message msg = (Message) objectInputStream.readObject();
                System.out.println("Msg In Client Thread : " + msg);
                if (msg.getName().equalsIgnoreCase(LogInFormController.userName + ": ")) {
                    continue;
                } else if (msg.getMessage().equalsIgnoreCase("bye")) {
                    break;
                }
                Thread.sleep(500);
                Label label = new Label();
                Platform.runLater(() -> {
                    String emojiStyle = "-fx-font-size: 25px";
                    if (msg.getImage() != null) {
                        Image image = new Image(msg.getImage());
                        ImageView imageView = new ImageView();
                        imageView.setImage(image);
                        imageView.setFitWidth(200);
                        imageView.setFitHeight(100);

                        if (!msg.getMessage().isEmpty()) {
                            System.out.println(!msg.getMessage().isEmpty());

                            label.setText(msg.getName() + " : " + msg.getMessage() + "\n\n");

                            label.setGraphic(imageView);
                            label.setContentDisplay(ContentDisplay.BOTTOM);
                        } else {

                            label.setText(msg.getName() + " : " + "\n\n");

                            label.setGraphic(imageView);
                            label.setContentDisplay(ContentDisplay.BOTTOM);
                        }
                    } else if (msg.getImage() == null) {
                        if (!msg.getMessage().isEmpty()) {

                            label.setText(msg.getName() + " : " + msg.getMessage() + "\n\n");

                        }
                    }
                    label.setStyle("-fx-background-color: #1f1f1f;-fx-text-fill: white;-fx-padding: 0 10 0 10;-fx-font-size: 13px");
                    observableList.addAll(label);
                    messageVBox.getChildren().clear();
                    messageVBox.setSpacing(10);
                    for (int i = 0; i < observableList.size(); i++) {
                        messageVBox.getChildren().addAll(observableList.get(i));
                    }
                });
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendBtnOnAction(MouseEvent mouseEvent) {
        messageSend();
    }

    public void fileChooserOnClick(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File file = fileChooser.showOpenDialog(messageControllerPane.getScene().getWindow());

        imageFilePath = file.toURI().toString();
    }

    public void closeOnClicked(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
