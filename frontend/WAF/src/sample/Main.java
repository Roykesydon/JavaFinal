package sample;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import sample.response.notice.CheckNoticeResponse;
public class Main extends Application {
    private Parent root;
    public static int connectErrorCount;
    @Override
    public void start(Stage primaryStage) throws Exception{
        GlobalVariable.mainStage = primaryStage;
        primaryStage.setTitle("We Are Family");
//        Parent root = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        this.root = setRoot("fxml/HomePage.fxml");
        primaryStage.setScene(new Scene(getRoot(), 1280, 800));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new javafx.scene.image.Image("/sample/css/WAF.png"));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        Timer timer = new Timer();
        TimerTask task = new Polling();
        timer.schedule(task, 1000, 1000);

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

    private static class Polling extends TimerTask
    {
        public void run()
        {
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/notifications/checkNotification",
                        new String[]{"accessKey", GlobalVariable.accessKey}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    CheckNoticeResponse jsonResponse = gson.fromJson(responseString, CheckNoticeResponse.class);
                    if(jsonResponse.errors.length==0){
                        Main.sendNotification("WeAreFamily", jsonResponse.message);
                    }

                } else {
                    System.out.println(response.getStatusLine());
                }
            }
            catch (IOException | AWTException e) {
                ToastCaller toast;
                if(Main.connectErrorCount==0)
                    toast = new ToastCaller("無法與伺服器連線",GlobalVariable.mainStage,ToastCaller.ERROR);
                Main.connectErrorCount++;
                Main.connectErrorCount%=10;
                e.printStackTrace();
            }
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
