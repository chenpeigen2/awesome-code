package featuretest;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

//        AbstractScriptEngine
//        Provides a standard implementation for several of the variants of the eval method.
//        Bindings
//        A mapping of key/value pairs, all of whose keys are Strings.
//        Compilable
//        The optional interface implemented by ScriptEngines whose methods compile scripts to a form that can be executed repeatedly without recompilation.
//        CompiledScript
//        Extended by classes that store results of compilations.
//        Invocable
//        The optional interface implemented by ScriptEngines whose methods allow the invocation of procedures in scripts that have previously been executed.
//        ScriptContext
//        The interface whose implementing classes are used to connect Script Engines with objects, such as scoped Bindings, in hosting applications.
//        ScriptEngine
//        ScriptEngine is the fundamental interface whose methods must be fully functional in every implementation of this specification.
//        ScriptEngineFactory
//        ScriptEngineFactory is used to describe and instantiate ScriptEngines.
//        ScriptEngineManager
//        The ScriptEngineManager implements a discovery and instantiation mechanism for ScriptEngine classes and also maintains a collection of key/value pairs storing state shared by all engines created by the Manager.
//        ScriptException
//        The generic Exception class for the Scripting APIs.
//        SimpleBindings
//        A simple implementation of Bindings backed by a HashMap or some other specified Map.
//        SimpleScriptContext
//        Simple implementation of ScriptContext.


public class ScriptTest {
    //    https://blog.csdn.net/hqwang4/article/details/78777716
    static ScriptEngineManager factory = new ScriptEngineManager();

    static void a1() {
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName(
                "JavaScript"
        );
        engine.put("a", 4);
        engine.put("b", 6);

        try {
            Object maxNum = engine.eval(
                    "function max_num(a,b){return (a>b)?a:b;}max_num(a,b);"
            );

            System.out.println("max_num:" + maxNum);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    static void a2() {
        ScriptEngineManager factory =
                new
                        ScriptEngineManager();

        // create a JavaScript engine


        ScriptEngine engine = factory.getEngineByName(
                "JavaScript"
        );

        try {
            engine.eval(
                    "function max_num(a,b){return (a>b)?a:b;}"
            );
            Invocable invoke = (Invocable) engine;

            Object maxNum = invoke.invokeFunction(
                    "max_num"
                    ,
                    4
                    ,
                    6
            );
            System.out.println(maxNum);
            maxNum = invoke.invokeFunction(
                    "max_num"
                    ,
                    7
                    ,
                    6
            );
            System.out.println(maxNum);
        } catch
        (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) throws ScriptException {

        a1();

        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("js");
        engine.eval("print('Hello Js!')");

        a2();
    }
}
