plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {

    apply(plugin = "java")

    group = "net.afyer.afybroker"
    version = "2.8"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://hub.spigotmc.org/nexus/repository/public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.minebench.de/")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}



tasks.withType<Jar> {
    enabled = false
}





