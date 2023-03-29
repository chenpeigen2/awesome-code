package basis

import groovy.json.JsonSlurper
import org.codehaus.groovy.runtime.DefaultGroovyMethods

class Main {


    static void main(String[] args) {
        def str1 = '单引号'
        def str2 = "双引号"

        String a = "hello world"
//
        println("hello from ${str1}   ${str2}")
        println 'hello world from 1'
        println "hello world from 2"

//        java.util.ArrayList
        def numList = [1, 2, 3, 4, 5, 6]
        println(numList.class.name)

//        java.util.LinkedHashMap
        def map1 = ["width": 1024, "height": 768]
        print(map1.getClass().name)

        System.out.println("hello from java we are fucking amazing")

        var zzz = 234;
        println zzz

        def jsonSlurper = new JsonSlurper()
        def lst = jsonSlurper.parseText('{ "List": [2, 3, 4, 5] }')

        println(lst.class)
        lst.each { println(it) }


        closure()
    }

    static def closure() {
        var f = { x ->
            print("xxxxxx")
            println(x)
        }

        var f1 = {
            println("call 1")
        }
//        f.call(1)

        f.andThen(f1).andThen {
            println("call 2")
        }.call(1)

        DefaultGroovyMethods.downto(12, 1) {
            println(it)
        }

        12.downto(1) {
            println(it)
        }

        println(1 * 2)
        println("1" * 2)


        def name = '六号表哥'
        def str4 = "say hello to ${name}"

    }

    private def method1(int a, int b) {
        return a + b
    }

    def method2(def a, def b) {
        return a + b
    }
}
