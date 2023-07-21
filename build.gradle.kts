import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  apply(from = ".buildscripts/git-hooks.gradle")
}

plugins {
  kotlin("jvm") version ("1.8.22") apply false
  id("com.github.ben-manes.versions") version ("0.42.0") apply false
  id("io.gitlab.arturbosch.detekt") version ("1.21.0") apply false
  id("com.github.node-gradle.node") version ("5.0.0") apply false
  id("org.jetbrains.kotlinx.kover") version ("0.7.1") apply false
}

allprojects {
  group = "com.legacycode"

  repositories {
    mavenCentral()
  }
}

subprojects {
  if (this.name == "web-client-react") {
    return@subprojects
  }

  apply(plugin = "com.github.ben-manes.versions")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlinx.kover")

  if (this.name != "bytecode-samples") {
    apply(plugin = "io.gitlab.arturbosch.detekt")
  }

  dependencies {
    val implementation by configurations
    val testImplementation by configurations
    val testRuntimeOnly by configurations

    // logging
    implementation(rootProject.libs.logback)

    // testing
    testImplementation(kotlin("test-junit5"))
    testImplementation(rootProject.testLibs.junit.api)
    testImplementation(rootProject.testLibs.junit.params)
    testRuntimeOnly(rootProject.testLibs.junit.engine)

    testImplementation(rootProject.testLibs.truth)

    testImplementation(rootProject.testLibs.approvalTests)
    testImplementation(rootProject.testLibs.gson) /* Used by Approvals for pretty-printing JSON */
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  tasks.withType<JacocoReport> {
    dependsOn(tasks.withType(Test::class.java))

    reports {
      csv.required.set(false)
      xml.required.set(true)
    }
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf { isNonStable(candidate.version) }
}
