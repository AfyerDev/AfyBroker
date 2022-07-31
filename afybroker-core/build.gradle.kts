plugins {
    `java-library`
}
dependencies {
    api("com.alipay.sofa:bolt:1.6.5")
    api("com.caucho:hessian:4.0.66")
    api("ch.qos.logback:logback-classic:1.2.11")
    api("com.google.guava:guava:31.0.1-jre")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.shadowJar)
    }
}