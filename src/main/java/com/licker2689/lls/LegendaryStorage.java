package com.licker2689.lls;

import com.licker2689.lls.commands.LLSCommand;
import com.licker2689.lls.events.LLSEvent;
import com.licker2689.lls.functions.LLSFunction;
import com.licker2689.lpc.utils.ColorUtils;
import com.licker2689.lpc.utils.ConfigUtils;
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
        plugin.getServer().getPluginManager().registerEvents(new LLSEvent(), plugin);
        Objects.requireNonNull(getCommand("창고")).setExecutor(new LLSCommand());
    }


    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(LLSFunction::saveAndLeave);
        ConfigUtils.savePluginConfig(plugin, config);
    }
}
