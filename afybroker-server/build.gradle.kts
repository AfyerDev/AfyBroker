plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}
dependencies {
    api(project(":afybroker-core"))
    api(libs.guava)
    api(libs.slf4j.api)
    api(libs.netty)
    api(libs.gson)
    api(libs.snakeyaml)
    api(libs.trove4j)
    api(libs.jline)
    api(libs.logback.classic)
}

java {
    withSourcesJar()
}