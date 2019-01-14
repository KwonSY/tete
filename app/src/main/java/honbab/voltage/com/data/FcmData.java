package honbab.voltage.com.data;

public class FcmData {
    public String token;
    public String user_id;
    public String user_name;
    public String message;

    public FcmData(String token, String user_id, String user_name, String message) {
        this.token = token;
        this.user_id = user_id;
        this.user_name = user_name;
        this.message = message;
    }
}