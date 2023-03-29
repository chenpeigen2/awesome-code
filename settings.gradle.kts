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
