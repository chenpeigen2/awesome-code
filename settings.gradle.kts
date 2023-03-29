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
