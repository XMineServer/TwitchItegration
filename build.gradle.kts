plugins {
	application
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.sidey383"
version = "0.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

extra["springCloudVersion"] = "2025.0.0"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.2.0")

	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
	implementation("org.glavo:rcon-java:3.0")
	implementation("net.kyori:adventure-api:4.22.0")
	implementation("net.kyori:adventure-text-serializer-gson:4.22.0")

	implementation("com.github.twitch4j:twitch4j:1.25.0")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	compileOnly("org.projectlombok:lombok")
	implementation("org.mapstruct:mapstruct:1.4.2.Final")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")

	compileOnly("org.jetbrains:annotations:26.0.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	imageName.set("sidey383/twitch-integration")
	tags.set(setOf(
		"sidey383/twitch-integration:${project.version}",
		"sidey383/twitch-integration:latest"
	))
	environment.set(
		mapOf(
			"BP_JVM_VERSION" to "21",
			"BP_JVM_TYPE" to "jdk"
		)
	)
}
