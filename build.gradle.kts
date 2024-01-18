import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.net.InetAddress
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    groovy
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.50.0"
}

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11


val buildTime = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
val builtBy = InetAddress.getLocalHost().hostName

dependencies {
    implementation("io.dropwizard:dropwizard-core:${property("dropwizard")}")
    implementation("com.datastax.oss:java-driver-core:${property("cassandraDriver")}")
    implementation("com.datastax.oss:java-driver-mapper-runtime:${property("cassandraDriver")}")
    annotationProcessor("com.datastax.oss:java-driver-mapper-processor:${property("cassandraDriver")}")
    implementation("org.lz4:lz4-java:1.8.0")
    testImplementation("io.dropwizard:dropwizard-testing:${property("dropwizard")}")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("io.rest-assured:rest-assured:${property("restAssured")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junit")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit")}")
}

application {
    mainClass.set("com.knoma.web.WebApp")
}

tasks.withType<JavaCompile> {
//     options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    exclude("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")
    manifest {
        attributes["Implementation-Title"] = rootProject.name
        attributes["Implementation-Version"] = rootProject.version
        attributes["Implementation-Vendor-Id"] = rootProject.group
        attributes["Build-Time"] = buildTime
        attributes["Built-By"] = builtBy
        attributes["Created-By"] = "Gradle " + gradle.gradleVersion
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<JavaExec>("run") {
    args = listOf("server", "config.yml")
}

tasks.named("assemble") {
    dependsOn("shadowJar")
}