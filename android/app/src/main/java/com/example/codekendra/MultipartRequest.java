package com.example.codekendra;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final String boundary = "----CodeKendraBoundary" + System.currentTimeMillis();
    private final Response.Listener<String> mListener;
    private final Map<String, String> mParams;
    private final File imageFile;
    private final String caption;
    private final int userId;

    public MultipartRequest(String url,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener,
                            File imageFile,
                            String imageField,
                            String caption,
                            int userId) {
        super(Request.Method.POST, url, errorListener);
        this.mListener = listener;
        this.imageFile = imageFile;
        this.caption = caption;
        this.userId = userId;
        this.mParams = new HashMap<>();
        mParams.put("caption", caption);
        mParams.put("user_id", String.valueOf(userId));
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                bos.write(("--" + boundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                bos.write((entry.getValue() + "\r\n").getBytes());
            }

            if (imageFile != null && imageFile.exists()) {
                bos.write(("--" + boundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"post_img\"; filename=\"" + imageFile.getName() + "\"\r\n").getBytes());
                bos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());

                FileInputStream fis = new FileInputStream(imageFile);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                fis.close();
                bos.write("\r\n".getBytes());
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String resp = new String(response.data);
        return Response.success(resp, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
