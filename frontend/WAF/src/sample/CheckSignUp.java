package sample;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckSignUp
{
    //data field
    private static final String regexUserName = "[a-z0-9A-Z -]{5,50}";
    private static final String regexUserID = "[a-zA-Z0-9]{5,30}";
    private static final String regexMail = "^[A-Za-z0-9]+@+[A-Za-z0-9]+[.-]+[A-Za-z0-9]+$";
    private static final String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,30}$";
    private String userPassWord;
    private String userMail;
    private String userID;
    private String userName;
    private Pattern pattern;
    private Matcher matcher;

    CheckSignUp(String passWord,String mail,String id,String name){
        this.userPassWord = passWord;
        this.userMail = mail;
        this.userID = id;
        this.userName = name;
    }

    CheckSignUp(String passWord){
        this.userPassWord = passWord;
    }
    public boolean checkPassWord()
    {
        try
        {
            //checkPassWord
            this.pattern = Pattern.compile(regexPassword);
            this.matcher = this.pattern.matcher(this.userPassWord);
            if(this.matcher.matches())
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch (RuntimeException e){return false;}
    }

    public boolean checkMail()
    {
        try
        {
            //checkEmail
            this.pattern = Pattern.compile(regexMail);
            this.matcher = this.pattern.matcher(this.userMail);
            if(this.matcher.matches())
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch (RuntimeException e){return false;}
    }

    public boolean checkID()
    {
        try
        {
            //checkID
            this.pattern = Pattern.compile(regexUserID);
            this.matcher = this.pattern.matcher(this.userID);
            if(this.matcher.matches())
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch (RuntimeException e){return false;}
    }

    public boolean checkName()
    {
        try
        {
            //checkName
            this.pattern = Pattern.compile(regexUserName);
            this.matcher = this.pattern.matcher(this.userName);
            if(this.matcher.matches())
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch (RuntimeException e){return false;}
    }
}