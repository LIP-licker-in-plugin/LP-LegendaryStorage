package com.darksoldier1404.dls;

import com.darksoldier1404.dls.commands.DLSCommand;
import com.darksoldier1404.dls.events.DLSEvent;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class LegendaryStorage extends JavaPlugin {
    private static LegendaryStorage plugin;
    public YamlConfiguration config;
    public String prefix;
    public int defaultSlot;
    public int maxPages;
    public static HashSet<UUID> opened = new HashSet<>();
    public static final Map<UUID, YamlConfiguration> udata = new HashMap<>();


    public static LegendaryStorage getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ColorUtils.applyColor(config.getString("Settings.prefix"));
        defaultSlot = config.getInt("Settings.DefaultSlot");
        maxPages = config.getInt("Settings.MaxPages");
        plugin.getServer().getPluginManager().registerEvents(new DLSEvent(), plugin);
        getCommand("창고").setExecutor(new DLSCommand());
    }

    @Override
    public void onDisable() {
    }
}
