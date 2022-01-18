package me.alon.setback;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class Setback extends JavaPlugin implements Listener{

    HashMap<Player, Location> lastMove = new HashMap<>();
    World worldToLagBack = Bukkit.getWorld(Objects.requireNonNull(this.getConfig().getString("world")));
    Permission bypassPermission = Bukkit.getPluginManager().getPermission(Objects.requireNonNull(this.getConfig().getString("bypass-permission")));
    Permission staffPermission = Bukkit.getPluginManager().getPermission(Objects.requireNonNull(this.getConfig().getString("staff-permission")));
    Boolean reportStaff = this.getConfig().getBoolean("report-staff");
    Boolean reportPlayer = this.getConfig().getBoolean("report-player");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        startSchedule();
    }

    void startSchedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (Player lp : Bukkit.getServer().getOnlinePlayers()) {
                    if (!lp.hasPermission(bypassPermission))
                        lastMove.put(lp, lp.getLocation());
                }
            }
        }, 0L, 20L); // 0 Tick initial delay, 20 Tick (1 Second) between repeats
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(bypassPermission)) {
            if (p.getGameMode() != GameMode.CREATIVE) {
                if (p.getWorld().equals(worldToLagBack)) {
                    e.setCancelled(true);
                    if (Math.floor(e.getBlock().getX()) == (int) Math.floor(e.getPlayer().getLocation().getX())) {
                        if (Math.floor(e.getBlock().getZ()) == (int) Math.floor(e.getPlayer().getLocation().getZ())) {
                            if (e.getBlock().getType().isSolid()) {
                                p.teleport(lastMove.get(p));
                                for (Player lp : Bukkit.getServer().getOnlinePlayers()) {
                                    if (lp.hasPermission(staffPermission)) {
                                        if (reportStaff)
                                            lp.sendMessage(ChatColor.GREEN + "Setback executed on " + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.GREEN + " in " + p.getWorld().getName());
                                        if (reportPlayer)
                                            p.sendMessage(ChatColor.RED + "You tried building and setback lagged you back! Building isn't enabled here!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(bypassPermission)) {
            if (p.getGameMode() != GameMode.CREATIVE) {
                if (p.getWorld().equals(worldToLagBack)) {
                    e.setCancelled(true);
                    if (Math.floor(e.getBlock().getX()) == (int) Math.floor(e.getPlayer().getLocation().getX())) {
                        if (Math.floor(e.getBlock().getZ()) == (int) Math.floor(e.getPlayer().getLocation().getZ())) {
                            e.getPlayer().teleport(lastMove.get(p));
                            for (Player lp : Bukkit.getOnlinePlayers()) {
                                if (lp.hasPermission(staffPermission)) {
                                    if (reportStaff)
                                        lp.sendMessage(ChatColor.GREEN + "Setback executed on " + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.GREEN + " in " + p.getWorld().getName());
                                    if (reportPlayer)
                                        p.sendMessage(ChatColor.RED + "You tried building and setback lagged you back! Building isn't enabled here!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}