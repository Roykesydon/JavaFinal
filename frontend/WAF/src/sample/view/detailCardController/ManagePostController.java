package sample.view.detailCardController;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.tool.RequestController;
import sample.tool.ToastCaller;
import sample.global.GlobalVariable;
import sample.tool.response.account.SetIdentityCodeResponse;
import sample.tool.response.detailCard.GetProfileAndOwnPostResponse;
import sample.tool.response.detailCard.JoinPostResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagePostController {
    public Label ownerIDLabel;
    public Label categoryLabel;
    public Label priceLabel;
    public Label joinPeopleLabel;
    public VBox managePostVBox;
    public AnchorPane anchorPane;
    public VBox listVBox;
    public VBox manageVBox;

    public ProgressIndicator loading;

    public Button completeBtn;
    public Button deleteBtn;
    public Label secondaryIDLabel;
    public Label secondaryCateLabel;
    public Label secondaryPriceLabel;
    public Label secondaryJoinLabel;

    private List<String> postData = new ArrayList<>();
    private GetProfileAndOwnPostResponse classJsonResponse;



    public void makeButton(String name, String id, String postID)  {
        Button button = new Button(name);
        button.setId(id);
        if(name.equals("COMPLETE"))
        {
            completeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> choosePeopleFunction(postID));
            completeBtn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:19;");
        }
        else if(name.equals("DELETE"))
        {
            deleteBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> deletePeopleFunction(postID));
            deleteBtn.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor+";-fx-font-size:19;");
        }
        else if(name.equals("LEAVE")){
            completeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> leaveFunction(postID));
            completeBtn.setText("LEAVE");
            completeBtn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:19;");
        }
        else
            System.out.println("字串比對錯誤(ManagePostController makeButton method)");
    }

    public JFXCheckBox makeCheckBox(String joinPeopleID,String postID)  {
        JFXCheckBox checkBox = new JFXCheckBox(joinPeopleID);
        checkBox.setId(postID);
        return checkBox;
    }

    public void leaveFunction(String postID)
    {
        new Thread(new Runnable() {
            public void run() {
                loading.setVisible(true);
                try {
                    HttpResponse response= RequestController.post(GlobalVariable.server+"posts/removeUser",
                            new String[]{"accessKey",GlobalVariable.accessKey},
                            new String[]{"postID",postID},
                            new String[]{"removeUserID",GlobalVariable.userID}
                    );
                    String responseString= EntityUtils.toString(response.getEntity());
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        Gson gson =new Gson();
                        JoinPostResponse gsonResponse = gson.fromJson(responseString,JoinPostResponse.class);
                        if(Arrays.toString(gsonResponse.errors).equals("[]")){
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    manageVBox.getChildren().remove(anchorPane);
                                }
                            });
                            ToastCaller toast = new ToastCaller("退出成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                        }
                        else{
                            ToastCaller toast = new ToastCaller("退出失敗",GlobalVariable.mainStage,ToastCaller.ERROR);
                            System.out.println("failed");
                        }
                    }
                    else{
                        System.out.println(response.getStatusLine().getStatusCode());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                loading.setVisible(false);
            }
        }).start();
    }

    public void choosePeopleFunction(String postID)
    {
        new Thread(new Runnable() {
            public void run() {
                String tmpList = "";
                String chooseList = "";

                for(Node checkBox :listVBox.getChildren())
                    if(((JFXCheckBox)checkBox).isSelected())
                        tmpList += ((JFXCheckBox) checkBox).getText()   +",";
                if(!tmpList.equals(""))
                {
                    loading.setVisible(true);
                    /* remove last ',' */
                    if(tmpList.charAt(tmpList.length() - 1) == ',')
                        chooseList = tmpList.substring(0,tmpList.length() - 1);
                    try {
                        HttpResponse response= RequestController.post(GlobalVariable.server+"posts/completePost",
                                new String[]{"accessKey",GlobalVariable.accessKey},
                                new String[]{"postID",postID},
                                new String[]{"chooseList",chooseList}
                        );
                        String responseString= EntityUtils.toString(response.getEntity());
                        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                            Gson gson =new Gson();
                            SetIdentityCodeResponse gsonResponse = gson.fromJson(responseString,SetIdentityCodeResponse.class);
                            if(Arrays.toString(gsonResponse.errors).equals("[]")){
                                Platform.runLater(new Runnable() {
                                    @Override public void run() {
                                        manageVBox.getChildren().remove(anchorPane);
                                    }
                                });
                                ToastCaller toast = new ToastCaller("添加成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                            }
                            else{
                                ToastCaller toast = new ToastCaller("添加失敗",GlobalVariable.mainStage,ToastCaller.ERROR);
                                System.out.println("failed");
                            }
                        }
                        else{
                            System.out.println(response.getStatusLine().getStatusCode());
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    loading.setVisible(false);
                }
                else{
                    ToastCaller toast = new ToastCaller("必須選擇至少一人",GlobalVariable.mainStage,ToastCaller.ERROR);
                }
            }
        }).start();
    }

    public void deletePeopleFunction(String postID)
    {
        new Thread(new Runnable() {
            public void run() {
                loading.setVisible(true);
                try {
                    HttpResponse response=RequestController.post(GlobalVariable.server+"posts/deletePost",
                            new String[]{"accessKey",GlobalVariable.accessKey},
                            new String[]{"postID",postID}
                    );
                    String responseString= EntityUtils.toString(response.getEntity());
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        Gson gson =new Gson();
                        SetIdentityCodeResponse gsonResponse = gson.fromJson(responseString,SetIdentityCodeResponse.class);
                        if(Arrays.toString(gsonResponse.errors)=="[]"){
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    manageVBox.getChildren().remove(anchorPane);
                                }
                            });
                            ToastCaller toast = new ToastCaller("刪除成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
//                    getOwnAndJoinPost();
                        }
                        else{
//                    deleteStatusLabel.setText("失敗!");
                            ToastCaller toast = new ToastCaller("刪除失敗",GlobalVariable.mainStage,ToastCaller.ERROR);
                            System.out.println("failed");
                        }
                    }
                    else{
                        System.out.println(response.getStatusLine().getStatusCode());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                loading.setVisible(false);
            }
        }).start();
    }

    public void setData(String ownerID, String category, String price, String postID, String joinPeople, ArrayList<String> joinList,VBox manageVBox){
        loading.setVisible(false);
        secondaryCateLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:26;");
        secondaryIDLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:26;");
        secondaryPriceLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:26;");
        secondaryJoinLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:26;");
        ownerIDLabel.setText(ownerID);
        categoryLabel.setText(category);
        priceLabel.setText("NT$ "+price);
        joinPeopleLabel.setText(joinPeople+"/10");
        this.manageVBox = manageVBox;

        anchorPane.setPadding(new Insets(10, 10, 10, 10));

        if(joinList.size()!=0) {
            listVBox.setPadding(new Insets(5, 5, 5, 5));
            listVBox.setPrefWidth(830);
            listVBox.setStyle("-fx-background-color: #323232; -fx-border-color: #323232;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);-fx-background: rgb(50,50,50);");
        }

        int cnt=0;
        if(ownerID.equals(GlobalVariable.userID))
        {
            makeButton("COMPLETE",postID + "chooseButton",postID);
            makeButton("DELETE",postID + "deleteButton",postID);

            for(String joinPeopleID:joinList){
                JFXCheckBox tmpCheckBox = makeCheckBox(joinPeopleID,postID);
                tmpCheckBox.setPadding(new Insets(5, 5, 5, 8));
                tmpCheckBox.setPrefWidth(20000);

                tmpCheckBox.setStyle("-fx-font-size: 25;-fx-border-color: #444444;-fx-border-style: solid none none none;");
                if(cnt++==0)
                    tmpCheckBox.setStyle("-fx-font-size: 25;");
                listVBox.getChildren().add(tmpCheckBox);
            }
        }
        //leaveBtn
        else{
            makeButton("LEAVE",postID + "chooseButton",postID);
            anchorPane.getChildren().remove(deleteBtn);

            for(String joinPeopleID:joinList){
                Label tmpLabel = new Label();
                tmpLabel.setText(joinPeopleID);
                tmpLabel.setPadding(new Insets(5, 5, 5, 15));
                tmpLabel.setPrefWidth(20000);

                tmpLabel.setStyle("-fx-font-size: 25;-fx-border-color: #444444;-fx-border-style: solid none none none;");
                if(cnt++==0)
                    tmpLabel.setStyle("-fx-font-size: 25;");
                listVBox.getChildren().add(tmpLabel);
            }
        }
    }

}
