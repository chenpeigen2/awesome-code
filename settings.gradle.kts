rootProject.name = "awesome-code"

// JDK 工具链自动下载：本机缺少匹配版本（Java 25）时，Gradle 会从 foojay 下载对应 JDK
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// 依赖版本目录 (Version Catalog)
// Gradle 默认自动从 gradle/libs.versions.toml 加载名为 "libs" 的版本目录
// 子项目可通过 libs 访问器使用统一的依赖版本
// 例如: implementation(libs.guava) 或 alias(libs.plugins.kotlin.jvm)
include("auto:autovalue")
findProject(":auto:autovalue")?.name = "autovalue"
include("time:chrono")
findProject(":time:chrono")?.name = "chrono"
include("reflect:anno")
findProject(":reflect:anno")?.name = "anno"
include("json:gson")
findProject(":json:gson")?.name = "gson"
include("http:okhttp")
findProject(":http:okhttp")?.name = "okhttp"
include("compile:apt")
findProject(":compile:apt")?.name = "apt"
include("compile:apt-processor")
findProject(":compile:apt-processor")?.name = "apt-processor"
include("compile:inmemory")
findProject(":compile:inmemory")?.name = "inmemory"
include("rxjava3")
include("pref")
include("io:okio")
findProject(":io:okio")?.name = "okio"
// ASM 字节码操作：:asm:basics（Core API）/ :asm:tree（Tree API）/ :asm:transform（转换）/ :asm:poet（源码生成）
include("asm:basics")
include("asm:tree")
include("asm:transform")
include("asm:poet")
include("database:couchdb")
findProject(":database:couchdb")?.name = "couchdb"
include("database:mongodb")
findProject(":database:mongodb")?.name = "mongodb"
include("nacos:simple-nacos-demo")
findProject(":nacos:simple-nacos-demo")?.name = "simple-nacos-demo"
include("jwt")
include("web:simple-webflux")
findProject(":web:simple-webflux")?.name = "simple-webflux"
include("web:kotlin-spring-coroutines")
findProject(":web:kotlin-spring-coroutines")?.name = "kotlin-spring-coroutines"
include("spi:autoservice")
findProject(":spi:autoservice")?.name = "autoservice"
include("spi:serviceprovider")
findProject(":spi:serviceprovider")?.name = "serviceprovider"
include("explore:jcommander")
findProject(":explore:jcommander")?.name = "jcommander"
include("explore:spoon")
findProject(":explore:spoon")?.name = "spoon"
include("explore:graphQL")
findProject(":explore:graphQL")?.name = "graphQL"
include("explore:javaParser")
findProject(":explore:javaParser")?.name = "javaParser"
include("grpc")
include("dagger")
include("kotlin:basic-kotlin")
findProject(":kotlin:basic-kotlin")?.name = "basic-kotlin"
include("kotlin:native-kotlin")
findProject(":kotlin:native-kotlin")?.name = "native-kotlin"
include("kotlin:coroutines-examples")
findProject(":kotlin:coroutines-examples")?.name = "coroutines-examples"
include("explore:spring-shell")
findProject(":explore:spring-shell")?.name = "spring-shell"
include("vertx:vertx-grpc")
findProject(":vertx:vertx-grpc")?.name = "vertx-grpc"
include("database:postgres")
findProject(":database:postgres")?.name = "postgres"
include("explore:Mutiny")
findProject(":explore:Mutiny")?.name = "Mutiny"
include("explore:disruptor")
findProject(":explore:disruptor")?.name = "disruptor"
include("vertx:vertx-config")
findProject(":vertx:vertx-config")?.name = "vertx-config"
include("explore:velocity")
findProject(":explore:velocity")?.name = "velocity"
include("explore:reactivestreams")
findProject(":explore:reactivestreams")?.name = "reactivestreams"
include("vertx:service-discovery")
findProject(":vertx:service-discovery")?.name = "service-discovery"
include("vertx:vertx-web")
findProject(":vertx:vertx-web")?.name = "vertx-web"
include("vertx:vertx-coroutines-web")
findProject(":vertx:vertx-coroutines-web")?.name = "vertx-coroutines-web"
include("explore:jsqlparser")
findProject(":explore:jsqlparser")?.name = "jsqlparser"
include("explore:snappy")
findProject(":explore:snappy")?.name = "snappy"
include("explore:Kryo")
findProject(":explore:Kryo")?.name = "Kryo"
include("explore:calcite-tutorial")
findProject(":explore:calcite-tutorial")?.name = "calcite-tutorial"
include("goodcode")
include("explore:jsprit")
findProject(":explore:jsprit")?.name = "jsprit"

include("explore:javassist")
include("zip")
include("explore:ai")
include("patterns")
include("claude")
include("kotlin:immutable-collections")
findProject(":kotlin:immutable-collections")?.name = "immutable-collections"
