rootProject.name = "awesome-code"
include("auto:autovalue")
findProject(":auto:autovalue")?.name = "autovalue"
include("time:chrono")
findProject(":time:chrono")?.name = "chrono"
include("reflect:anno")
findProject(":reflect:anno")?.name = "anno"
include("asm:poet")
findProject(":asm:poet")?.name = "poet"
