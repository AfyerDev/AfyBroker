plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api("org.jetbrains:annotations:22.0.0")
    api("com.caucho:hessian:4.0.66")
    api("com.google.guava:guava:33.0.0-jre")
    api("org.slf4j:slf4j-api:1.7.21")
    api("io.netty:netty-all:4.1.42.Final")
}

java {
    withSourcesJar()
}