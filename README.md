<h1 align="center">AfyBroker</h1>
<h5 align="center">高效率bukkit通信框架，基于sofabolt</h5>

## 💡这是什么

这是一个用于bukkit集群服务器的rpc跨服通信框架，用于实现在不同bukkit、bungee，甚至是mirai服务器上进行信息传递以及远程代码调用。

基于蚂蚁金融的高性能sofabolt框架，可承载每秒百万级别的通信请求。

支持多bungee集群架构，本项目专门为大型服务器设计，可承载万人级别。



## ⚡快速安装

假设你已经克隆了此项目，输入以下指令以构建项目。

```shell
gradlew build
```

将项目安装到本地maven仓库

```shell
gradlew publishMavenPublicationToMavenLocal
```

将`broker-server-bootstrap`模块下生成的jar包拖入到一个文件夹，创建一个shell脚本，输入以下指令并且保存运行。

```shell
java -jar afybroker-server-bootstrap-版本号.jar
```

将`broker-bukkit`，`broker-bungee`模块下生成的jar包分别放入到bukkit，bungee服务端的plugin目录下，并启动服务器，然后修改插件目录下AfyBroker目录里的config.yml文件，最后重启服务器。

在broker-server控制台内输入`list`指令即可查看连接到的broker-client。

## 📖功能开发

工作原理为，将bukkit，bungee等服务端作为broker-client，另外还有一个broker-server用于连接客户端来进行通信。

每个消息`Message`，皆为一个对象，这个对象必须实现了java的`Serializable`接口，消息的序列化基于hessian高性能框架，并非java原生的对象序列化机制，因此没有性能问题，

每个消息，在每个平台上（broker-client、broker-server），都有唯一的一个消息处理器`Processor`来处理这个消息。

rpc协议的具体实现过程，请参考[sofabolt](https://github.com/sofastack/sofa-bolt/blob/master/README.md)

可以参考运用此框架的跨服私聊功能演示项目来快速上手 https://github.com/Nipuru/MsgDemo