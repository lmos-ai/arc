
dependencies {

    implementation(project(":arc-agents"))
    implementation(project(":arc-result"))
    implementation(project(":arc-api"))

    // Graphql
    implementation("com.expediagroup:graphql-kotlin-spring-server:7.1.4")
    implementation("com.graphql-java:graphql-java:21.5")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Micrometer
    implementation(platform("io.micrometer:micrometer-tracing-bom:1.3.2"))
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
