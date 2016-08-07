package fr.handipressante.app.server;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Nico on 24/05/2016.
 */
public class MultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;
    private final String mMimeType;
    private final byte[] mMultipartBody;

    public MultipartRequest(String url, Map<String, String> headers, String mimeType, byte[] multipartBody, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers;
        this.mMimeType = mimeType;
        this.mMultipartBody = multipartBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return mMimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mMultipartBody;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
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

    public static class Builder {
        private final String twoHyphens = "--";
        private final String lineEnd = "\r\n";
        private final String boundary = "apiclient-" + System.currentTimeMillis();
        private final String mimeType = "multipart/form-data;boundary=" + boundary;

        private ByteArrayOutputStream mBos;
        private DataOutputStream mDos;

        private String mUrl;

        public Builder() {
            mBos = new ByteArrayOutputStream();
            mDos = new DataOutputStream(mBos);
        }

        public MultipartRequest build(Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
            byte[] multipartBody = null;

            try {
                mDos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                multipartBody = mBos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MultipartRequest request = new MultipartRequest(mUrl, null, mimeType, multipartBody,
                    listener, errorListener);

            return request;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public void addTextPart(String parameterName, String parameterValue) throws IOException {
            mDos.writeBytes(twoHyphens + boundary + lineEnd);
            mDos.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
            mDos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            mDos.writeBytes(lineEnd);
            mDos.writeBytes(parameterValue + lineEnd);
        }

        public void addFilePart(String parameterName, byte[] fileData, String fileName) throws IOException {
            mDos.writeBytes(twoHyphens + boundary + lineEnd);
            mDos.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\""
                    + fileName + "\"" + lineEnd);
            mDos.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                mDos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            mDos.writeBytes(lineEnd);
        }
    }
}
