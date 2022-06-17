package com.darksoldier1404.dls.commands;

import com.darksoldier1404.dls.LegendaryStorage;
import com.darksoldier1404.dls.functions.DLSFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class DLSCommand implements CommandExecutor, TabCompleter {
    private LegendaryStorage plugin = LegendaryStorage.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix + "플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            if (p.hasPermission("dls.admin")) {
                p.sendMessage(plugin.prefix + "/창고 쿠폰 <칸수> - 창고 확장 쿠폰을 발급합니다.");
                p.sendMessage(plugin.prefix + "/창고 오픈 <닉네임> - 해당 플레이어의 창고를 오픈합니다.");
                p.sendMessage(plugin.prefix + "/창고 리로드 - 콘피그 설정 파일을 리로드 합니다.");
            } else if (p.hasPermission("dls.use")) {
                p.sendMessage(plugin.prefix + "/창고 오픈 - 창고를 오픈합니다.");
                p.sendMessage(plugin.prefix + "/창고 구매 - 창고를 구매 선택창을 오픈합니다.");
            } else {
                p.sendMessage(plugin.prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            return false;
        }
        if (args[0].equals("오픈")) {
            if (args.length == 1) {
                if (p.hasPermission("dls.use")) {
                    DLSFunction.openStorage(p, p);
                    return false;
                } else {
                    p.sendMessage(plugin.prefix + "권한이 없습니다.");
                    return false;
                }
            }
            if (args.length == 2) {
                if (p.hasPermission("dls.admin")) {
                    DLSFunction.openStorage(p, Bukkit.getPlayer(args[1]));
                    return false;
                }else{
                    p.sendMessage(plugin.prefix + "권한이 없습니다.");
                    return false;
                }
            }
        }
        if (args[0].equals("쿠폰")) {
            if (p.hasPermission("dls.admin")) {
                if (args.length == 1) {
                    p.sendMessage(plugin.prefix + "칸수를 입력해주세요.");
                    return false;
                }
                if (args.length == 2) {
                    DLSFunction.getCoupon(p, args[1]);
                    return false;
                }
            }
        }
        if (args[0].equals("리로드")) {
            if (p.hasPermission("dls.admin")) {
                DLSFunction.reloadConfig();
                p.sendMessage(plugin.prefix + "설정 파일을 리로드 하였습니다.");
                return false;
            }else{
                p.sendMessage(plugin.prefix + "권한이 없습니다.");
                return false;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String
            alias, @NotNull String[] args) {
        if (sender.hasPermission("dls.admin")) {
            if (args.length == 1) {
                return Arrays.asList("오픈", "쿠폰", "리로드");
            }
            if (args.length == 2) {
                if (args[0].equals("오픈")) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                }
            }
        } else if (sender.hasPermission("dls.use")) {
            if (args.length == 1) {
                return Arrays.asList("오픈");
            }
        }
        return null;
    }
}
