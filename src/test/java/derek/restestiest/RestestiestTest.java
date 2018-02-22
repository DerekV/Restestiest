package derek.restestiest;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import derekv.restestiest.Restestiest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestestiestTest {

  MockWebServer server = new MockWebServer();

  @After
  public void closeServer() throws Exception {
    server.close();
  }

  @Test
  public void test_happy_path_with_json() throws Exception {
    String someJson = "{\n\"name\":\"Joanne\",\"favorite-number\": 3 \n}\n";

    server.enqueue(new MockResponse().setBody(someJson).setHeader("content-type", "application/json").setResponseCode(200));
    server.start();

    Restestiest.fromBaseUrl(server.url("").toString())
        .whenGet()
        .withPath("/endpoint/7/")
        .then()
        .expectStatus(200)
        .expectJsonResponseBody("\n\n{   \"favorite-number\": 3 \n,  \"name\":\"Joanne\"    }\n\n")
        .executeTest();


    RecordedRequest recordedRequest = server.takeRequest(400, TimeUnit.MILLISECONDS);
    assertEquals("/endpoint/7/",recordedRequest.getPath());
  }

  @Test
  public void test_json_missing_field() throws Exception {
    String someJson = "{\n\"name\":\"Joanne\"\n}\n";

    server.enqueue(new MockResponse().setBody(someJson).setHeader("content-type", "application/json").setResponseCode(200));
    server.start();


    Throwable exception = assertThrows(AssertionError.class, () ->
        Restestiest.fromBaseUrl(server.url("").toString())
            .whenGet()
            .withPath("/endpoint/7/")
            .then()
            .expectStatus(200)
            .expectJsonResponseBody("\n\n{   \"favorite-number\": 3 \n,  \"name\":\"Joanne\"    }\n\n")
            .executeTest());

    assertEquals("\nExpected: favorite-number\n     but none found\n", exception.getMessage());
  }

  @Test
  public void test_json_wrong_value() throws Exception {
    String someJson = "{\"name\":\"Joanne\",\"favorite-number\": 1000000}";

    server.enqueue(new MockResponse().setBody(someJson).setHeader("content-type", "application/json").setResponseCode(200));
    server.start();

    Throwable exception = assertThrows(AssertionError.class, () ->
        Restestiest.fromBaseUrl(server.url("").toString())
            .whenGet()
            .withPath("/endpoint/7/")
            .then()
            .expectStatus(200)
            .expectJsonResponseBody("\n\n{   \"favorite-number\": 3 \n,  \"name\":\"Joanne\"    }\n\n")
            .executeTest());

    assertEquals("favorite-number\nExpected: 3\n     got: 1000000\n", exception.getMessage());

    RecordedRequest recordedRequest = server.takeRequest(400, TimeUnit.MILLISECONDS);
    assertEquals("/endpoint/7/",recordedRequest.getPath());
  }

}
