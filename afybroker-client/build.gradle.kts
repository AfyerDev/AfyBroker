plugins {
    `java-library`
    `maven-publish`
}
dependencies {
    api(project(":afybroker-core"))
}

java {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}