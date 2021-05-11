package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("We Are Family");
//        Parent root = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        this.root = setRoot("fxml/HomePage.fxml");
        primaryStage.setScene(new Scene(getRoot(), 1280, 800));
        primaryStage.show();

        Timer timer = new Timer();
        TimerTask task = new Polling();
        timer.schedule(task, 1000, 2000);
    }

    private static void sendNotification(String title,String content) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon1.png");

        TrayIcon trayIcon = new TrayIcon(image, "Polling");
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        trayIcon.displayMessage(title, content, TrayIcon.MessageType.INFO);
        tray.remove(trayIcon);
    }

    static private class Polling extends TimerTask
    {
        public static int i = 0;
        public void run()
        {

            try {
                if(i%5==0) {
                    Main.sendNotification("testTitle1", "testContent");
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public Parent setRoot(String root)
    {
        try{
            Parent page = FXMLLoader.load(getClass().getResource(root));
            return page;
        }catch (java.io.IOException e){
            System.out.println("root is wrong");
            return null;
        }
    }

    public Parent getRoot()
    {
        return this.root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
