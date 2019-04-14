package honbab.voltage.com.data;

import java.io.Serializable;

public class UserData implements Serializable {

    private String user_id;
    private String user_name;
    private String phone;
    private String email;
    private String age;
    private String gender;
    private String img_url;
    private String comment;
    private String token;
    //req
    private String status;
    private String status_name;
    private String password;

    public UserData() {

    }

    //req
    public UserData(String user_id, String user_name,
                    String age, String gender, String token, String img_url, String status, String status_name) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.age = age;
        this.gender = gender;
        this.token = token;
        this.img_url = img_url;
        this.status = status;
        this.status_name = status_name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}