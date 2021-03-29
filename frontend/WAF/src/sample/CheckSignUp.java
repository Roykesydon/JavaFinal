package sample;
import java.nio.charset.IllegalCharsetNameException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckSignUp
{
    //data field
    private static final String REGEXMAIL = "^(.+)@(.+)$";
    private static final char[] ILLEGALCHAR = {'\'', '<', '>','\\','`'};
    private static final String REGEXPASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$";
    private static final int LEN = 5;
    private String userPassWord;
    private String userMail;
    private String userID;
    private String userName;
    private Pattern pattern;
    private Matcher matcher;

    //constructor
    CheckSignUp(String passWord,String mail,String ID,String name){
        this.userPassWord = passWord;
        this.userMail = mail;
        this.userID = ID;
        this.userName = name;
    }

    //method
    public boolean checkName()
    {
        for(int i = 0;i<LEN;i++)
        {
            if(this.userName.indexOf(ILLEGALCHAR[i]) != -1)
            {
                System.out.println("Illegal char found!");
                return false;
            }
        }
        return true;
    }

    public boolean checkID()
    {
        for(int i = 0;i<LEN;i++)
        {
            if(this.userID.indexOf(ILLEGALCHAR[i]) != -1)
            {
                System.out.println("Illegal char found!");
                return false;
            }
        }
        return true;
    }

    public boolean checkPassWord()
    {
        try
        {
            for(int i = 0;i<LEN;i++)
            {
                if(this.userPassWord.indexOf(ILLEGALCHAR[i]) != -1)
                {
                    System.out.println("Illegal char found!");
                    return false;
                }
            }

            this.pattern = Pattern.compile(REGEXPASSWORD);
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
            for(int i = 0;i<LEN;i++)
            {
                if(this.userMail.indexOf(ILLEGALCHAR[i]) != -1)
                {
                    System.out.println("Illegal char found!");
                    return false;
                }
            }

            this.pattern = Pattern.compile(REGEXMAIL);
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