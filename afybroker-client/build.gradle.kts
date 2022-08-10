plugins {
    `java-library`
}
dependencies {
    api(project(":afybroker-core"))
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}