package org.peter;

import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * config application
 *
 * @author lance
 * @date 2022/1/5 0:04
 */
public class ConfigApplication {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ConfigRetriever retriever = readYaml(vertx);

        retriever.getConfig(json -> {
//            DeploymentOptions options = new DeploymentOptions().setConfig(json.result());
//            vertx.deployVerticle(MainApp.class.getName(), options);
        });
        retriever.listen(new Handler<ConfigChange>() {
            @Override
            public void handle(ConfigChange configChange) {
                var a = configChange.getNewConfiguration();
                var b = configChange.getPreviousConfiguration();

                System.out.println(a);
                System.out.println(b);
            }
        });
    }

    private static ConfigRetriever readYaml(Vertx vertx) {
        String path_to_config = System.getProperty("vertx.config", "./config/config.json");
        System.out.println(Paths.get("./").toAbsolutePath().toString());
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", path_to_config));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        return ConfigRetriever.create(vertx, options);



//        ConfigStoreOptions store = new ConfigStoreOptions()
//                .setType("file")
//                .setFormat("json")
//                .setOptional(true)
//                .setConfig(new JsonObject().put("path", "application.json"));
//
//        return ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
//                .setScanPeriod(2000)
//                .addStore(store));
    }
}
