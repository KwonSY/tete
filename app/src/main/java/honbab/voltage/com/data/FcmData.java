package honbab.voltage.com.data;

public class FcmData {
    public String token;
    public String user_name;
    public String message;

    public FcmData(String token, String user_name, String message) {
        this.token = token;
        this.user_name = user_name;
        this.message = message;
    }
}