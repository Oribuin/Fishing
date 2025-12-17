import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

group = "dev.oribuin"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    disableAutoTargetJvm()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.oribuin.dev/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://nexus.neetgames.com/repository/maven-snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    // Commands, Configs & Database
    api("org.incendo:cloud-core:2.0.0")
    api("org.incendo:cloud-annotations:2.0.0")
    api("org.incendo:cloud-paper:2.0.0-beta.10")
    api("org.spongepowered:configurate-yaml:4.2.0")
    api("com.zaxxer:HikariCP:4.0.3")
    api("dev.triumphteam:triumph-gui:3.1.11") {  // https://mf.mattstudios.me/triumph-gui/introduction
        exclude(group = "com.google.code.gson", module = "gson") // Remove GSON, Already included in spigot api
        exclude(group = "net.kyori", module = "*") // Remove kyori
    }

    // Additional Utilities
    api("net.objecthunter:exp4j:0.4.8")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")

    // Spigot
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")

    // External Plugins
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("org.black_ixx:playerpoints:3.2.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "*")
    }
}

tasks {
    val commitHash = let {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
        val output = ByteArrayOutputStream()
        process.inputStream.copyTo(output)

        output.toString().trim()
    }
    
    project.version = commitHash
    
    compileJava {
        this.options.compilerArgs.add("-parameters")
        this.options.isFork = true
        this.options.encoding = "UTF-8"
    }

    shadowJar {
        // add commit hash to the jar name
        this.archiveClassifier.set("")
//        this.archiveVersion.set("$version-[$commitHash]")

        this.relocate("com.jeff_media.morepersistentdatatypes", "${project.group}.fishing.libs.pdt")
        this.relocate("net.objecthunter.exp4j", "${project.group}.fishing.libs.exp4j")
        this.relocate("dev.triumphteam.gui", "${project.group}.fishing.libs.triumphgui")
        this.relocate("io.leangen.geantyref", "${project.group}.fishing.libs.geantyref")
        this.relocate("org.incendo", "${project.group}.fishing.libs.incendo")
        this.relocate("org.spongepowered", "${project.group}.fishing.libs.spongepowered")
        this.relocate("com.zaxxer", "${project.group}.fishing.libs.hikari")
        this.relocate("org.slf4j", "${project.group}.fishing.libs.slf4j")
        this.minimize()
    }
    
    bukkit {
        this.main = "dev.oribuin.fishing.FishingPlugin"
        this.version = project.version as String?
        this.author = "Oribuin"
        this.description = "hello"
        this.apiVersion = "1.21"
        this.softDepend = listOf("Vault")
    }
    
    javadoc {
        this.options {
            this as StandardJavadocDocletOptions

            this.links("https://jd.papermc.io/paper/1.21.4/")
            this.links("https://www.javadoc.io/doc/dev.triumphteam/triumph-gui/3.1.10/")

            // Exclude unnecessary classes from javadocs
            this.excludeDocFilesSubDir("command")
            this.excludeDocFilesSubDir("database")
            this.excludeDocFilesSubDir("listener")
            this.excludeDocFilesSubDir("gui")
            this.excludeDocFilesSubDir("hook")

            // encoding options
            this.addStringOption("encoding", "UTF-8")
            this.addStringOption("docencoding", "UTF-8")
            this.addStringOption("charset", "UTF-8")
            this.addStringOption("locale", "en_US")
        }
    }

//    publishing {
//        publications {
//            create("shadow", MavenPublication::class) {
//                project.shadow.component(this)
//                this.artifactId = "fishing"
//                this.pom.name.set("fishing")
//            }
//        }
//
//        repositories {
//            val version = project.version as String
//            val mavenUser = project.properties["mavenUser"] as String?
//            val mavenPassword = project.properties["mavenPassword"] as String?
//
//            if (mavenUser != null && mavenPassword != null) {
//                maven {
//                    credentials {
//                        username = mavenUser
//                        password = mavenPassword
//                    }
//
//                    val releasesRepoUrl = "https://repo.rosewooddev.io/repository/public-releases/"
//                    val snapshotsRepoUrl = "https://repo.rosewooddev.io/repository/public-snapshots/"
//                    url = uri(if (version.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
//                }
//            }
//        }
//    }

    build {
//        this.dependsOn(javadoc)
        this.dependsOn(shadowJar)
    }
}