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
import org.springframework.stereotype.Component;

@Component
public class HttpProxy {
    private static final Logger log = LogManager.getLogger(HttpProxy.class);

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() throws Exception {
        httpClient = HttpClientUtil.getHttpClient(20 * 1000);
    }

    public String get(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("url", url), e);
            throw e;
        }
    }

    public String post(String url, String body, Map<String, String> additionalHeaders) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(EntityBuilder.create().setBinary(body.getBytes()).build());
        httpPost.setHeader("Content-type", "application/json");
        additionalHeaders.entrySet().forEach(entry -> httpPost.setHeader(entry.getKey(), entry.getValue()));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("url", url, "body", body), e);
            throw e;
        }
    }

    public String post(String url, String body) throws Exception {
        return post(url, body, Map.of());
    }

    public String form(String url, List<NameValuePair> params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error(Map.of("url", url, "params", params), e);
            throw e;
        }
    }

    @PreDestroy
    public void teardown() throws Exception {
        httpClient.close();
    }
}
