package lopzinc.sprinthud;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class configStore {
    public static int x = 10;
    public static int y = 10;

    private static final File file = (FabricLoader.getInstance().getConfigDir().resolve("simplesprinthud.txt")).toFile();

    static {
        System.out.println("loading x,y from config");
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String config = reader.readLine();
                String[] coordinates = config.split(":",2)[0].split(",");
                x = Integer.parseInt(coordinates[0]);
                y = Integer.parseInt(coordinates[1]);
                reader.close();
            } catch (IOException e) {e.printStackTrace();}
        } else { createFile(); }
    }

    public static void save() {
        System.out.println("Save called");
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String config = reader.readLine();
                String coordinates = x + "," + y + ":";
                config = config.replaceFirst("^\\d+,\\d+:",coordinates);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(config);
                writer.close();
                System.out.println("Saving: " + config);
            } catch (IOException e) {e.printStackTrace();}
        } else { createFile(); }
    }

    public static void format(String input) {
        System.out.println("format change called");
        if (!file.exists()) { createFile(); }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentData = reader.readLine();
            String[] splitCurrentData = currentData.split(":");
            currentData = splitCurrentData[0]+":"+splitCurrentData[1];
            currentData += ":" + input;
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentData);
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    public static boolean CycleHud() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] splittedLine = reader.readLine().split(":");
            splittedLine[1] = String.valueOf(!Boolean.parseBoolean(splittedLine[1]));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String updatedLine = String.join(":",splittedLine);
            writer.write(updatedLine);
            reader.close();
            writer.close();
            return Boolean.parseBoolean(splittedLine[1]);
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    private static void createFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String coordinates = (x + "," + y)+":true:"+("&&7Sprint: %status%");
            writer.write(coordinates);
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }


}

