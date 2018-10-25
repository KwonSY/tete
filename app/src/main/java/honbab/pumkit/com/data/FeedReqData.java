package honbab.pumkit.com.data;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedReqData implements Serializable {

    private String feed_id;
    private String rest_id, rest_name, rest_img;
    private ArrayList<UserData> usersList;
    private ArrayList<CommentData> commentsList;

    public FeedReqData() {

    }

    public FeedReqData(String feed_id,
                       String rest_id, String rest_name, String rest_img,
                       ArrayList<UserData> usersList, ArrayList<CommentData> commentsList) {
        this.feed_id = feed_id;
        this.rest_id = rest_id;
        this.rest_name = rest_name;
        this.rest_img = rest_img;
        this.usersList = usersList;
        this.commentsList = commentsList;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getRest_img() {
        return rest_img;
    }

    public void setRest_img(String rest_img) {
        this.rest_img = rest_img;
    }

    public ArrayList<UserData> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<UserData> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<CommentData> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<CommentData> commentsList) {
        this.commentsList = commentsList;
    }
}