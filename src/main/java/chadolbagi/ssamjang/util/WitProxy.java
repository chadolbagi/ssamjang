package chadolbagi.ssamjang.util;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WitProxy {
    @Value("${wit-ai.app-id}")
    String appId;
    
    @Value("${wit-ai.client-token}")
    String clientToken;

    @Value("${wit-ai.server-token}")
    String serverToken;

    @Value("${wit-ai.version}")
    String version;

    private final String baseUrl = "https://api.wit.ai";

    private static final Logger log = LogManager.getLogger(HttpProxy.class);

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() throws Exception {
        httpClient = HttpClientUtil.getHttpClient(20 * 1000);
    }

    public String getUrl(Api api, Map<String, String> queryParams) {
        String url = baseUrl + api.getEndpoint() + "?v=" + version;
        for(String key: queryParams.keySet()) {
            url += ("&" + key + "=" + queryParams.get(key));
        }
        return url;
    }

    public String get(Api api, Map<String, String> queryParams) throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(api, queryParams));
        httpGet.setHeader("Authorization", "Bearer " + serverToken);
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("baseUrl", baseUrl), e);
            throw e;
        }
    }

    public String get(Api api) throws Exception {
        return get(api, Map.of());
    }

    public String post(Api api, String body, Map<String, String> additionalHeaders) throws Exception {
        // TODO: https://wit.ai/docs/http/20200513#post__speech_link 를 참고해서 additionalHeaders 에 올 것들 미리 정의할지?
        HttpPost httpPost = new HttpPost(getUrl(api, Map.of()));
        httpPost.setEntity(EntityBuilder.create().setBinary(body.getBytes()).build());
        httpPost.setHeader("Authorization", "Bearer " + serverToken);
        httpPost.setHeader("Content-type", "application/json");
        additionalHeaders.entrySet().forEach(entry -> httpPost.setHeader(entry.getKey(), entry.getValue()));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("baseUrl", baseUrl, "body", body), e);
            throw e;
        }
    }

    public String post(Api api, String body) throws Exception {
        return post(api, body, Map.of());
    }

    public String form(Api api, List<NameValuePair> params) throws Exception {
        HttpPost httpPost = new HttpPost(getUrl(api, Map.of()));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        httpPost.setHeader("Authorization", "Bearer " + serverToken);
        httpPost.setHeader("Content-type", "application/x-www-form-baseUrlencoded");

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("baseUrl", baseUrl, "params", params), e);
            throw e;
        }
    }

    @PreDestroy
    public void teardown() throws Exception {
        httpClient.close();
    }

    public enum Api {
        Message("message"), Speech("speech"), Intent("intents"), Entity("entities"), Trait("traits"), Utterance("utterances");

        String endpoint;
        Api(String endpoint) {
            this.endpoint = endpoint;
        }
        public String getEndpoint() {
            return this.endpoint;
        }

    }
}