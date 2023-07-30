<h1 align="center">AfyBroker</h1>
<h5 align="center">高效率bukkit通信框架，基于sofabolt</h5>

## 💡这是什么

这是一个用于bukkit集群服务器的rpc跨服通信框架，用于实现在不同 bukkit、bungee，甚至是 mirai 服务器上进行信息传递以及远程代码调用。

基于蚂蚁金融的高性能 sofabolt 框架，可承载每秒百万级别的通信请求。

支持 bungee 集群架构，本项目专门为大型服务器设计，可承载万人级别。



## ⚡快速安装

1、假设你已经克隆了此项目，输入以下指令以构建项目。

```shell
gradlew build
```

2、将项目安装到本地maven仓库

```shell
gradlew publishMavenPublicationToMavenLocal
```

3、将`broker-server-bootstrap`模块下生成的jar包拖入到一个文件夹，创建一个shell脚本，输入以下指令并且保存运行。

```shell
java -jar afybroker-server-bootstrap-版本号.jar
```

4、将 broker-bukkit，broker-bungee 模块下生成的 jar 包分别放入到 bukkit，bungee 服务端的插件 plugins 目录下，并启动服务器。

5、如果 broker-server 与 bukkit、bungee 服务器不在同一台设备上，则需修改 bukkit、bungee 插件目录下 AfyBroker 目录里的 config.yml 文件，将主机 host 改为 broker-server 的网络ip地址，并重启服务器。

5、修改 bukkit 插件目录下 AfyBroker 目录里的 config.yml 文件，将客户端名称 name 改为此 bukkit 服务器在 bungeecord 配置里的名称，最后重启 bukkit 服务器。

```yaml
broker:
  #broker 网关服务器地址
  host: localhost
  #broker 网关服务器端口
  port: 11200
  #客户端名称 必须和bungee内的此服务端名称保持一致，如spawn、lobby等
  name: 'bukkit'
  #客户端标签
  tags: []
```

## 📖功能开发

可以参考运用此框架的演示项目来快速上手 

- 跨服私聊：https://github.com/Nipuru/MsgDemo
- 跨服传送：https://github.com/Nipuru/TpaDemo

rpc协议的具体实现过程，请参考 [sofabolt](https://github.com/sofastack/sofa-bolt/blob/master/README.md)