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

But the server developer did not go to work, emmmmm.....  

You have to build a mock server yourself and define the data you want.  

But now, you only need to do the following things to forget about this trouble.   

 **Step 1 Add maven repository dependency**  

`maven { url 'https://jitpack.io' }`

 **Step 2 Add dependency to your app "build.gradle"**  

`implementation "com.github.woilsy:android-mock:v1.0.0"`  

 **Step 3 Launcher the mock server before init Retrofit**  

`MockLauncher.start(Context context, MockOptions options, MockObj... objs)`

 **Step 4 Take MockOptions.BASEURL as baseUrl to Retrofit**  

 **OK!**  




 **Advanced:**   
1.If you want the http server to return custom data, you can either create a file in assets and write the data, and then pass it to MockOption.  
File 'mock.json' in assets  
```
[
  {
    "url":"request1",
    "data":["one","two","three"]
  }
] 
```
MockOptions.setDataSource("mock.json");   

2.If you want to customize the mock data, you can use the @Mock annotation to mark the field you need to mock, and then pass the data you want to pass.

```
@Mock("your custom data")
String name;
@Mock("100")
Integer age;
@Mock(type = Type.IMAGE)
String avatar;
```

