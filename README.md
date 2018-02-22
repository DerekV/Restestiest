
# RESTestiest

The intent is to remove some boilerplate and tedium around driving integration tests against json http services, using
a fluent API.  Built on top of OkHTTP and JSONAssert, it is only intended to help with simple use cases.

## Example

```java
public class MyAppIntegrationTest {

  @Test
  public void test() {
    Restestiest.fromBaseUrl("localhost:8080")
        .whenGet()
        .withPath("/person/7/")
        .then()
        .expectStatus(200)
        .expectJsonResponseBody("{\"favorite-number\":3 ,\"name\":\"Joanne\"}")
        .executeTest();
  }
}
```

## STATUS : Not Ready for Use!
