package honbab.pumkit.com.data;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedReqData implements Serializable {

    private String feed_id, status, feed_time;
    private String host_id, host_name, host_img;
    private String rest_id, rest_name, rest_img;
    private ArrayList<UserData> usersList;
    private ArrayList<CommentData> commentsList;

    public FeedReqData() {

    }

    public FeedReqData(String feed_id, String status, String feed_time,
                       String host_id, String host_name, String host_img,
                       String rest_id, String rest_name, String rest_img,
                       ArrayList<UserData> usersList) {
        this.feed_id = feed_id;
        this.status = status;
        this.feed_time = feed_time;

        this.host_id = host_id;
        this.host_name = host_name;
        this.host_img = host_img;

        this.rest_id = rest_id;
        this.rest_name = rest_name;
        this.rest_img = rest_img;

        this.usersList = usersList;
    }

    public FeedReqData(String feed_id, String status, String feed_time,
                       String rest_id, String rest_name, String rest_img,
                       ArrayList<UserData> usersList, ArrayList<CommentData> commentsList) {
        this.feed_id = feed_id;
        this.status = status;
        this.feed_time = feed_time;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(String feed_time) {
        this.feed_time = feed_time;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getHost_img() {
        return host_img;
    }

    public void setHost_img(String host_img) {
        this.host_img = host_img;
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