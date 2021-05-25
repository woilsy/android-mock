# RetrofitMock 

[English](https://github.com/woilsy/android-mock/blob/master/README_EN.md)

#### 介绍
  不管是每一次的开发迭代，还是新建的项目，我们总会依赖后端接口，有时候后端给了数据结构，但开发进度却遥遥无期，有时仅仅是一个很简单的获取一个Boolean或者Int这样的简单数据也需要后端返回，这样的开发效率是很慢的。而手动去创建一些mock数据又费时费力，关键是接口好了以后还得重新按照网络请求的形式去获取。如果能想要什么就返回什么，仅仅需要几行代码，那肯定很不错。

 **思考：** 

Q：如何返回mock数据？  
A：Android的网络请求目前以Retrofit为主流，基本上都是REST风格的接口形式存在，但它本身还是一个网络请求，所以得按照网络请求的方式返回数据，否则正式接入后就无法做到一键切换。所以需要在本地自建一个HTTP服务器，[nanohttpd](https://github.com/NanoHttpd/nanohttpd)正好符合这个需求，通过集成，可以创建一个本地服务器，并且还可以获取到客户端发起的请求数据，也就可以自行根据一些策略来返回想要的数据了。

Q：如何获取想要的数据？  
A：对于Retrofit形式的请求，一般都是在函数的返回值中以Observable\<xx\>/Call\<xxx\>/Flow\<xx\>等形式存在，所以只要解析此返回值的第一个参数，就能获取到想要的返回对象，这就是数据来源，但也不排除Call\<ResponseBody\>这种形式或者上述没包含的形式的返回。  

Q：支持哪些方法？  
A：目前支持GET、POST、PUT、DELETE。

Q：什么叫静态url？  
A：静态url是以@GET("url")这种能够直接在注解中获取到的url，能直接获取的url。而@Get Call\<ResponseBody\> test(@Url String url)这种在运行时才能获取到具体请求地址，被称为动态url，所以这种没办法直接拿到它的值，除非可以监听函数执行，并能拿到参数（AOP是可以实现的，去监听Retrofit的Invoke过程，函数调用时再进行数据导入，但代价是还需要接入插件到Project中）。

Q：Call\<ResponseBody\>返回如何处理？  
A：ResponseBody由于其本身是无法被静态解析的，能静态解析的都是可序列化的Bean类(List、Map、class)，所以对于外部，可以通过assets、文件、List\<MockData\>的形式，将其在配置阶段导入，之后在解析到这个url对应的Method时，会优先判断是否已导入，以导入优先，不会再去解析返回对象。

#### 软件架构
/annotations 存放了库的注解  
/constants 存放的常量  
/data 存放数据相关  
/entity 存放实体类  
/generate 存在跟数据生成有关的东西  
/options 配置文件  
/parse 解析器  
/server 本地Http服务器所在  
/service android service  
/strategy 策略相关，跟解析挂钩的  
/test 测试代码  
/type mock数据的类型  
MockLauncher：启动类，负责初始化参数配置，开启android mock service，开启本地service，对传入的class进行静态解析。

#### 安装教程
加入maven仓库依赖  

`maven { url 'https://jitpack.io' }`

导入aar  

[![](https://jitpack.io/v/com.woilsy/android-mock.svg)](https://jitpack.io/#com.woilsy/android-mock)  
`implementation "com.github.woilsy:android-mock:latest.version"`

#### 使用说明

 **第一步**  

在Retrofit创建之前，调用    

`MockLauncher.start(Context context, MockOptions options, MockObj... objs)`  

 **参数说明**   
Context：为了启动服务和解析assets中的文件。  
MockOptions：进行mock相关的一些配置：开启日志、设置mock数据返回规则、设置备用地址、设置gson处理对象（在mock Date.class的时候，如果DateFormat不一致，会导致解析失败）。  
MockObj：待mock的对象，包含Class和一个MockStrategy策略，Class就是网络请求用的定义的接口，而MockStrategy则是决定是默认进行解析还是默认不解析的mock策略。被排除和不被包含的Method，将会访问去备用的原始地址重定向同步返回请求结果。

**第二步**  
将MockDefault.BASE_URL作为BaseUrl传递给Retrofit

 **其他**   
1.如果需要自定义mock数据，可以通过MockOptions.setDataSource()传入，返回值为ResponseBody时，只有导入了数据才会有返回值。  
2.@Mock注解可以指定字段具体的mock数据，以及类型，可以为基本类型，也可以为Json数据类型。

#### 参与贡献

@leo 
