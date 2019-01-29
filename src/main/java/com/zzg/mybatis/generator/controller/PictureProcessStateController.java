package com.zzg.mybatis.generator.controller;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import static javafx.scene.paint.Color.DARKSEAGREEN;

public class PictureProcessStateController {
    private ImageView dbImage = new ImageView("icons/SSH_tunnel.png");
    private final Rectangle rect = new Rectangle(20, 20, 30, 30);
    private final RotateTransition rotateTransition = new RotateTransition();
    private final Text text = new Text("");
    private final Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
    private double initX;
    private double initY;
    private Stage parentStage;
    private final Button button = new Button("");
    public void setDialogStage(Stage stage) {
        this.parentStage = stage;
    }

    public void startPlay() {
        dbImage.setFitHeight(192);
        dbImage.setFitWidth(798);
        Group rootGroup = new Group();
        Scene scene = new Scene(rootGroup, 800, 212, Color.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(scene);
        dialogStage.initOwner(parentStage);
        dialogStage.centerOnScreen();
        dialogStage.setTitle("OverSSH");

        rect.setArcHeight(10);
        rect.setArcWidth(10);
        rect.setFill(DARKSEAGREEN);

        rotateTransition.setNode(rect);
        rotateTransition.setDuration(Duration.seconds(0.8d));
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(720);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setAutoReverse(true);
        VBox vBoxRect = new VBox();
        vBoxRect.setAlignment(Pos.TOP_CENTER);
        vBoxRect.getChildren().add(rect);
        VBox.setMargin(rect, new Insets(125, 0, 0, 350));
        rotateTransition.play();


        text.setFont(Font.font(12));
        VBox vBoxLabel = new VBox();
        vBoxLabel.getChildren().add(text);
        VBox.setMargin(text, new Insets(175, 0, 15, 40));


        button.setPrefSize(90, 40);
        HBox hBoxButton = new HBox();
        hBoxButton.setPrefSize(505, 170);
        hBoxButton.getChildren().add(button);
        hBoxButton.setAlignment(Pos.BOTTOM_RIGHT);
        hBoxButton.getStylesheets().add(Thread.currentThread().getContextClassLoader().getResource("style.css").toExternalForm());
        HBox.setMargin(button, new Insets(0, 15, 5, 0));
        button.setStyle("-fx-border-width: 0px;");
        button.setStyle("-fx-border-color: transparent;");
        button.setStyle("-fx-background-color: transparent;");
        rootGroup.getChildren().addAll(dbImage, vBoxRect, vBoxLabel, hBoxButton);
        dialogStage.show();

        button.setOnMouseClicked((event) -> dialogStage.close());

        rootGroup.setOnMousePressed((me) -> {
                initX = me.getScreenX() - dialogStage.getX();
                initY = me.getScreenY() - dialogStage.getY();
        });
        rootGroup.setOnMouseDragged((me) -> {
            dialogStage.setX(me.getScreenX() - initX);
            dialogStage.setY(me.getScreenY() - initY);
        });
    }

    public void playFailState(String message, boolean showButton) {
        rect.setFill(Color.ORANGERED);
        rotateTransition.stop();
        dbImage.setImage(new Image("icons/SSH_tunnel_disconnected.png"));
        rotateTransition.setDuration(Duration.seconds(3));
        rotateTransition.play();
        text.setText(message);
        if (showButton) {
            showCloseButton();
        }
    }

    private void showCloseButton() {
        button.getStyleClass().add("btn");
        button.getStyleClass().add("btn-default");
        button.setStyle("-fx-border-width: 1px;");
        button.setStyle("-fx-background-color: #fff;");
        button.setText("我知道了");
    }

    public void playSuccessState(String message, boolean showButton) {
        rect.setFill(DARKSEAGREEN);
        rotateTransition.stop();
        dbImage.setImage(new Image("icons/SSH_tunnel.png"));
        rotateTransition.setDuration(Duration.seconds(0.8));
        rotateTransition.play();
        text.setText(message);
        if (showButton) {
            showCloseButton();
        }
    }

    public void close() {
        dialogStage.close();
    }
}
