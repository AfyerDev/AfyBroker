plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api(libs.hessian)
    api(libs.prometheus.simpleclient)
    api(libs.prometheus.httpserver)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.slf4j.api)
    compileOnly(libs.guava)
    compileOnly(libs.netty)
}

java {
    withSourcesJar()
}
