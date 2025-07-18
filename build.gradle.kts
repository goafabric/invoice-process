import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val group: String by project
val version: String by project
java.sourceCompatibility = JavaVersion.VERSION_21

val dockerRegistry = "goafabric"
val baseImage = "ibm-semeru-runtimes:open-21.0.4.1_7-jre-focal@sha256:8b94f8b14fd1d4660f9dc777b1ad3630f847b8e3dc371203bcb857a5e74d6c39" //"ibm-semeru-runtimes:open-23_37-jre-focal@sha256:04534a98d0e521948b7525c665f9f8871aba56155de9e70d23b14c905a28a052"

plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.graalvm.buildtools.native") version "0.10.6"

	id("com.google.cloud.tools.jib") version "3.4.5"
	id("net.researchgate.release") version "3.1.0"
	id("org.sonarqube") version "6.2.0.5505"

	id("org.cyclonedx.bom") version "2.3.1"
	id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	constraints {
		annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
		implementation("org.mapstruct:mapstruct:1.6.3")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
		implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
		implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.1.2")
		testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
	}
}

dependencies {
	//web
	implementation("org.springframework.boot:spring-boot-starter-web")

	//monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")

	//cache
	implementation("org.springframework.boot:spring-boot-starter-cache");
	implementation("com.github.ben-manes.caffeine:caffeine");
	implementation("org.springframework.boot:spring-boot-starter-data-redis");
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

	//openapi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

	//kafka
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	//adapter
	implementation("io.github.resilience4j:resilience4j-spring-boot3")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	//s3
	implementation("am.ik.s3:simple-s3-client:0.2.2") {exclude("org.springframework", "spring-web")}

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("com.tngtech.archunit:archunit-junit5")
}

tasks.withType<Test> {
	useJUnitPlatform()
	exclude("**/*NRIT*")
	finalizedBy("jacocoTestReport")
}
tasks.jacocoTestReport { reports {csv.required.set(true); xml.required.set(true) } }

jib {
	val amd64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); amd64.os = "linux"; amd64.architecture = "amd64"; val arm64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); arm64.os = "linux"; arm64.architecture = "arm64"
	from.image = baseImage
	to.image = "${dockerRegistry}/${project.name}:${project.version}"
	container.jvmFlags = listOf("-Xms256m", "-Xmx256m")
	from.platforms.set(listOf(amd64, arm64))
}

interface InjectedExecOps { @get:Inject val execOps: ExecOperations }
tasks.register("dockerImageNative") { description= "Native Image"; group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
	val nativeImageName = "${dockerRegistry}/${project.name}-native:${project.version}"
	imageName.set(nativeImageName)
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true", "BP_JVM_VERSION" to "21", "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-J-Xmx5000m -march=compatibility"))
	doLast {
		project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker run --rm $nativeImageName -check-integrity") }
		project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker push $nativeImageName") }
	}
}

configure<net.researchgate.release.ReleaseExtension> {
	buildTasks.set(listOf("build", "test", "jib", "dockerImageNative"))
	tagTemplate.set("v${version}".replace("-SNAPSHOT", ""))
}

tasks.withType<org.springframework.boot.gradle.tasks.aot.ProcessAot>().configureEach {
	args("--spring.profiles.active=redis")
}

//tasks.cyclonedxBom { destination = file("doc/generated") }
openApi {
	outputDir.set(file("doc/generated"))
	customBootRun { args.set(listOf("--server.port=8080")) }
	tasks.forkedSpringBootRun { dependsOn("compileAotJava", "processAotResources") }
}

