package org.ybuka.je.thread;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.nio.charset.Charset;

// Test Http Client in separate threads
public class SimpleHttpClientTest {

    public static class HTTPTask extends Thread {

        CloseableHttpClient httpClient;
        HttpGet httpGet;
        String result;
        long minExecutionTime;

        public HTTPTask(CloseableHttpClient httpClient, HttpGet httpGet) {
            this(httpClient, httpGet, -1);
        }

        public HTTPTask(CloseableHttpClient httpClient, HttpGet httpGet, long minExecutionTime) {
            this.httpClient = httpClient;
            this.httpGet = httpGet;
            this.minExecutionTime = minExecutionTime;
        }

        @Override
        public void run() {
            result = "Start execution";
            long startTime = System.currentTimeMillis();
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String requestResult = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
                response.close();
                long httpTime = System.currentTimeMillis() - startTime;
                System.out.println("Wait time - " + (minExecutionTime - httpTime));
                if (httpTime < minExecutionTime) {
                    while (minExecutionTime > System.currentTimeMillis() - startTime) {
                        System.out.println("sleep");
                        sleep(100);
                    }
                }
                result = "Http request completed. Response size " + requestResult.length()
                        + ", time " + (System.currentTimeMillis() - startTime) + " ms";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    CloseableHttpClient httpClient;


    public static void main(String[] args) {
        SimpleHttpClientTest tests = new SimpleHttpClientTest();
        tests.testSimpleRequest();
        tests.testSimpleRequestTimeOut();
    }


    public SimpleHttpClientTest() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.httpClient = builder.build();
    }

    public void testSimpleRequest() {
        System.out.println(" method:testSimpleRequest");
        try {
            HttpGet get = new HttpGet("https://sap.com");
            HTTPTask http = new HTTPTask(this.httpClient, get);
            System.out.println("Before start");
            http.run();
            System.out.println("Before join");
            http.join();
            System.out.println("After join");
            System.out.println("Completed with result:\n" + http.result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSimpleRequestTimeOut() {
        System.out.println("method:testSimpleRequestTimeOut");
        try {
            HttpGet get = new HttpGet("https://onliner.by");
            HTTPTask http = new HTTPTask(this.httpClient, get, 15000);
            System.out.println("Before start");
            http.run();
            System.out.println("Before join");
            http.join(1000);
            System.out.println("Completed with result:\n" + http.result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
