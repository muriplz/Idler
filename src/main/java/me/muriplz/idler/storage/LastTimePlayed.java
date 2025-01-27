package me.muriplz.idler.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LastTimePlayed {
    private final File file;
    private final Properties properties;

    public LastTimePlayed(String filePath) throws IOException {
        this.file = new File(filePath);

        // Check if the parent directories exist, create them if not
        if (!file.getParentFile().exists()) {
            boolean directoriesCreated = file.getParentFile().mkdirs();
            if (!directoriesCreated) {
                throw new IOException("Failed to create parent directories for: " + filePath);
            }
        }

        // Check if the file exists, create it if not
        if (!file.exists()) {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Failed to create file: " + filePath);
            }
        }

        this.properties = new Properties();
        this.properties.load(new FileInputStream(file));
    }


    public Map<UUID, Long> getHashMap() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        }

        Map<UUID, Long> hashMap = new ConcurrentHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            hashMap.put(UUID.fromString(key), Long.parseLong(properties.getProperty(key)));
        }
        return hashMap;
    }

    public void setHashMap(Map<UUID, Long> hashMap) throws IOException {
        for (Map.Entry<UUID, Long> entry : hashMap.entrySet()) {
            properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        properties.store(new FileOutputStream(file), null);
    }

    public void addElement(UUID playerID, long lastTimePlayed) {
        try {
            Map<UUID, Long> hashMap = getHashMap();
            hashMap.put(playerID, lastTimePlayed);
            setHashMap(hashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getElement(UUID playerID) {
        try {
            return getHashMap().getOrDefault(playerID, -1L);
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }
}

