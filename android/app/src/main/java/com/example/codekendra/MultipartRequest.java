package com.example.codekendra;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class MultipartRequest extends Request<String> {
    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;
    private final String mBoundary;
    private static final String PROTOCOL_CHARSET = "utf-8";

    public MultipartRequest(String url, Map<String, String> headers,
                            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers;
        this.mBoundary = "apiclient-" + System.currentTimeMillis();
    }

    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(url, null, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + mBoundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Add string parameters
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(bos, entry.getValue(), entry.getKey());
                }
            }

            // Add byte data (files)
            Map<String, DataPart> byteData = getByteData();
            if (byteData != null && byteData.size() > 0) {
                for (Map.Entry<String, DataPart> entry : byteData.entrySet()) {
                    buildBytePart(bos, entry.getValue(), entry.getKey());
                }
            }

            // End boundary
            bos.write(("--" + mBoundary + "--\r\n").getBytes(PROTOCOL_CHARSET));

        } catch (IOException e) {
            throw new AuthFailureError("Failed to write multipart body", e);
        }
        return bos.toByteArray();
    }

    // These methods MUST be overridden by subclasses
    protected abstract Map<String, String> getParams() throws AuthFailureError;
    protected abstract Map<String, DataPart> getByteData() throws AuthFailureError;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    private void buildTextPart(ByteArrayOutputStream dataOutputStream, String parameterValue, String parameterName) throws IOException {
        dataOutputStream.write(("--" + mBoundary + "\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(("Content-Type: text/plain; charset=" + PROTOCOL_CHARSET + "\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(("\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write((parameterValue + "\r\n").getBytes(PROTOCOL_CHARSET));
    }

    private void buildBytePart(ByteArrayOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.write(("--" + mBoundary + "\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataFile.getFileName() + "\"\r\n").getBytes(PROTOCOL_CHARSET));
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.write(("Content-Type: " + dataFile.getType() + "\r\n").getBytes(PROTOCOL_CHARSET));
        }
        dataOutputStream.write(("Content-Transfer-Encoding: binary\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(("\r\n").getBytes(PROTOCOL_CHARSET));
        dataOutputStream.write(dataFile.getContent());
        dataOutputStream.write(("\r\n").getBytes(PROTOCOL_CHARSET));
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart() {
        }

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
        }

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}