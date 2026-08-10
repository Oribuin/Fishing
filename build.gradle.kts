import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
}

group = "dev.oribuin"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    disableAutoTargetJvm()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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
    api("org.incendo:cloud-paper:2.0.0")
    api("org.spongepowered:configurate-yaml:4.2.0")
    api("com.zaxxer:HikariCP:4.0.3")
    api("dev.triumphteam:triumph-gui:3.1.13") {  // https://triumphteam.dev/docs/triumph-gui/
        exclude(group = "com.google.code.gson", module = "gson") // Remove GSON, Already included in spigot api
        exclude(group = "net.kyori", module = "*") // Remove kyori
    }

    // Additional Utilities
    api("net.objecthunter:exp4j:0.4.8")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")

    // Spigot
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
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
        this.foliaSupported = true
        this.softDepend = listOf("Vault", "HeadDatabase", "PlaceholderAPI", "PlayerPoints")
    }

    javadoc {
        this.options {
            this as StandardJavadocDocletOptions

            this.links("https://jd.papermc.io/paper/26.2/")
//            this.links("https://www.javadoc.io/doc/dev.triumphteam/triumph-gui/3.1.13/")

            // Exclude unnecessary classes from javadocs
            this.excludeDocFilesSubDir("command")
            this.excludeDocFilesSubDir("database")
            this.excludeDocFilesSubDir("gui")
            this.excludeDocFilesSubDir("hook")
            this.excludeDocFilesSubDir("listener")

            // encoding options
            this.addStringOption("encoding", "UTF-8")
            this.addStringOption("docencoding", "UTF-8")
            this.addStringOption("charset", "UTF-8")
            this.addStringOption("locale", "en_US")
        }
    }

    publishing {
        publications {
            create<MavenPublication>("shadow") {
                artifact(this@tasks["shadowJar"]) {
                    classifier = null
                }

                groupId = project.group as String
                artifactId = rootProject.name
                repositories {
                    maven {
                        val version = project.version as String
                        credentials {
                            username = project.property("mavenUser") as String?
                            password = project.property("mavenPassword") as String?
                        }

                        val releasesRepoUrl = "https://repo.rosewooddev.io/repository/public-releases/"
                        val snapshotsRepoUrl = "https://repo.rosewooddev.io/repository/public-snapshots/"
                        url = uri(if (version.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    }
                }
            }
        }
    }
    
    build {
//        this.dependsOn(javadoc)
        this.dependsOn(shadowJar)
    }
}