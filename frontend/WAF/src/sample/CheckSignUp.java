package sample;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckSignUp
{
    //data field
    private static final String regexMail = "^(.+)@(.+)$";
    private static final String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$";
    private String userPassWord;
    private String userMail;
    private Pattern pattern;
    private Matcher matcher;

    CheckSignUp(String passWord,String mail){
        this.userPassWord = passWord;
        this.userMail = mail;
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
            //checkemail
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
}