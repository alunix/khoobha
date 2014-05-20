package ir.sobhe.khoobha;

/**
 * Created by hadi on 14/5/17 AD.
 */
public class Group {
    public long id;
    public String assistantEmail;
    public String assisrantPassword;
    public String groupTitle;

    public Group(long groupId, String title, String email, String password){
        id = groupId;
        assistantEmail = email;
        assisrantPassword = password;
        groupTitle = title;
    }

}
