package lopzinc.sprinthud;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class configLoader {
    private static final File configFile = (FabricLoader.getInstance().getConfigDir().resolve("simplesprinthud.json")).toFile();
    private static final Gson gson = new Gson();

    public static Config loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                return gson.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
                Config tempConfig = new Config();
                saveConfig(tempConfig);
                return tempConfig;
            }
        } else {
            Config tempConfig = new Config();
            saveConfig(tempConfig);
            return tempConfig;
        }
    }

    public static void saveConfig(Config config) {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
            System.out.println("Config saved: " + config.x + " " + config.y + " " + config.hudEnabled + " " + config.modeDisplay + " " + config.format);
        } catch (IOException e) {e.printStackTrace();}
    }
}
