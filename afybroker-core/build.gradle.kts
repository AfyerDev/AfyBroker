plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api(libs.hessian)
    api(libs.prometheus.core)
    api(libs.prometheus.httpserver)
    api(libs.prometheus.jvm)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.slf4j.api)
    compileOnly(libs.guava)
    compileOnly(libs.netty)
}

java {
    withSourcesJar()
}
