/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.network;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Jonny
 */
@Component
public class Network
{
    @Value("${market.network.connection.timeout}")
    private int connectTimeout;

    @Value("${market.network.read.timeout}")
    private int readTimeout;

    public String httpRequest(String httpUrl, String method, boolean cache) throws IOException
    {
        final StringBuilder builder = new StringBuilder();

        try(InputStream stream = read(httpUrl, method, cache)) {
            try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = buffReader.readLine()) != null) {
                    builder.append(line);
                }
            }
        }
        return builder.toString();
    }
    
    public InputStream read(String httpUrl, String method, boolean cache) throws IOException
    {

        HttpURLConnection conn = null;
        try
        {

            URL base, next;
            String location;

            while (true) {
                // inputing the keywords to google search engine
                URL url = new URL(httpUrl);

//                if(cache) {
//                    //Proxy instance, proxy ip = 10.0.0.1 with port 8080
//                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("cache", 3128));
//                    // makking connection to the internet
//                    conn = (HttpURLConnection) url.openConnection(proxy);
//                }
//                else {
                    conn = (HttpURLConnection) url.openConnection();
//                }

                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
                conn.setRequestMethod(method);
                conn.setRequestProperty("User-Agent", "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 ( .NET CLR 3.5.30729)");
                conn.setRequestProperty("Accept-Encoding", "gzip");

                switch (conn.getResponseCode()) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        location = conn.getHeaderField("Location");
                        base = new URL(httpUrl);
                        next = new URL(base, location);  // Deal with relative URLs
                        httpUrl = next.toExternalForm();
                        continue;
                }

                break;
            }

            if (!conn.getContentType().startsWith("text/") || conn.getContentType().equals("text/xml")) {
                throw new IOException(String.format("Content of story is not Text. Returned content type is [%s]", conn.getContentType()));
            }

            if ("gzip".equals(conn.getContentEncoding())) {
                return new GZIPInputStream(conn.getInputStream());
            }

            // getting the input stream of page html into bufferedreader
            return conn.getInputStream();
        }
        catch (Exception exception)
        {            
            throw new IOException(exception);
        }
        finally {
            if (conn != null && conn.getErrorStream() != null) {
                conn.getErrorStream().close();
            }
        }
    }
}
