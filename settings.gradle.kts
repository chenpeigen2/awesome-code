rootProject.name = "awesome-code"
include("auto:autovalue")
findProject(":auto:autovalue")?.name = "autovalue"
include("time:chrono")
findProject(":time:chrono")?.name = "chrono"
include("reflect:anno")
findProject(":reflect:anno")?.name = "anno"
include("asm:poet")
findProject(":asm:poet")?.name = "poet"
include("json:gson")
findProject(":json:gson")?.name = "gson"
include("http:okhttp")
findProject(":http:okhttp")?.name = "okhttp"
include("groovy")
include("compile:apt")
findProject(":compile:apt")?.name = "apt"
include("rxjava3")
include("pref")
include("io:okio")
findProject(":io:okio")?.name = "okio"
include("asm:core")
findProject(":asm:core")?.name = "core"
include("asm:model")
findProject(":asm:model")?.name = "model"
include("database:couchdb")
findProject(":database:couchdb")?.name = "couchdb"
include("database:mongodb")
findProject(":database:mongodb")?.name = "mongodb"
include("nacos:simple-nacos-demo")
findProject(":nacos:simple-nacos-demo")?.name = "simple-nacos-demo"
include("jwt")
include("web:simple-webflux")
findProject(":web:simple-webflux")?.name = "simple-webflux"
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
