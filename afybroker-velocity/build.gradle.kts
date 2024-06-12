plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    implementation(project(":afybroker-client")) {
        exclude(group = "io.netty", module = "netty-all")
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.shadowJar)
    }
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version
    )
    inputs.properties(props)
    filesMatching("velocity-plugin.json") {
        expand(props)
    }
}