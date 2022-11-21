plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {

    apply(plugin = "java")

    group = "net.afyer.afybroker"
    version = "1.2"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
    }

    dependencies {
        testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")

        compileOnly ("org.projectlombok:lombok:1.18.24")
        annotationProcessor ("org.projectlombok:lombok:1.18.24")

        testCompileOnly ("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor ("org.projectlombok:lombok:1.18.24")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
}

tasks.withType<Jar> {
    enabled = false
}





