package sample.global;

import javafx.stage.Stage;
import sample.controller.PollingController;

public class GlobalVariable{
    public static String accessKey = "";
    public static String userID = "";
    public static boolean isAdmin = false;
    public static boolean userEnterFirstTime = true;
    public static Stage mainStage = null;
    public static String primaryColor = "#00E3E3";
    public static String secondaryColor = "#B15BFF";
    public static PollingController polling;
    //heroku website "https://java-waf-api.herokuapp.com/"
    //public static String server = "http://127.0.0.1:13261/";
    public static String server = "https://java-waf-api.herokuapp.com/";
}