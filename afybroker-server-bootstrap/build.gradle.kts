plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":afybroker-server"))
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "net.afyer.afybroker.server.BootStrap",
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}