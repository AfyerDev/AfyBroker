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
    api(libs.jline.reader)
    api(libs.jline.terminal)
    runtimeOnly(libs.jline.terminal.jansi)
    api(libs.log4j.api)
    api(libs.log4j.core)
    api(libs.log4j.slf4j.impl)
    runtimeOnly(libs.jline.terminal.jansi)
}

java {
    withSourcesJar()
}
