package com.nouuidwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class NoUUIDWhiteList extends JavaPlugin {

    public static Plugin config;

    public static List<String> Whitelist;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getLogger().info(ChatColor.GREEN+"插件已加载,作者:3cxc");
        config = NoUUIDWhiteList.getPlugin(NoUUIDWhiteList.class);
        Whitelist = config.getConfig().getStringList("Whitelist");
        CommandRegister();//注册命令
        getServer().getPluginManager().registerEvents(new Event(),this);//注册Event
        new BukkitRunnable(){
            @Override
            public void run() {//异步执行，每50tick执行一次，每次执行检查一次在线玩家是否都有白名单
                ArrayList<Player> list = new ArrayList<>(getServer().getOnlinePlayers());
                for (int i = 0 ; i < list.size() ; i++){
                    if (!Whitelist.contains(list.get(i).getName())){
                        list.get(i).kickPlayer(ChatColor.RED+"你没有白名单！");
                    }
                }
            }
        }.runTaskTimerAsynchronously(this,70,50);
    }

    public void CommandRegister(){//命令注册
        PluginCommand command = getCommand("whitelist");
        if (command != null){
            command.setExecutor(new Commands());
            command.setTabCompleter(new Commands());
        }
        if (command == null){//如果命令注册失败
            getLogger().info(ChatColor.RED+"[ERROR]命令注册失败，为了安全将禁用本插件！");
            config.getPluginLoader().disablePlugin(config);//为了方便直接用了config
        }
    }

    @Override
    public void onDisable(){
        config.getConfig().set("Whitelist",Whitelist);
        config.saveConfig();
        config.reloadConfig();
        getLogger().info("插件数据已保存,插件已关闭");
    }
}
