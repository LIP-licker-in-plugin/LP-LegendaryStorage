package com.darksoldier1404.dls.functions;

import com.darksoldier1404.dls.LegendaryStorage;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("all")
public class DLSFunction {
    private static final LegendaryStorage plugin = LegendaryStorage.getInstance();

    public static void updateCurrentPage(DInventory inv) {
        ItemStack[] tools = inv.getPageTools();
        ItemStack item = tools[4];
        ItemMeta im = item.getItemMeta();
        im.setDisplayName("§a현재 페이지: §f" + (inv.getCurrentPage() + 1));
        item.setItemMeta(im);
        tools[4] = item;
        inv.setPageTools(tools);
        inv.update();
    }

    public static void openStorage(Player p, Player target) {
        if(plugin.opened.contains(target.getUniqueId())) {
            p.sendMessage(ColorUtils.applyColor("&a아직 사용할 수 없습니다."));
            return;
        }
        String title = plugin.config.getString("Settings.title");
        title = title.replace("<player>", target.getName());
        DInventory inv = new DInventory(null, ColorUtils.applyColor(title), 54, true, plugin);
        inv.setPages(0);
        inv.setObj(target.getUniqueId());

        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "page", "true");
        ItemStack block = NBT.setStringTag(new ItemStack(Material.BARRIER), "dls-block", "true");
        ItemStack prev = NBT.setStringTag(new ItemStack(Material.PINK_DYE), "prev", "true");
        ItemStack current = NBT.setStringTag(new ItemStack(Material.PAPER), "current", "true");
        ItemMeta im = prev.getItemMeta();
        im.setDisplayName("이전 페이지");
        prev.setItemMeta(im);
        ItemStack next = NBT.setStringTag(new ItemStack(Material.LIME_DYE), "next", "true");
        im = next.getItemMeta();
        im.setDisplayName("다음 페이지");
        next.setItemMeta(im);
        im = current.getItemMeta();
        im.setDisplayName("§a현재 페이지: §f" + (inv.getCurrentPage() + 1));
        current.setItemMeta(im);
        im = block.getItemMeta();
        im.setDisplayName("§c소유하지 않은 슬롯");
        block.setItemMeta(im);
        inv.setPageTools(new ItemStack[]{pane, pane, prev, pane, current, pane, next, pane, pane});

        Map<Integer, ItemStack> items = new HashMap<>();
        YamlConfiguration data = plugin.udata.get(target.getUniqueId());
        int owned = data.getInt("Owned");
        owned += plugin.defaultSlot;
        if (data.getConfigurationSection("Storage") != null) {
            for (String key : data.getConfigurationSection("Storage").getKeys(false)) {
                items.put(Integer.parseInt(key), data.getItemStack("Storage." + key) == null ? new ItemStack(Material.AIR) : data.getItemStack("Storage." + key));
            }
        }
        int maxPages = plugin.maxPages;
        int count = 0;
        ItemStack[] contents = new ItemStack[45];
        inv.setPages(maxPages);
        for (int page = 0; page <= maxPages; page++) {
            for (int i = 0; i < 45; i++) {
                contents[i] = items.get(count);
                if (owned == 0) {
                    contents[i] = block;
                } else {
                    owned--;
                }
                count++;
            }
            inv.setPageContent(page, contents);
            contents = new ItemStack[45];
        }
        inv.update();
        p.openInventory(inv);
    }

    public static void saveCurrentContents(DInventory inv) {
        int currentPage = inv.getCurrentPage();
        ItemStack[] content = new ItemStack[45];
        for (int i = 0; i < 45; i++) {
            content[i] = inv.getContents()[i];
        }
        inv.getPageItems().remove(currentPage);
        inv.getPageItems().put(currentPage, content);
    }

    public static void saveInventory(DInventory inv) {
        Player p = Bukkit.getPlayer((UUID) inv.getObj());
        plugin.opened.remove(p.getUniqueId());
        YamlConfiguration data = plugin.udata.get(p.getUniqueId());
        int count = 0;
        for (ItemStack[] item : inv.getPageItems().values()) {
            for (int i = 0; i < item.length; i++) {
                if (!NBT.hasTagKey(item[i], "dls-block")) {
                    data.set("Storage." + count, item[i]);
                }
                count++;
            }
        }
    }

    public static void getCoupon(Player p, String sint) {
        int i = 0;
        try {
            i = Integer.parseInt(sint);
        } catch (NumberFormatException e) {
            p.sendMessage(ColorUtils.applyColor("옳바르지 않은 숫자 입니다."));
            return;
        }
        String name = ColorUtils.applyColor(plugin.config.getString("Settings.Coupon.displayName")).replace("<slot>", i + "");
        List<String> lore = plugin.config.getStringList("Settings.Coupon.lore");
        for (int j = 0; j < lore.size(); j++) {
            lore.set(j, ColorUtils.applyColor(lore.get(j)).replace("<slot>", i + ""));
        }
        ItemStack coupon = new ItemStack(Material.valueOf(plugin.config.getString("Settings.Coupon.Material")));
        ItemMeta im = coupon.getItemMeta();
        im.setLore(lore);
        im.setDisplayName(name);
        coupon.setItemMeta(im);
        p.getInventory().addItem(NBT.setIntTag(coupon, "dls-coupon", i));
        p.sendMessage("쿠폰을 발급했습니다.");
    }

    public static void initUserData(Player p) {
        YamlConfiguration data = ConfigUtils.initUserData(plugin, p.getUniqueId().toString(), "udata");
        plugin.udata.put(p.getUniqueId(), data);
    }

    public static void saveAndLeave(Player p) {
        ConfigUtils.saveCustomData(plugin, plugin.udata.get(p.getUniqueId()), p.getUniqueId().toString(), "udata");
        plugin.udata.remove(p.getUniqueId());
    }

    public static void useCoupon(Player p, ItemStack item) {
        int i = NBT.getIntegerTag(item, "dls-coupon");
        YamlConfiguration data = plugin.udata.get(p.getUniqueId());
        int owned = data.getInt("Owned");
        int maxPages = plugin.maxPages + 1;
        if(owned + i + plugin.defaultSlot > (maxPages * 45)) {
            p.sendMessage(ColorUtils.applyColor("&a창고를 확장할 수 있는 최대 슬롯을 넘었습니다."));
            return;
        }
        data.set("Owned", owned + i);
        p.sendMessage(ColorUtils.applyColor("&a창고 확장 쿠폰을 사용했습니다."));
        item.setAmount(item.getAmount() - 1);
    }

    public static void reloadConfig() {
        plugin.config = ConfigUtils.reloadPluginConfig(plugin, plugin.config);
        plugin.prefix = plugin.config.getString("Settings.prefix");
        plugin.defaultSlot = plugin.config.getInt("Settings.DefaultSlot");
        plugin.maxPages = plugin.config.getInt("Settings.MaxPages");
    }
}
