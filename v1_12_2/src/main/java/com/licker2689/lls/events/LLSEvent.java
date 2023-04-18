package com.licker2689.lls.events;

import com.licker2689.lls.LegendaryStorage;
import com.licker2689.lls.functions.LLSFunction;
import com.licker2689.lpc.api.inventory.LInventory;
import com.licker2689.lpc.utils.NBT;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LLSEvent implements Listener {
    private final LegendaryStorage plugin = LegendaryStorage.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        LLSFunction.initUserData(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        LLSFunction.saveAndLeave(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if(NBT.hasTagKey(e.getItem(), "lls-coupon")) {
                e.setCancelled(true);
                LLSFunction.useCoupon(e.getPlayer(), e.getItem());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (LLSFunction.currentInv.containsKey(e.getPlayer().getUniqueId())) {
            LInventory di = LLSFunction.currentInv.get(e.getPlayer().getUniqueId());
            if (di.isValidHandler(plugin)) {
                LLSFunction.saveCurrentContents(di);
                LLSFunction.saveInventory(di);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (LLSFunction.currentInv.containsKey(e.getWhoClicked().getUniqueId())) {
            LInventory di = LLSFunction.currentInv.get(e.getWhoClicked().getUniqueId());
            if (di.isValidHandler(plugin)) {
                if (e.getCurrentItem() != null) {
                    if(NBT.hasTagKey(e.getCurrentItem(), "lls-block") || NBT.hasTagKey(e.getCurrentItem(), "current")) {
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "page")) {
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                        e.setCancelled(true);
                        LLSFunction.saveCurrentContents(di);
                        if(di.prevPage()) {
                            LLSFunction.updateCurrentPage(di);
                        }
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                        e.setCancelled(true);
                        LLSFunction.saveCurrentContents(di);
                        if(di.nextPage()) {
                            LLSFunction.updateCurrentPage(di);
                        }
                    }
                }
            }
        }
    }
}
