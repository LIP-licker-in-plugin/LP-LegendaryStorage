package com.darksoldier1404.dls.events;

import com.darksoldier1404.dls.LegendaryStorage;
import com.darksoldier1404.dls.functions.DLSFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DLSEvent implements Listener {
    private final LegendaryStorage plugin = LegendaryStorage.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        DLSFunction.initUserData(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        DLSFunction.saveAndLeave(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if(NBT.hasTagKey(e.getItem(), "dls-coupon")) {
                e.setCancelled(true);
                DLSFunction.useCoupon(e.getPlayer(), e.getItem());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (DLSFunction.currentInv.containsKey(e.getPlayer().getUniqueId())) {
            DInventory di = DLSFunction.currentInv.get(e.getPlayer().getUniqueId());
            if (di.isValidHandler(plugin)) {
                DLSFunction.saveCurrentContents(di);
                DLSFunction.saveInventory(di);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (DLSFunction.currentInv.containsKey(e.getWhoClicked().getUniqueId())) {
            DInventory di = DLSFunction.currentInv.get(e.getWhoClicked().getUniqueId());
            if (di.isValidHandler(plugin)) {
                if (e.getCurrentItem() != null) {
                    if(NBT.hasTagKey(e.getCurrentItem(), "dls-block") || NBT.hasTagKey(e.getCurrentItem(), "current")) {
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "page")) {
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                        e.setCancelled(true);
                        DLSFunction.saveCurrentContents(di);
                        if(di.prevPage()) {
                            DLSFunction.updateCurrentPage(di);
                        }
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                        e.setCancelled(true);
                        DLSFunction.saveCurrentContents(di);
                        if(di.nextPage()) {
                            DLSFunction.updateCurrentPage(di);
                        }
                    }
                }
            }
        }
    }
}
