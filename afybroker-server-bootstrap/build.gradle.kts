plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":afybroker-server"))
}

val mainClazz = "net.afyer.afybroker.server.BootStrap"

application {
    mainClass.set(mainClazz)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to mainClazz,
        )
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