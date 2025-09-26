plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api(libs.kryo)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.slf4j.api)
    compileOnly(libs.guava)
    compileOnly(libs.netty)
    compileOnly(libs.hessian) // 不依赖 hessian 仅供 sofabolt 编译通过
}

java {
    withSourcesJar()
}