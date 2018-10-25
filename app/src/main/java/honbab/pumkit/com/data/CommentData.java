package honbab.pumkit.com.data;

import java.io.Serializable;

public class CommentData implements Serializable {

    private String sid;
    private String user_id, user_name, img_url;
    private String comment;
    private String time;

    public CommentData() {

    }

    public CommentData(String sid, String user_id, String user_name, String img_url, String comment, String time) {
        this.sid = sid;
        this.user_id = user_id;
        this.user_name = user_name;
        this.img_url = img_url;
        this.comment = comment;
        this.time = time;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}