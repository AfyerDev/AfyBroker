<div align="center">
    <h1>AfyBroker</h1>
    <h5>高性能 Minecraft 服务器通信框架，基于 sofabolt</h5>
    <span><a href="./README.en-US.md">English</a> | 中文</span>
</div>

## 💡这是什么

- 这是一个适用于 Minecraft 集群服务器的 RPC 跨服通信框架
- 用于实现在不同 Bukkit、BungeeCord、Velocity、甚至是 Mirai 服务器上进行信息传递以及远程代码调用。
- 旨在降低跨服通信业务的开发流程，学习成本低，使用方法简单。
- 基于蚂蚁金融的高性能 sofa-bolt 框架，使用无锁异步化的事件驱动型设计，经过压力测试，可承载每秒百万级别的通信请求。
- 支持同步、异步、回调多种通信模型，请求超时处理，自动断连与重连。
- 高性能序列化框架，近原生的性能，纳秒级响应速度。
- 支持 Bungee 集群架构，本项目专门为大型服务器设计，可承载万人级别。
- 可灵活添加自定义的服务器类型，满足各种通信业务需求。



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

```yaml
broker:
  #broker 网关服务器地址
  host: localhost
  #broker 网关服务器端口
  port: 11200
  #客户端名称 每个客户端应该唯一 建议和bungee内的此服务端名称保持一致，如spawn1、lobby1等
  name: 'bukkit-%unique_id%'
```

## 📖功能开发

可以参考运用此框架的演示项目来快速上手 

- 跨服私聊：https://github.com/Nipuru/MsgDemo
- 跨服传送：https://github.com/Nipuru/TpaDemo

rpc协议的具体实现过程，请参考 [sofabolt](https://github.com/sofastack/sofa-bolt/blob/master/README.md)

## 🙏致谢

特别感谢以下项目和开发者为本框架的设计提供了宝贵的灵感和技术支持：

**设计灵感来源**
- [IoGame](https://github.com/iohao/ioGame) - 无锁异步化、事件驱动的Java网络游戏框架，本项目的主要设计灵感来源

**技术贡献者**
- [CarmJos](https://github.com/CarmJos) - service 功能的核心设计思路
- [Ling556](https://github.com/Ling556) - docker 环境部署方案，优化了项目的 CI/CD 流程