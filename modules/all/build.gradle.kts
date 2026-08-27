import com.smushytaco.lwjgl_gradle.Preset

import groovy.namespace.QName
import groovy.util.Node
import groovy.xml.XmlParser
//import org.kohsuke.github.GHReleaseBuilder
//import org.kohsuke.github.GitHub

import java.io.IOException
import java.net.URL
import java.nio.file.Files

plugins {
	alias(libs.plugins.loom)
	alias(libs.plugins.lwjgl)
	alias(libs.plugins.minotaur)
    java
	`maven-publish`
}

/*buildscript {
	repositories {
		mavenCentral()
	}

	dependencies {
		classpath("org.kohsuke:github-api:1.135")
	}
}*/

val modVersion: String = project.properties["mod_version"] as String
val modGroup: String = project.properties["mod_group"] as String
val modName: String = project.properties["mod_name"] as String

val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

base.archivesName = modName
group = modGroup
version = modVersion

loom {
	customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/${libs.versions.bta.get()}/manifest.json")
}

lwjgl {
	version = libs.versions.lwjgl
	implementation(Preset.MINIMAL_OPENGL)
}

dependencies {
    minecraft("::${libs.versions.bta.get()}")
	include(libs.commonsLang3)
	implementation(project(":catalyst-core"))
	include(project(":catalyst-core"))
	implementation(project(":catalyst-effects"))
	include(project(":catalyst-effects"))
	implementation(project(":catalyst-fluids"))
	include(project(":catalyst-fluids"))
	implementation(project(":catalyst-energy"))
	include(project(":catalyst-energy"))
	//implementation(project(":catalyst-multipart"))
	//include(project(":catalyst-multipart"))
	implementation(project(":catalyst-multiblocks"))
	include(project(":catalyst-multiblocks"))
	implementation(project(":catalyst-screens"))
	include(project(":catalyst-screens"))
}

tasks {
	processResources {
		val resourceMap = mapOf(
			"version" to modVersion,
			"loader" to libs.versions.loader.get(),
			"halplibe" to libs.versions.halplibe.get(),
			"java" to libs.versions.java.get(),
			"modmenu" to libs.versions.modMenu.get()
		)
		inputs.properties(resourceMap)
		filesMatching("fabric.mod.json") { expand(resourceMap) }
		filesMatching("**/*.mixins.json") { expand(resourceMap.filterKeys { it == "java" }) }
	}
}
// Removes LWJGL2 dependencies
configurations.configureEach { exclude(group = "org.lwjgl.lwjgl") }

java {
	toolchain {
		languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	sourceCompatibility = JavaVersion.toVersion(javaVersion.get())
	targetCompatibility = JavaVersion.toVersion(javaVersion.get())
	withSourcesJar()
}

tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.get().toString()
		targetCompatibility = javaVersion.get().toString()
		if (javaVersion.get() > 8) options.release = javaVersion
	}
}

val modrinthToken: Provider<String> = providers.gradleProperty("modrinthToken")
val githubToken: Provider<String> = providers.gradleProperty("githubToken")

if (modrinthToken.isPresent) {
	modrinth {
		token = modrinthToken
		projectId = "catalyst"
		versionName = "Catalyst ${modVersion}"
		versionNumber = modVersion
		versionType = "release"
		uploadFile.set(tasks.jar)
		additionalFiles = listOf(tasks.named("sourcesJar"))
		gameVersions.add("b1.7.3")
		loaders.add("bta-babric")
		changelog = Files.readString(project.projectDir.toPath().resolve("../../CHANGELOG.md"))
		dependencies { // A special DSL for creating dependencies
			required.version("halplibe", libs.versions.halplibe.get())
		}
	}
}

publishing {
	if(checkVersion(modGroup, modName, modVersion)){
		repositories {
			maven {
				name = "signalumMaven"
				url = uri("https://maven.thesignalumproject.net/releases")
				credentials(PasswordCredentials::class)
				authentication {
					create<BasicAuthentication>("basic")
				}
			}

			publications {
				create<MavenPublication>("maven") {
					groupId = modGroup
					artifactId = modName
					version = modVersion
					from(components["java"])
				}
			}
		}
	}
}

fun checkVersion(group: String, name: String, version: String): Boolean {
	return !(rootProject.property("check_versions") as String).toBoolean() || try {
		val xml = URL("https://maven.thesignalumproject.net/releases/$group/$name/maven-metadata.xml").readText()
		val metadata = XmlParser().parseText(xml)

		val versions = metadata.getAt(QName("versioning")).getAt("versions").getAt("version").map { (it as Node).text() }

		if (version in versions) {
			System.err.println("Version $version of $group.$name already exists!")
			false
		} else {
			System.out.println("Version $version of $group.$name ready to release!")
			true
		}
	} catch (e: IOException) {
		System.err.println("Failed to check version for $group.$name!")
		e.printStackTrace()
		true
	}
}

/*if(githubToken.isPresent){
	tasks.register("github") {
		description = "Publishes mod to GitHub"
		doLast {

			val projects = listOf(
				":catalyst-core",
				":catalyst-effects",
				":catalyst-fluids",
				":catalyst-energy",
				":catalyst-multiblocks",
				":catalyst-screens"
			)

			val github = GitHub.connectUsingOAuth(githubToken.get())
			val repository = github.getRepository("MartinSVK12/catalyst")

			val releaseBuilder = GHReleaseBuilder(repository, modVersion)
			releaseBuilder.name(modVersion)
			releaseBuilder.body(Files.readString(project.projectDir.toPath().resolve("../../CHANGELOG.md")))
			releaseBuilder.commitish("8.0")

			val release = releaseBuilder.create()
			release.uploadAsset(
				project.file(tasks.named("jar").get().outputs.files.singleFile),
				"application/java-archive"
			)
			release.uploadAsset(
				project.file(tasks.named("sourcesJar").get().outputs.files.singleFile),
				"application/java-archive"
			)
			if(findProject(":catalyst-all") != null){
				for (project in projects) {
					release.uploadAsset(
						project(project).file(project(project).tasks.named("jar").get().outputs.files.singleFile),
						"application/java-archive"
					)
				}
			}
		}
	}
}*/
