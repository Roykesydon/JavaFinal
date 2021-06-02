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
    public static String server = "https://java-waf-api.herokuapp.com/";
}