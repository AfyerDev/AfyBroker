plugins {
    `java-library`
}

dependencies {
    api("com.alipay.sofa:bolt:1.6.5")
    api("com.caucho:hessian:4.0.66")
    api("ch.qos.logback:logback-classic:1.2.11")
    api("com.google.guava:guava:31.0.1-jre")
}

java {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}