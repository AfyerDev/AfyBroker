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

tasks.assemble {
    dependsOn(tasks.shadowJar)
}