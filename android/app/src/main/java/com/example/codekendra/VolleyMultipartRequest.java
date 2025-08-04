package com.example.codekendra;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;
    private final String mBoundary;

    // Constructor for direct parameter passing
    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener,
                                  Map<String, String> headers,
                                  Map<String, String> params,
                                  Map<String, DataPart> byteData) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers;
        this.mParams = params;
        this.mByteData = byteData;
        this.mBoundary = "apiclient-" + System.currentTimeMillis();
    }

    // Constructor for overriding getParams() and getByteData() methods
    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = new HashMap<>();
        this.mParams = null;
        this.mByteData = null;
        this.mBoundary = "apiclient-" + System.currentTimeMillis();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null) {
            return mParams;
        }
        return getCustomParams();
    }

    // Provide default implementation instead of abstract
    protected Map<String, String> getCustomParams() {
        return new HashMap<>();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (Exception e) {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // Build multipart form data
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            // Get parameters
            Map<String, String> params = mParams != null ? mParams : getCustomParams();

            // Add text parameters
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    dos.writeBytes(twoHyphens + mBoundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(entry.getValue());
                    dos.writeBytes(lineEnd);
                }
            }

            // Get byte data
            Map<String, DataPart> byteData = mByteData != null ? mByteData : getCustomByteData();

            // Add file data
            if (byteData != null && byteData.size() > 0) {
                for (Map.Entry<String, DataPart> entry : byteData.entrySet()) {
                    DataPart dataPart = entry.getValue();
                    dos.writeBytes(twoHyphens + mBoundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: " + dataPart.getType() + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(dataPart.getContent());
                    dos.writeBytes(lineEnd);
                }
            }

            // End of multipart form data
            dos.writeBytes(twoHyphens + mBoundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new AuthFailureError("Error creating multipart request", e);
        }
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + mBoundary;
    }

    // Provide default implementation instead of abstract
    protected Map<String, DataPart> getCustomByteData() {
        return new HashMap<>();
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(String name, byte[] data, String mimeType) {
            fileName = name;
            content = data;
            type = mimeType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}