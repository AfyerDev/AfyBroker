plugins {
    `java-library`
}

dependencies {
    api("org.jetbrains:annotations:22.0.0")
    api("com.alipay.sofa:bolt:1.6.7")
    api("com.caucho:hessian:4.0.66")
    api("ch.qos.logback:logback-classic:1.2.11")
    api("com.google.guava:guava:33.0.0-jre")
}

java {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}