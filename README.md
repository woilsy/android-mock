[English](https://github.com/woilsy/android-mock/blob/master/README_EN.md)

#### 介绍

每一次的开发迭代客户端总会依赖后端接口，有时候后端给了接口文档，但开发进度却遥遥无期，有时仅仅是一个很简单的获取一个Boolean或者Int这样的简单数据也需要后端返回，这样的开发效率是很慢的。而手动去创建一些mock数据又费时费力，关键是接口好了以后还得重新按照网络请求的形式去获取，本框架就是为了解决这个问题。

**思考：**

Q：如何返回mock数据？  
A：Android的网络请求目前以Retrofit为主流，基本上都是REST风格的接口形式存在，它本身是一个网络请求，所以得按照网络请求的方式返回数据，否则正式接入后就无法做到一键切换。那么我们可以在本地自建一个HTTP服务器，[AndroidAsync](https://github.com/koush/AndroidAsync)正好符合这个需求，通过集成，可以创建一个本地服务器，并且还可以获取到客户端发起的请求数据，也就可以自行根据一些策略来返回想要的数据了。

Q：如何获取想要的数据？  
A：对于Retrofit形式的请求，一般都是在函数的返回值中以Observable\<xx\>/Call\<xxx\>/Flow\<xx\>等形式存在，所以只要解析此返回值的第一个参数，就能获取到想要的返回对象，这就是数据来源，但也不排除Call\<ResponseBody\>这种形式或者上述没包含的形式的返回。

Q：支持哪些方法？  
A：目前支持GET、POST、PUT、DELETE。

Q：静态url？动态url？  
A：静态url是以@GET("url")这种能够直接在注解中获取到的url，能直接获取的url。而@Get Call\<ResponseBody\> test(@Url String url)
这种在运行时才能获取到具体请求地址的动态url，暂时没办法直接拿到它的值，除非可以监听函数执行，并能拿到参数（AOP是可以实现的，去监听Retrofit的Invoke过程，函数调用时再进行数据导入，但代价是还需要接入插件到Project中）。

Q：Call\<ResponseBody\>返回如何处理？是否支持协程？  
A：ResponseBody由于其本身是无法被静态解析的，能静态解析的都是可序列化的Bean类(List、Map、class)
，所以对于外部，可以通过assets、文件、List\<MockData\>的形式，将其在配置阶段导入，之后在解析到这个url对应的Method时，会优先判断是否已导入，以导入优先，不会再去解析返回对象。
支持协程函数。

#### 使用方法

加入maven仓库

`maven { url 'https://jitpack.io' }`

导入依赖  
[![](https://jitpack.io/v/com.woilsy/android-mock.svg)](https://jitpack.io/#com.woilsy/android-mock)  
`implementation "com.woilsy:android-mock:latest.version"`

### 简单使用

**第一步，在Retrofit创建之前调用**

`Mocker.init(Context context, MockOptions options, MockObj... objs)`  
或者  
`Mocker.init(Context context, MockOptions options, MockGroup... groups)`   

**参数说明**   
Context：为了启动服务和解析assets中的文件。  
MockOptions：进行mock相关的一些配置：开启日志、设置mock数据返回规则、设置gson处理对象（在mockDate.class的时候，如果DateFormat不一致，会导致解析失败）。  
MockObj：待mock的对象，包含Class和一个MockStrategy策略，Class就是网络请求用的定义的接口，而MockStrategy则是决定是默认进行解析还是默认不解析的mock策略。被排除和不被包含的Method，将会访问去原始地址同步返回请求结果。

**第二步，将MockInterceptor添加到拦截器中**  
OkHttpClient.Builder.addInterceptor(new MockInterceptor()));  

**到此接入完成~**

### 详细说明  
使用方法：  
1，初始化。  
初始化通过MockOptions，设置debug状态，setDebug，可以查看日志。  
添加mock规则addRule，包含如DictionaryRule、BaseTypeGenerator，可根据自己需求继承或者拓展。  
设置mockList数据最大元素数量，setMockListCount，对于列表类型会随机生成0-最大元素数量的数据。  
设置mock数据来源，setDataSource，优先级高于规则，比如将请求地址和返回数据写到assets目录文件中。  
设置动态访问，setDynamicAccess，每次是否生成新的数据，如果为false，那么每次同一个地址会返回同样的数据。  

2，策略。  
通过MockGroup或者MockObj，传入解析策略和具体mock的接口类对象，表示传入的接口类对象会被自动解析并mock，  
可以通过策略（如MockStrategy.EXCLUDE）和策略注解（@MockExclude）配置。  

3，使用。  
通过策略将被mock的接口类对象传入，根据策略在请求函数上加入@MockInclude或者@MockExclude，示例：      

@MockExclude  
@POST(“/live/liveRoom/pk/initiate”)  
fun invitePk(@Body pkCreateInfo: PkCreateInfo): Observable<BaseRsp<String>>  
这样访问网络时，会自动根据mock规则、mock数据来源等配置返回想要的数据。  

4，自定义Mock数据  
在Bean类中，还可以添加@Mock、@MockIntRange等注解，传入需要mock的数据，如以下  
@Mock(“S”)  
val timeUnit = “SECONDS”  
@Mock(“18”)  
val age: Int? = null  
@Mock(“{\”a\”:\”1234\”}”)  
public ChildA childA;  
@Mock(“[]”)  
public List<String> ls;  
@MockIntRange({1,2})  
public int type;

@Mock优先级高于默认值，默认值高于自动规则。  
@Mock注解可以指定字段具体的mock数据，以及类型，可以为基本类型，也可以为Json数据类型。

补充：  
1，点击通知栏，可以切换使用本地地址和服务器地址，且持续生效。  
2，更多内容请查看demo和源码。

#### 参与贡献

leo@woilsy.com 
