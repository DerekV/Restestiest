package derekv.restestiest;

import org.skyscreamer.jsonassert.JSONAssert;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CaseBuilder {
  private static final MediaType JSON = MediaType.parse("application/json");
  final private String baseUrl;
  final private Request.Builder rqBuilder = new Request.Builder();
  final private HttpUrl.Builder urlBuilder;
  final private List<Function<HttpUrl.Builder, HttpUrl.Builder>> urlCustomizers = new ArrayList<>(1);
  final private List<Function<Request.Builder, Request.Builder>> reqestCustomizers = new ArrayList<>(1);
  private String method;
  private RequestBody body;

  CaseBuilder(String baseUrl) {
    this.baseUrl = baseUrl;
    this.urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
  }

  public CaseBuilder withMethod(String method) {
    this.method = method;
    return this;
  }

  public CaseBuilder withEndpoint(String endpoint) {
    urlBuilder.addPathSegment(endpoint);
    return this;
  }

  public CaseBuilder withCustomizedUrl(Function<HttpUrl.Builder, HttpUrl.Builder> urlCustomizer) {
    urlCustomizers.add(urlCustomizer);
    return this;
  }

  public CaseBuilder withCustomizedRequest(Function<Request.Builder, Request.Builder> rqCustomizer) {
    reqestCustomizers.add(rqCustomizer);
    return this;
  }

  // TODO with query params, with headers, etc ...

  public CaseBuilder withRequestBody(MediaType mediaType, byte[] bytes) {
    this.body = RequestBody.create(mediaType, bytes);
    return this;
  }

  public CaseBuilder withRequestBody(MediaType mediaType, String body) {
    this.body = RequestBody.create(mediaType, body);
    return this;
  }

  public CaseBuilder withJsonRequestBody(String json) {
    this.body = RequestBody.create(JSON, json);
    return this;
  }

  public CaseBuilder withBodyFromResource(String resourcePath) {
    throw new NotImplementedException();  // TODO !
  }


  public CaseValidator then() {
    return new CaseValidator();
  }


  public class CaseValidator {
    private int expectedStatus = 200;
    private String expectedJson = null;

    private CaseValidator() {
    }

    public CaseValidator expectStatus(int expectedStatus) {
      this.expectedStatus = expectedStatus;
      return this;
    }

    public CaseValidator expectJsonResponseBody(String expectedJson) {
      this.expectedJson = expectedJson;
      return this;
    }

    public CaseValidator expectJsonResonseBodyFromResource() {
      throw new NotImplementedException();  // TODO !
    }


    // TODO expect headers ... etc ... closures for assertions on response


    public void executeTest() throws Exception {
      urlCustomizers.forEach((f) -> f.apply(urlBuilder));
      HttpUrl url = urlBuilder.build();

      rqBuilder.url(url).method(method, body);
      reqestCustomizers.forEach((f) -> f.apply(rqBuilder));
      Request request = rqBuilder.build();


      // TODO should keep this somewhere global
      // TODO should enable customization
      OkHttpClient client = new OkHttpClient.Builder().build();

      final Response response = client.newCall(request).execute();

      assertThat(response.code(), equalTo(expectedStatus));

      if (expectedJson != null) {
        assertThat(response.body(), notNullValue());
        assertThat(response.body().contentType(), equalTo(JSON));
        JSONAssert.assertEquals(expectedJson, response.body().string(), true);
      }

    }
  }
}
