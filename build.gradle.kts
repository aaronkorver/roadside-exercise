plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

extra["springShellVersion"] = "3.1.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.springframework.data:spring-data-jpa:3.1.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.hibernate.orm:hibernate-spatial:6.3.1.Final")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
