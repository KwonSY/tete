package honbab.voltage.com.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatData {
    private String type;
    private String id_message;

    private String fromId;
    private String toId;
    private String text;
    public Long timestamp;

    private String fromUserName;
    private String toUserName;
    private String toUserImg;

    private String imageUrl;
    private int imageWidth;
    private int imageHeight;

    public ChatData() {

    }

    public ChatData(String type, String fromId, String toId, String text, HashMap<String, Object> timestamp) {
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
        this.text = text;
//        this.imageWidth = 0;
//        this.imageHeight = 0;
    }

    public ChatData(String type,
                    String fromId, String toId, String toUserName, String message,
                    String imageUrl, int imageWidth, int imageHeight, String toUserImg) {
//        fromId, toId, imageUrl, milliSeconds, imageUrl, imageWidth, imageHeight, toUserImg

        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
        this.toUserName = toUserName;
        this.text = message;
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.toUserImg = toUserImg;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fromId", fromId);
        result.put("toId", toId);
        result.put("message", "xxxxxxxxx");
//        result.put("time", body);

        return result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId_message() {
        return id_message;
    }

    public void setId_message(String id_message) {
        this.id_message = id_message;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public Long getTimestampLong() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getToUserImg() {
        return toUserImg;
    }

    public void setToUserImg(String toUserImg) {
        this.toUserImg = toUserImg;
    }
}