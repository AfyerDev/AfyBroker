dependencies {
    implementation(project(":afybroker-core"))
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("org.yaml:snakeyaml:1.30")
    implementation("net.sf.trove4j:core:3.1.0")
    implementation("jline:jline:2.14.6")
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

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.shadowJar)
    }
}