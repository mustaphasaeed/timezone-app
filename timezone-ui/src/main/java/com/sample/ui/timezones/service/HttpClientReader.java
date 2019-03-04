package com.sample.ui.timezones.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.sample.ui.timezones.service.model.HttpApiResponse;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class HttpClientReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientReader.class.getName());

    private final String serverUrl;

    @Autowired
    public HttpClientReader(@Value("${backend.server.host}") String serverHost,
            @Value("${backend.server.port}") String serverPort) {
        this.serverUrl = String.format("http://%s:%s/", serverHost, serverPort);
    }

    public HttpApiResponse invokeApi(String url, Map<String, String> urlParameters, String username, String password)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpClient httpclient = HttpClientBuilder.create().build();

        URIBuilder uriBuilder = new URIBuilder(serverUrl + url);
        if (urlParameters != null) {
            for (Entry<String, String> entry : urlParameters.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        URI uri = uriBuilder.build();

        LOGGER.info(String.format("Querying URL [%s]", uri.toString()));
        HttpPost postRequest = new HttpPost(uri);

        if (!StringUtils.isEmpty(username)) {
            String encodedAuth = Base64.encodeBase64String(
                    new StringBuilder().append(username).append(":").append(password).toString().getBytes());
            String authHeader = AuthSchemes.BASIC + " " + encodedAuth;
            postRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        }
        HttpResponse httpResponse = httpclient.execute(postRequest);

        String results = IOUtils.toString(httpResponse.getEntity().getContent());
        LOGGER.info(String.format("Received results [%s]", results));
        return new HttpApiResponse(httpResponse.getStatusLine().getStatusCode(), results);
    }
}
