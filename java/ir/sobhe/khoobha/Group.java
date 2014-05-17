package ir.sobhe.khoobha;

/**
 * Created by hadi on 14/5/17 AD.
 */
public class Group {
    public long id;
    public String assistantEmail;
    public String assisrantPassword;

    public Group(long groupId, String email, String password){
        id = groupId;
        assistantEmail = email;
        assisrantPassword = password;
    }

}
