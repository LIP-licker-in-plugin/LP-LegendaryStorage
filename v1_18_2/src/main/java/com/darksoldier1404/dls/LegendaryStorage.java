package com.darksoldier1404.dls;

import com.darksoldier1404.dls.commands.DLSCommand;
import com.darksoldier1404.dls.events.DLSEvent;
import com.darksoldier1404.dls.functions.DLSFunction;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

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
        prefix = ColorUtils.applyColor(Objects.requireNonNull(config.getString("Settings.prefix")));
        defaultSlot = config.getInt("Settings.DefaultSlot");
        maxPages = config.getInt("Settings.MaxPages");
        plugin.getServer().getPluginManager().registerEvents(new DLSEvent(), plugin);
        Objects.requireNonNull(getCommand("창고")).setExecutor(new DLSCommand());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(DLSFunction::saveAndLeave);
        ConfigUtils.savePluginConfig(plugin, config);
    }
}
