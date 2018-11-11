package honbab.pumkit.com.widget;

import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileUploadRequest extends StringRequest {

    Context mContext;

    public ProfileUploadRequest(Context mContext, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

        this.mContext = mContext;

    }

    // 파일 업로드
    private Map<String, Uri> fileUploads = new HashMap<String, Uri>();

    // 키-밸류 업로드
    private Map<String, String> stringUploads = new HashMap<String, String>();


    public void addFileUpload(String param, Uri uri) {
        fileUploads.put(param, uri);
    }

    public void addStringUpload(String param, String content) {
        stringUploads.put(param, content);
    }

    String boundary = "XXXYYYZZZ";
    String lineEnd = "\r\n";

    @Override
    public byte[] getBody() throws AuthFailureError {
//            Log.d(TAG, "getBody works in MultlpartRequest");
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            for (String key : stringUploads.keySet()) {
                dos.writeBytes("--" + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                dos.writeBytes("Content-Type: text/plain; charset-UTF-8" + lineEnd);
                dos.writeBytes(lineEnd);

                dos.writeBytes(stringUploads.get(key));
                dos.writeBytes(lineEnd);
            }

            for (String key : fileUploads.keySet()) {
                dos.writeBytes("--" + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + key + "\"" + lineEnd);
                dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                dos.writeBytes(lineEnd);

                Uri uri = fileUploads.get(key);
                InputStream is = mContext.getContentResolver().openInputStream(uri);
                byte[] fileData = ByteStreams.toByteArray(is);
                dos.write(fileData);
                dos.writeBytes(lineEnd);
            }

            dos.writeBytes("--" + boundary + "--" + lineEnd);
            dos.flush();
            dos.close();

            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }
}