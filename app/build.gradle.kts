plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("org.mindrot:jbcrypt:0.4")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "ticket.booking.App"
}