Sometimes,

As an android developer, you define some network requests in an interface, like this. ↓  

```
  @POST("/request1")
  Observable<List<String>> request1();

  @GET("/request2")
  Call<ResponseBody> request2();

  @GET("/XXX")
  Flow<A<B<C<D<List<E>>>>>> requestX();

```

But the server only provides interface documents, emmmmmmmmm......  

You have to build a mock server yourself and define the data you want.  

But now, you only need to do the following things to forget about this trouble.   

 **Step 1 Add maven repository dependency**  

`maven { url 'https://jitpack.io' }`

 **Step 2 Add dependency to your app "build.gradle"**  

[![](https://jitpack.io/v/com.woilsy/android-mock.svg)](https://jitpack.io/#com.woilsy/android-mock)  
`implementation "com.woilsy:android-mock:latest.version"`

 **Step 3 Initialize the mock server**  

`Mocker.init(Context context, MockOptions options)`   

 **Step 4 Add interceptor to okhttp client**

`OkHttpClient.Builder.addInterceptor(new MockInterceptor()));`

**Step 5 Use @MockObj to mark the interface that needs to be mocked, such as ApiService in the example, and you can also specify its MockStrategy**
```
@MockObj
public interface ApiService {
    ...
}
```
 **It's ready!**

 **Advanced:**   
1.If you want the http server to return custom data, you can either create a file in assets and write the data, and then pass it to MockOption.  
File 'mock.json' in assets  
```
[
  {
    "method":"GET",
    "path":"/request1",
    "data":["one","two","three"]
  }
] 
```
MockOptions.setDataSource(AssetFileDataSource(context, "mock.json"));   

2.If you want to customize the mock data, you can use the @MockXX annotation to mark the field you need to mock, and then pass the data you want to pass.

```java
public class Data{
    @Mock("SECONDS")
    public String timeUnit;
    @Mock("18")
    public int age;
    @Mock("{\"a\":\"1234\"}")
    public ChildA childA;
    @Mock("[]")
    public List<String> ls;
    @MockIntRange({1,2})
    public int type;   
}
```

For more information, please check the demo and source code.
