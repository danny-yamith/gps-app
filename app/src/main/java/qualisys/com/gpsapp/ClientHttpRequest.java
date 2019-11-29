package qualisys.com.gpsapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Mario on 08/04/2015.
 */
public class ClientHttpRequest {

    public static int CONNECT_TIMEOUT = 4000;
    public static int READ_TIMEOUT = 8000;

    private final HttpURLConnection connection;
    private OutputStream os = null;

    public ClientHttpRequest(String url) throws IOException {
        this(new URL(url).openConnection());
    }


    public ClientHttpRequest(URLConnection conn) throws IOException {
        this.connection = (HttpURLConnection) conn;
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
    }

    protected void connect() throws IOException {
        if (os == null) {
            os = connection.getOutputStream();
        }
    }

    protected void write(char c) throws IOException {
        connect();
        os.write(c);
    }

    protected void write(String s) throws IOException {
        connect();
        if (s != null && (!s.isEmpty())) {
            os.write(s.getBytes("UTF-8"));
        }
    }

    protected void newline() throws IOException {
        connect();
        write("\r\n");
    }

    protected void writeln(String s) throws IOException {
        connect();
        write(s);
        newline();
    }

    private final static Random random = new Random();

    protected static String randomString() {
        return Long.toString(random.nextLong(), 36);
    }

    String boundary = "---------------------------" + randomString() + randomString() + randomString();

    private void boundary() throws IOException {
        write("--");
        write(boundary);
    }

    /**
     * @param name
     * @throws IOException
     */
    private void writeName(String name) throws IOException {
        newline();
        write("Content-Disposition: form-data; name=\"");
        write(name);
        write('"');
    }

    /**
     * adds a string parameter to the request
     *
     * @param name  parameter name
     * @param value parameter vlrTotal
     * @throws IOException
     */
    public void setParameter(String name, String value) throws IOException {
        boundary();
        writeName(name);
        newline();
        newline();
        writeln(value);
    }

    private void pipe(InputStream in, OutputStream out) throws IOException {
        copy(in, out);
    }

    /**
     * adds a file parameter to the request
     *
     * @param name     parameter name
     * @param filename the name of the file
     * @param is       input stream to read the contents of the file from
     * @throws IOException
     */
    public void setParameter(String name, String filename, InputStream is) throws IOException {
        this.setParameter(name, filename, is, 0);
    }

    public void setParameter(String name, String filename, InputStream is, long len) throws IOException {
        boundary();
        writeName(name);
        write("; filename=\"");
        write(filename);
        write('"');
        newline();
        write("Content-Type: ");
        String type = URLConnection.guessContentTypeFromName(filename);
        if (type == null) {
            type = "application/octet-stream";
        }
        writeln(type);
        newline();
        pipe(is, os);
        newline();
    }


    public InputStream post() throws IOException {
        boundary();
        writeln("--");
        os.close();
        try {
            String enc = connection.getHeaderField("Content-Encoding");
            if (enc != null && enc.equals("gzip")) {
                return new GZIPInputStream(connection.getInputStream());
            } else {
                return connection.getInputStream();
            }
        } catch (IOException ex) {
            Log.e("ClientHttpRequest", null, ex);
            if (connection.getResponseMessage() != null && !connection.getResponseMessage().isEmpty()) {
                throw new IOException(connection.getResponseMessage());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (connection.getErrorStream() != null) {
                copy(connection.getErrorStream(), baos);
                throw new IOException(new String(baos.toByteArray(), Charset.forName("UTF-8")));
            } else {
                throw ex;
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int count = 0;
        int n = 0;

        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static byte[] toBais(List<Object> data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos));
        for (int i = 0; i < data.size(); i++) {
            Object get = data.get(i);
            if (get instanceof String) {
                oos.writeUTF((String) get);
            } else if (get instanceof Integer) {
                oos.writeInt((Integer) get);
            } else if (get instanceof Boolean) {
                oos.writeBoolean((Boolean) get);
            } else {
                throw new RuntimeException("tipo no reconocido: " + get.getClass().getName());
            }
        }
        oos.close();
        baos.close();
        return baos.toByteArray();
    }

    public void setReadTimeout(int readTimeout) {
        connection.setReadTimeout(readTimeout);
    }

    public void setConnectTimeout(int connectTimeout) {
        connection.setConnectTimeout(connectTimeout);
    }
}
