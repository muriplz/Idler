package me.muriplz.idler.afk;

import me.muriplz.idler.config.ConfigReader;

// This class has been mostly made by afkdisplay mod
// https://github.com/beabfc/afkdisplay
public class Config {
    public static class PacketOptions {
        public static int timeoutSeconds = ConfigReader.IDLE_TIMEOUT_SECONDS;
        public static boolean resetOnMovement = true;
        public static boolean resetOnLook = true;
    }

    public static class PlayerListOptions {
        public static boolean enableListDisplay = true;
        public static String afkColor = "gray";
    }
}
