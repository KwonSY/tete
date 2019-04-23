package honbab.voltage.com.widget;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import honbab.voltage.com.tete.Statics;

public class Encryption {

    private static String userPassword;

    /**
     * 패스워드 암호화
     * @param userPassword
     *            사용자 패스워드
     * @return 암호화 된 사용자 패스워드
     *         암호화 방식 : SHA-512
     */
    public static boolean encryption(String userPassword) {
        MessageDigest md;
        boolean isSuccess;
        String tempPassword = "";

        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(userPassword.getBytes());
            byte[] mb = md.digest();
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                tempPassword += s;
            }
            setPassword(tempPassword);
            isSuccess = true;
        } catch (NoSuchAlgorithmException e) {
            isSuccess = false;
            return isSuccess;
        }
        return isSuccess;
    }

    public static void setPassword(String temppassword) {
        userPassword = temppassword;
    }

    public static String getPassword() {
        return userPassword;
    }

    // SHA256
    public static String sha256(String msg)  throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());

        return Encryption.byteToHexString(md.digest());
    }

    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(byte b : data) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String voltAuth() {
        String auth = "";

        if (Integer.parseInt(Statics.my_id) > 0 && Statics.my_id.length() > 0) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            try {
                auth = Statics.my_id + "ooooo" + Encryption.sha256(firebaseUser.getUid().substring(5) + "madeBy@ksy");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return auth;
    }
}