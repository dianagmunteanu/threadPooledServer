package server;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetRequestsIT extends BaseIT{

    @Test
    public void whenSendGetRequestUsingHttpClient_thenCorrect() throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        URI uri = new URIBuilder(httpGet.getURI()).addParameters(params).build();
        httpGet.setURI(uri);

        CloseableHttpResponse response = client.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void whenSendGetRequestWithAuthorizationUsingHttpClient_thenCorrect() throws IOException, AuthenticationException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("John", "pass");
        httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));

        CloseableHttpResponse response = client.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void whenSendGetRequestKeepAlive() throws IOException, AuthenticationException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("John", "pass");
        httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));

        CloseableHttpResponse response = client.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());

        CloseableHttpResponse response2 = client.execute(httpGet);
        assertEquals(200, response2.getStatusLine().getStatusCode());
        client.close();
    }

}
