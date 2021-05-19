package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;


//ToastCaller toast = new ToastCaller("Register Success!",primaryStage,ToastCaller.ERROR);
public class ToastCaller {
    public final static int SUCCESS = 1;
    public final static int ERROR = 2;
    public ToastCaller(String message,Stage mainStage,int toastType) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage toastStage = new Stage();
                toastStage.initStyle(StageStyle.TRANSPARENT);
                Label label= setLabelInfo(message,toastType);

                Scene scene=new Scene(label);
                scene.setFill(null);

                toastStage.setScene(scene);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        FadeTransition ft = new FadeTransition(Duration.millis(250),label);
                        ft.setFromValue(1.0);
                        ft.setToValue(0.0);
                        ft.play();
                    }
                }, 2000);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(()->toastStage.close());
                    }
                }, 2250);

                toastStage.setX(mainStage.getX()+mainStage.getHeight()/2);
                toastStage.setY(mainStage.getY()+mainStage.getWidth()/2);
                FadeTransition ft = new FadeTransition(Duration.millis(250),label);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                toastStage.show();
            }
        });

    }
    public Label setLabelInfo(String message,int toastType){
        Label label = new Label();
        label.setText(message);
        label.setTextFill(Color.rgb(255,255,255));
        label.setPrefHeight(100);
        if(toastType == SUCCESS)
            label.setStyle("-fx-background-color: rgba(70,230,140,255);-fx-background-radius: 25;-fx-border-radius: 25;");
        else if(toastType == ERROR)
            label.setStyle("-fx-background-color: rgba(205,51,51,255);-fx-background-radius: 25;-fx-border-radius: 25;");
        label.setPrefWidth(450);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(30));

        return label;
    }
}
