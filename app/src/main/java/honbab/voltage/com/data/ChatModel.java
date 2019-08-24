package honbab.voltage.com.data;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String, Object> users = new HashMap<>(); //채팅방의 유저들
//    public Map<String,Comment> comments = new HashMap<>();//채팅방의 대화내용
    public Map<String,Chats> chats = new HashMap<>();//채팅방의 대화내용


//    public static class Comment {
//
//        public String uid;
//        public String message;
//        public Object timestamp;
//        public Map<String,Object> readUsers = new HashMap<>();
//    }

//    public static class Users {
//        public String d
//    }

    public static class Chats {
        public String message;
//        public Map<String,Object> readUsers = new HashMap<>();
        public Object timestamp;
        public String uid;
    }
}