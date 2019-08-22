package server;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://www.baeldung.com/httpclient-post-http-request
 */
public class PostRequestsIT extends BaseIT {


    @Test
    public void whenSendPostRequestUsingHttpClient_thenCorrect() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void whenSendPostRequestWithAuthorizationUsingHttpClient_thenCorrect() throws IOException, AuthenticationException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setEntity(new StringEntity("test post"));
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("John", "pass");
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void whenPostJsonUsingHttpClient_thenCorrect()
            throws IOException, InterruptedException {
        Thread.sleep(10000);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        String json = "{\"id\":1,\"name\":\"John\"}";
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void whenSendMultipartRequestUsingHttpClient_thenCorrect() throws IOException, URISyntaxException, InterruptedException {
        Thread.sleep(10000);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("username", "John");
        builder.addTextBody("password", "pass");
        File file = new File(getClass().getClassLoader().getResource("test.txt").toURI());
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, "file.ext");

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }
}
