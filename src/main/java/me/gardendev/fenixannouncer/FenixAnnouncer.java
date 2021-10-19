package me.gardendev.fenixannouncer;

import me.gardendev.fenixannouncer.commands.MainCommand;
import me.gardendev.fenixannouncer.managers.AnnouncerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

@SuppressWarnings("all")
public final class FenixAnnouncer extends JavaPlugin {

    private AnnouncerManager announcerManager;

    @Override
    public void onEnable() {
        this.registerConfig();

        announcerManager = new AnnouncerManager(this);
        announcerManager.initTask();
        getCommand("fenixannouncer").setExecutor(new MainCommand(this));

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Hook PlaceholderAPI");
            return;
        }
        getLogger().log(Level.WARNING, "The PlaceholderAPI plugin is missing, this plugin cannot function properly without this dependency");

    }

    @Override
    public void onDisable() {
        announcerManager.stopTask();
    }

    private void registerConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }
    }

    public AnnouncerManager getAnnouncerManager() {
        return announcerManager;
    }
}
