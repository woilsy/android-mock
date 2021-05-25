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

But the server developer did not go to work, emmmmmmmmm......  

You have to build a mock server yourself and define the data you want.  

But now, you only need to do the following things to forget about this trouble.   

 **Step 1 Add maven repository dependency**  

`maven { url 'https://jitpack.io' }`

 **Step 2 Add dependency to your app "build.gradle"**  

[![](https://jitpack.io/v/com.woilsy/android-mock.svg)](https://jitpack.io/#com.woilsy/android-mock)  
`implementation "com.woilsy:android-mock:latest.version"`

 **Step 3 Launcher the mock server before init Retrofit**  

`MockLauncher.start(Context context, MockOptions options, MockObj... objs)`

 **Step 4 Take MockDefault.BASE_URL as baseUrl to Retrofit**  

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
MockOptions.setDataSource(AssetFileDataSource(context, "mock.json"));   

2.If you want to customize the mock data, you can use the @Mock annotation to mark the field you need to mock, and then pass the data you want to pass.

```
@Mock("your custom data")
String name;
@Mock("100")
Integer age;
@Mock(type = Type.IMAGE)
String avatar;
```

