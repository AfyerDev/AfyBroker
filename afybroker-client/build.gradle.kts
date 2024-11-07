plugins {
    `java-library`
    `maven-publish`
    id("afybroker-publish")
}

dependencies {
    api(project(":afybroker-core"))
}

java {
    withSourcesJar()
}