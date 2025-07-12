plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api(project(":afybroker-core"))

    // 添加服务器模块的测试依赖
    testImplementation(project(":afybroker-server"))
}

java {
    withSourcesJar()
}