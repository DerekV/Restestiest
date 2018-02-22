package derekv.restestiest;

public class Restestiest {

  private final String baseUrl;

  public Restestiest(String baseUrl) {
    this.baseUrl=baseUrl;
  }

  public static Restestiest fromBaseUrl(String baseUrl) {
    return new Restestiest(baseUrl);
  }

  public CaseBuilder whenGet() {
    return when("GET");
  }

  public CaseBuilder whenPost() {
    return when("POST");
  }

  public CaseBuilder whenPut() {
    return when("PUT");
  }

  public CaseBuilder whenDelete() {
    return when("DELETE");
  }

  public CaseBuilder whenHead() {
    return when("HEAD");
  }

  public CaseBuilder when(String method) {
    return new CaseBuilder(baseUrl).withMethod(method);
  }


}
