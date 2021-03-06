package me.jonakls.miniannouncer.announce;

import me.jonakls.miniannouncer.MiniAnnouncer;
import me.jonakls.miniannouncer.message.MessageHandler;
import me.jonakls.miniannouncer.stack.AnnouncementStack;
import me.jonakls.miniannouncer.stack.AnnouncementStackCreator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AnnouncementManager {

    private final MiniAnnouncer plugin;
    private final MessageHandler messageHandler;
    private final AnnouncementStackCreator stackCreator;

    private int taskId;

    public AnnouncementManager(MiniAnnouncer plugin,
                               MessageHandler messageHandler,
                               AnnouncementStackCreator stackCreator) {
        this.plugin = plugin;
        this.messageHandler = messageHandler;
        this.stackCreator = stackCreator;
    }

    public List<Announcement> parseAnnouncements() {
        FileConfiguration configuration = plugin.getConfig();
        ConfigurationSection section = configuration
                .getConfigurationSection("announcements");

        if (section == null) {
            return Collections.emptyList();
        }

        return stackCreator.parse(section);
    }

    public @Nullable AnnouncementStack createStack() {
        FileConfiguration configuration = plugin.getConfig();
        ConfigurationSection section = configuration
                .getConfigurationSection("announcer");

        if (section == null) {
            return null;
        }

        return stackCreator.createStack(section, parseAnnouncements());
    }

    public void toggleAnnouncements(Plugin plugin, CommandSender sender) {
        FileConfiguration configuration = plugin.getConfig();
        boolean state = !configuration.getBoolean("announcer.enabled");

        if (state) {
            AnnouncementStack announcementStack = createStack();
            startTask(plugin, announcementStack);
        } else {
            stopTask();
        }

        configuration.set("announcer.enabled", state);
        messageHandler.sendMessage(sender, "toggle-announcements", state);
    }

    public void startTask(Plugin plugin, AnnouncementStack announcementStack) {
        FileConfiguration configuration = plugin.getConfig();
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, new AnnouncementTask(announcementStack, messageHandler),
                0L, 20L * configuration.getInt("announcer.interval")
        ).getTaskId();
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
