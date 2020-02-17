package sendgrid;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerSimulationIngress extends Mockito {

  private CloseableHttpClient httpClient;
  private CloseableHttpResponse response;
  private HttpEntity entity;
  private StatusLine statusline;
  private String spectrumAuth;
  private String randomNumbers;

  private String mockServerIngressBase;
  private String mockServerIngressEndpoint;

  private String mockServerBaseUrl;
  private String mockServerNodePort;

  @Before
  public void setUp() throws Exception {
    this.httpClient = mock(CloseableHttpClient.class);
    this.response = mock(CloseableHttpResponse.class);
    this.entity = mock(HttpEntity.class);
    this.statusline = mock(StatusLine.class);
    this.spectrumAuth = "";

    this.randomNumbers = RandomStringUtils.randomNumeric(5);

    //MockServer Ingress
//    this.mockServerIngressBase= "http://mockserver.jesus-microk8s.com";
    this.mockServerIngressBase= "mockserver.jesus-microk8s.com";
    this.mockServerIngressEndpoint= "http://"+mockServerIngressBase+"/api/1/space/spectrum-aa-1/objects/jesus-test-"+randomNumbers+"%2Ftest_createSingleSmallCall%2Fload-test-1/payload";
    // Base IP
    this.mockServerBaseUrl="http://10.143.8.72:30282";
    //NodePort IP
    this.mockServerNodePort= mockServerBaseUrl+"/api/1/space/spectrum-aa-1/objects/jesus-test-"+randomNumbers+"%2Ftest_createSingleSmallCall%2Fload-test-1/payload";
  }

  @Test
  public void CreateSimpleExpectation() {
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


  @Test
  public void CreateSimpleExpectationPost() {
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

  @Test
  public void testMockServerPayloadSmallEncodedFile() {
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

    java.util.Date dateTest=new java.util.Date();
    long time = dateTest.getTime();

    httppost.addHeader("x-spectrum-meta-fs2_metadata_createdOn", dateTest.toString());
    httppost.addHeader("timestamp", Long.toString(time));

    httppost.addHeader("Connection", "Keep-Alive");
    httppost.addHeader("Accept-Encoding", "gzip,deflate");

    try {
      File file = new File(getClass().getClassLoader().getResource("small_file_encoded.txt").toURI());
      FileEntity entity = new FileEntity(file,  ContentType.APPLICATION_OCTET_STREAM);
      entity.setChunked(true);

      System.out.println("InputStreamEntity DATA getContent: " + entity.getContent());
      System.out.println("InputStreamEntity DATA getContentType: " + entity.getContentType());
      System.out.println("InputStreamEntity DATA getContentEncoding: " + entity.getContentEncoding());
      System.out.println("InputStreamEntity DATA isStreaming: " + entity.isStreaming());
      httppost.setEntity(entity);

      System.out.println("Executing request: " + httppost.getRequestLine());

      Header[] testH = httppost.getAllHeaders();

      for (Header strTemp : testH){

        System.out.println("Executing request Hearder Name "  +  strTemp.getName());
        System.out.println("Executing request Hearder Value "  +  strTemp.getValue());
      }


      CloseableHttpResponse response = httpclient.execute(httppost);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));

      }finally {
        response.close();
      }

      Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testMockServerPayloadPayloadReactiveManifesto() {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(mockServerIngressEndpoint);

    httppost.addHeader("x-spectrum-access-token", spectrumAuth);
    httppost.addHeader("Content-Type", "application/octet-stream");
    httppost.addHeader("x-spectrum-meta-fs2_headers_test-name", "[\"test_ReactiveManifestoEncoded\"]");
    httppost.addHeader("x-spectrum-meta-fs2_metadata_dataDefinitionId", "Payload");
    httppost.addHeader("x-spectrum-meta-wasZippedFirst", "false");
    httppost.addHeader("x-spectrum-meta-fs2_headers_test-header", "[\"test-header-value\"]");
    httppost.addHeader("source", "G2_SB_DEV_INT");
    httppost.addHeader("ttl", "50000");
    httppost.addHeader("x-spectrum-meta-fs2_metadata_ttl", "50000");

    java.util.Date dateTest=new java.util.Date();
    long time = dateTest.getTime();

    httppost.addHeader("x-spectrum-meta-fs2_metadata_createdOn", dateTest.toString());
    httppost.addHeader("timestamp", Long.toString(time));
    httppost.addHeader("Connection", "Keep-Alive");
    httppost.addHeader("Accept-Encoding", "gzip,deflate");

    try {
      File file = new File(getClass().getClassLoader().getResource("reactive-manifesto-encoded.txt").toURI());
      FileEntity entity = new FileEntity(file,  ContentType.APPLICATION_OCTET_STREAM);
      entity.setChunked(true);

      System.out.println("InputStreamEntity DATA getContent: " + entity.getContent());
      System.out.println("InputStreamEntity DATA getContentType: " + entity.getContentType());
      System.out.println("InputStreamEntity DATA getContentEncoding: " + entity.getContentEncoding());
      System.out.println("InputStreamEntity DATA isStreaming: " + entity.isStreaming());
      httppost.setEntity(entity);

      System.out.println("Executing request: " + httppost.getRequestLine());

      Header[] testH = httppost.getAllHeaders();

      for (Header strTemp : testH){

        System.out.println("Executing request Hearder Name "  +  strTemp.getName());
        System.out.println("Executing request Hearder Value "  +  strTemp.getValue());
      }


      CloseableHttpResponse response = httpclient.execute(httppost);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));

      }finally {
        response.close();
      }

      Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void testMinikubeMockServerPayloadSmallEncodedFile() {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    this.mockServerIngressBase= "example.mockserver.com";
    this.mockServerIngressEndpoint= "http://"+mockServerIngressBase+"/api/1/space/spectrum-aa-1/objects/jesus-test-"+randomNumbers+"%2Ftest_createSingleSmallCall%2Fload-test-1/payload";

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

    java.util.Date dateTest=new java.util.Date();
    long time = dateTest.getTime();

    httppost.addHeader("x-spectrum-meta-fs2_metadata_createdOn", dateTest.toString());
    httppost.addHeader("timestamp", Long.toString(time));

    httppost.addHeader("Connection", "Keep-Alive");
    httppost.addHeader("Accept-Encoding", "gzip,deflate");

    try {
      File file = new File(getClass().getClassLoader().getResource("small_file_encoded.txt").toURI());
      FileEntity entity = new FileEntity(file,  ContentType.APPLICATION_OCTET_STREAM);
      entity.setChunked(true);

      System.out.println("InputStreamEntity DATA getContent: " + entity.getContent());
      System.out.println("InputStreamEntity DATA getContentType: " + entity.getContentType());
      System.out.println("InputStreamEntity DATA getContentEncoding: " + entity.getContentEncoding());
      System.out.println("InputStreamEntity DATA isStreaming: " + entity.isStreaming());
      httppost.setEntity(entity);

      System.out.println("Executing request: " + httppost.getRequestLine());

      Header[] testH = httppost.getAllHeaders();

      for (Header strTemp : testH){

        System.out.println("Executing request Hearder Name "  +  strTemp.getName());
        System.out.println("Executing request Hearder Value "  +  strTemp.getValue());
      }


      CloseableHttpResponse response = httpclient.execute(httppost);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));

      }finally {
        response.close();
      }

      Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
