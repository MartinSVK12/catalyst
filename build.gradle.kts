plugins {
	java
}

val lib = extensions.getByType<VersionCatalogsExtension>().named("libs")
val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

if(project == rootProject){
	tasks {
		named<UpdateDaemonJvm>("updateDaemonJvm") {
			languageVersion = libs.versions.gradleJava.map { JavaLanguageVersion.of(it.toInt()) }
			vendor = JvmVendorSpec.ADOPTIUM
		}
	}
}

subprojects {
	apply(plugin = "java")

	val licenseFile = run {
		val rootLicense = layout.projectDirectory.file("LICENSE")
		val parentLicense = layout.projectDirectory.file("../../LICENSE")
		when {
			rootLicense.asFile.exists() -> {
				logger.lifecycle("Using LICENSE from project root: {}", rootLicense.asFile)
				rootLicense
			}
			parentLicense.asFile.exists() -> {
				logger.lifecycle("Using LICENSE from parent directory: {}", parentLicense.asFile)
				parentLicense
			}
			else -> {
				logger.warn("No LICENSE file found in project or parent directory.")
				null
			}
		}
	}

	repositories {
		mavenCentral()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
		maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
		maven("https://maven.thesignalumproject.net/nightly") { name = "SignalumMavenNightly" }
		maven("https://maven.danygames2014.net/signalum") { name = "SignalumMavenMirror1" }
		ivy("https://github.com/Turnip-Labs") {
			patternLayout {
				artifact("/fabric-loader/releases/download/[revision]/fabric-loader-[revision].jar")
			}
			metadataSources { artifact() }
			content { includeGroup("bta.loader") }
		}
		ivy("https://github.com/Better-than-Adventure") {
			patternLayout { artifact("[organisation]/releases/download/[revision]/[module]-bta-[revision].jar") }
			metadataSources { artifact() }
		}
		ivy("https://downloads.betterthanadventure.net/bta-client/${lib.findVersion("btaChannel").get()}/") {
			patternLayout { artifact("/v[revision]/client.jar") }
			metadataSources { artifact() }
		}
		ivy("https://downloads.betterthanadventure.net/bta-server/${lib.findVersion("btaChannel").get()}/") {
			patternLayout { artifact("/v[revision]/server.jar") }
			metadataSources { artifact() }
		}
		ivy("https://piston-data.mojang.com") {
			patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
			metadataSources { artifact() }
		}
	}

	dependencies {
		runtimeOnly(lib.findLibrary("clientJar").get())
		implementation(lib.findLibrary("loader").get())
		implementation(lib.findLibrary("halplibe").get())
		implementation(lib.findLibrary("modMenu").get())
		implementation(lib.findLibrary("legacyLwjgl").get())

		implementation(lib.findLibrary("slf4jApi").get())
		implementation(lib.findLibrary("guava").get())
		implementation(lib.findLibrary("log4j.slf4j2.impl").get())
		implementation(lib.findLibrary("log4j.core").get())
		implementation(lib.findLibrary("log4j.api").get())
		implementation(lib.findLibrary("log4j.api12").get())
		implementation(lib.findLibrary("gson").get())

		implementation(lib.findLibrary("commonsLang3").get())
	}

	tasks {
		withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
		withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
		withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
		withType<Jar>().configureEach {
			licenseFile?.let {
				from(it) {
					rename { original -> "${original}_${archiveBaseName.get()}" }
				}
			}
			destinationDirectory.set(file("$rootDir/jars"))
			archiveVersion.set(archiveVersion.get()+"-${lib.findVersion("bta").get()}")
		}
	}
}

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
