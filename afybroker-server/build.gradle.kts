plugins {
    `java-library`
}
dependencies {
    api(project(":afybroker-core"))
    api("com.google.code.gson:gson:2.8.8")
    api("org.yaml:snakeyaml:1.30")
    api("net.sf.trove4j:core:3.1.0")
    api("jline:jline:2.14.6")
}

java {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}