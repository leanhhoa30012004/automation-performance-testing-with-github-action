package com.hoale.automation.performance.worker;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * HTTP Worker for Performance Testing
 * Executes HTTP requests and measures performance
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:34:45 UTC
 */
public class HttpWorker implements Callable<Void> {
    private static final Logger logger = LoggerFactory.getLogger(HttpWorker.class);

    private final PerformanceConfig config;
    private final TestResult testResult;
    private final CloseableHttpClient httpClient;

    public HttpWorker(PerformanceConfig config, TestResult testResult) {
        this.config = config;
        this.testResult = testResult;

        // Configure HTTP client with timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(config.getConnectionTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(config.getResponseTimeout()))
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public Void call() throws Exception {
        String threadName = Thread.currentThread().getName();
        logger.debug("Worker thread {} starting execution", threadName);

        try {
            for (int i = 0; i < config.getRequestsPerThread(); i++) {
                executeRequest();

                // Small delay to prevent overwhelming the server
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.warn("Worker thread {} interrupted", threadName);
            Thread.currentThread().interrupt();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.warn("Error closing HTTP client in thread {}", threadName, e);
            }
        }

        logger.debug("Worker thread {} completed execution", threadName);
        return null;
    }

    private void executeRequest() {
        long startTime = System.currentTimeMillis();
        boolean success = false;

        try {
            HttpUriRequest request = createRequest();

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                success = (statusCode >= 200 && statusCode < 300);

                if (!success) {
                    logger.debug("Request failed with status code: {}", statusCode);
                }

                // Consume response entity to complete the request
                if (response.getEntity() != null) {
                    response.getEntity().getContent().close();
                }
            }

        } catch (Exception e) {
            logger.debug("Request failed with exception: {}", e.getMessage());
            success = false;
        } finally {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            testResult.recordRequest(success, responseTime);
        }
    }

    private HttpUriRequest createRequest() {
        String method = config.getHttpMethod().toUpperCase();
        String url = config.getTargetUrl();

        return switch (method) {
            case "POST" -> {
                HttpPost post = new HttpPost(url);
                String jsonData = String.format(
                        "{\"test\":\"data\",\"developer\":\"leanhhoa30012004\",\"timestamp\":\"%s\",\"requestType\":\"performance-test\"}",
                        java.time.LocalDateTime.now().toString()
                );
                // Sửa cú pháp StringEntity cho Apache HttpClient 5
                post.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
                post.setHeader("Content-Type", "application/json");
                post.setHeader("User-Agent", "Hoale-Performance-Test/1.0");
                yield post;
            }
            case "PUT" -> {
                HttpPut put = new HttpPut(url);
                String jsonData = String.format(
                        "{\"test\":\"data\",\"developer\":\"leanhhoa30012004\",\"timestamp\":\"%s\",\"requestType\":\"performance-test\"}",
                        java.time.LocalDateTime.now().toString()
                );
                put.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
                put.setHeader("Content-Type", "application/json");
                put.setHeader("User-Agent", "Hoale-Performance-Test/1.0");
                yield put;
            }
            case "DELETE" -> {
                HttpDelete delete = new HttpDelete(url);
                delete.setHeader("User-Agent", "Hoale-Performance-Test/1.0");
                yield delete;
            }
            default -> {
                HttpGet get = new HttpGet(url);
                get.setHeader("User-Agent", "Hoale-Performance-Test/1.0");
                get.setHeader("Accept", "application/json");
                yield get;
            }
        };
    }
}