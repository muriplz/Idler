package me.muriplz.idler.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;

public class LastTimePlayed {
    private final File file;
    private final ConcurrentHashMap<UUID, Long> map = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledSave;

    public LastTimePlayed(String filePath) throws IOException {
        this.file = new File(filePath);
        ensureFileExists();
        load();
    }

    private void ensureFileExists() throws IOException {
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create directories: " + parentDir);
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create file: " + file);
        }
    }

    private synchronized void load() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }
        map.clear();
        for (String key : props.stringPropertyNames()) {
            UUID uuid = UUID.fromString(key);
            long time = Long.parseLong(props.getProperty(key));
            map.put(uuid, time);
        }
    }

    private synchronized void save() {
        Properties props = new Properties();
        map.forEach((uuid, time) -> props.setProperty(uuid.toString(), String.valueOf(time)));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Last played times");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addElement(UUID playerId, long lastTimePlayed) {
        map.put(playerId, lastTimePlayed);
        scheduleDelayedSave();
    }

    private synchronized void scheduleDelayedSave() {
        if (scheduledSave != null && !scheduledSave.isDone()) {
            scheduledSave.cancel(false);
        }
        scheduledSave = scheduler.schedule(this::save, 5, TimeUnit.SECONDS);
    }

    public long getElement(UUID playerId) {
        return map.getOrDefault(playerId, -1L);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        save(); // Final save on shutdown
    }
}