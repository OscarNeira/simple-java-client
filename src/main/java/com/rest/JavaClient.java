package com.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mockserver.client.MockServerClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JavaClient {

    private static String spectrumAuth = "";
    private static String randomNumbers = RandomStringUtils.randomNumeric(5);

    //MockServer Ingress
    private static String mockServerIngressBase = "example.mockserver.com";
    // Minikube IP test
//    private static String mockServerIngressBase = "192.168.64.50:30282";
    private static String mockServerIngressEndpoint = "http://" + mockServerIngressBase + "/api/1/space/spectrum-aa-1/objects/jesus-test-" + randomNumbers + "%2Ftest_createSingleSmallCall%2Fload-test-1/payload";

    public static void main(String[] args) {

//        System.out.println("Setup Mock Server");
//        System.out.println("mockServerIngressBase::" + mockServerIngressBase);
//        System.out.println("Create Expectation Delay 1 Sec");
//        createDelayMockServer();
//        System.out.println("Create Expectation Post simulation");
//        createPostMockServer();
//        System.out.println("Mock Server Ready");

        System.out.println("START http call Small Encoded file");
        System.out.println("mockServerIngressEndpoint::" + mockServerIngressEndpoint);
        testMockServerPayloadSmallEncodedFile();
        System.out.println("END http call Small Encoded file");
    }

    private static void testMockServerPayloadSmallEncodedFile() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(mockServerIngressEndpoint);

        httppost.addHeader("x-spectrum-access-token", spectrumAuth);
        httppost.addHeader("Content-Type", "application/octet-stream");
        httppost.addHeader("x-spectrum-meta-fs2_headers_test-name", "[\"test_createSingleSmallCall\"]");
        httppost.addHeader("x-spectrum-meta-fs2_metadata_dataDefinitionId", "Payload");
        httppost.addHeader("x-spectrum-meta-wasZippedFirst", "false");
        httppost.addHeader("x-spectrum-meta-fs2_metadata_createdBy", "FS2");
        httppost.addHeader("x-spectrum-meta-fs2_headers_test-header", "[\"test-header-value\"]");
        httppost.addHeader("source", "G2_SB_DEV_INT");
        httppost.addHeader("ttl", "50000");
        httppost.addHeader("x-spectrum-meta-fs2_metadata_ttl", "50000");

        java.util.Date dateTest = new java.util.Date();
        long time = dateTest.getTime();

        httppost.addHeader("x-spectrum-meta-fs2_metadata_createdOn", dateTest.toString());
        httppost.addHeader("timestamp", Long.toString(time));

        httppost.addHeader("Connection", "Keep-Alive");
        httppost.addHeader("Accept-Encoding", "gzip,deflate");

        try {
            File file = new File("small_file_encoded.txt");
            FileEntity entity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
            entity.setChunked(true);

            System.out.println("InputStreamEntity DATA getContent: " + entity.getContent());
            System.out.println("InputStreamEntity DATA getContentType: " + entity.getContentType());
            System.out.println("InputStreamEntity DATA getContentEncoding: " + entity.getContentEncoding());
            System.out.println("InputStreamEntity DATA isStreaming: " + entity.isStreaming());
            httppost.setEntity(entity);

            System.out.println("Executing request: " + httppost.getRequestLine());

            Header[] testH = httppost.getAllHeaders();

            for (Header strTemp : testH) {

                System.out.println("Executing request Hearder Name " + strTemp.getName());
                System.out.println("Executing request Hearder Value " + strTemp.getValue());
            }


            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));

            } finally {
                response.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDelayMockServer() {

        new MockServerClient(mockServerIngressBase, 80)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/delay/1")

                )
                .respond(
                        response()
                                .withDelay(TimeUnit.SECONDS, 1)
                                .withBody("OK")
                );
    }

    private static void createPostMockServer() {
        new MockServerClient(mockServerIngressBase, 80)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/1/space/.*")

                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("OK")
                );

    }
}