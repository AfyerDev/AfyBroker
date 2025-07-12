<div align="center">
    <h1>AfyBroker</h1>
    <h5>High-performance Bukkit communication framework based on SofaBolt</h5>
    <span>English | <a href="./README.md">中文</a></span>
</div>

## 💡What is this

- This is an RPC framework for Bukkit cluster servers.
- Used to implement message passing and remote code invocation between different Bukkit, BungeeCord, Velocity, etc
- Aims to simplify cross-server communication development process with low learning curve and easy-to-use methods.
- Based on high-performance SofaBolt framework, using lock-free asynchronous event-driven design, stress tested to handle millions of communication requests per second.
- Supports multiple communication models including synchronous, asynchronous, and callback, with request timeout handling and automatic disconnection/reconnection.
- High-performance serialization framework with near-native performance and nanosecond-level response time.
- Supports Bungee cluster architecture, specially designed for large servers capable of handling tens of thousands of players.
- Flexible addition of custom server types to meet various communication business needs

## ⚡Quick Installation

1. Assuming you have cloned this project, enter the following command to build the project.

```shell
gradlew build
```

2. Install the project to your local Maven repository.

```shell
gradlew publishMavenPublicationToMavenLocal
```

3. Drag the generated jar file from the `broker-server-bootstrap` module into a folder, create a shell script, enter the following command and save and run.

```shell
java -jar afybroker-server-bootstrap-LASTVERSION.jar
```

4. Place the generated jar files from the broker-bukkit and broker-bungee modules into the plugins directory of your Bukkit and Bungee servers respectively, then start the servers.

5. If the broker-server is not on the same device as the Bukkit and Bungee servers, you need to modify the config.yml file in the AfyBroker directory under the plugins directory of Bukkit and Bungee, change the host to the network IP address of the broker-server, and restart the servers.

```yaml
broker:
  # broker server address
  host: localhost
  # broker server port
  port: 11200
  # client name, should be unique for each client, recommended to keep consistent with the server name in bungee, such as spawn1, lobby1, etc.
  name: 'bukkit-%unique_id%'
```

## 📖Development

You can refer to these demo projects using this framework to quickly get started

- private messaging: https://github.com/Nipuru/MsgDemo  
- teleportation: https://github.com/Nipuru/TpaDemo

For details on RPC firework, please refer to [sofabolt](https://github.com/sofastack/sofa-bolt/blob/master/README.md)

## 🙏Acknowledgments

Special thanks to the following projects and developers for providing valuable inspiration and technical support for this framework:

**Design Inspiration**
- [IoGame](https://github.com/iohao/ioGame) - A lock-free asynchronous, event-driven Java network game framework, the main design inspiration for this project

**Technical Contributors**
- [CarmJos](https://github.com/CarmJos) - Core design ideas for RPC and Service functionality
- [Ling556](https://github.com/Ling556) - Docker environment deployment solution, optimized the project's CI/CD workflow