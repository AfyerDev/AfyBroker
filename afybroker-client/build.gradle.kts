plugins {
    `java-library`
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