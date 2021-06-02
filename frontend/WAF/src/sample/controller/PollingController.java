package sample.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.Main;
import sample.controller.sidePanel.SidePanelController;
import sample.global.GlobalVariable;
import sample.tool.response.comment.CommentNoticeResponse;
import sample.tool.response.notice.CheckNoticeResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PollingController extends TimerTask {

    public static Circle noticeCircle = null;
    public static Label noticeCircleLabel = null;

    public PollingController(){
        Timer timer = new Timer();
        TimerTask task = this;
        timer.schedule(task, 1000, 3000);
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

    public void run()
    {
        try {
            HttpResponse response = RequestController.post(GlobalVariable.server+"notifications/checkNotification",
                    new String[]{"accessKey", GlobalVariable.accessKey}
            );
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                CheckNoticeResponse jsonResponse = gson.fromJson(responseString, CheckNoticeResponse.class);
                if(jsonResponse.errors.length==0){
                    sendNotification("WeAreFamily", jsonResponse.message);
                }

            } else {
                ToastCaller toast;
                if(Main.connectErrorCount==0)
                    toast = new ToastCaller("Heroku已達到每小時3600筆詢問的限制",GlobalVariable.mainStage,ToastCaller.ERROR,550);
                System.out.println(response.getStatusLine());
                Main.connectErrorCount++;
                Main.connectErrorCount%=2;
            }
        }
        catch (IOException | AWTException e) {
            ToastCaller toast;
            if(Main.connectErrorCount==0)
                toast = new ToastCaller("無法與伺服器連線",GlobalVariable.mainStage,ToastCaller.ERROR);
            Main.connectErrorCount++;
            Main.connectErrorCount%=2;
            e.printStackTrace();
        }

        if(noticeCircleLabel!=null && noticeCircle != null){
            try {
                HttpResponse response = RequestController.post(GlobalVariable.server+"comments/getUnreadCommentCount",
                        new String[]{"accessKey", GlobalVariable.accessKey}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    CommentNoticeResponse jsonResponse = gson.fromJson(responseString, CommentNoticeResponse.class);
                    if(jsonResponse.errors.length==0){
                        if(jsonResponse.count==0){
                            Platform.runLater(() -> {
                                noticeCircle.setVisible(false);
                                noticeCircleLabel.setVisible(false);
                            });
                        }
                        else{
                            Platform.runLater(() -> {
                                noticeCircle.setVisible(true);
                                noticeCircleLabel.setVisible(true);
                                int tmp = jsonResponse.count;
                                if(tmp>99)
                                    tmp=99;
                                this.noticeCircleLabel.setText(Integer.toString(tmp));
                            });
                        }

                    }
                } else {
                    System.out.println(response.getStatusLine());
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }
}
