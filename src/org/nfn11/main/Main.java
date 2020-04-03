package org.nfn11.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.nfn11.events.FastDeath;
import org.nfn11.events.HidePlayersInSameLobby;
import org.nfn11.events.TNT;

public class Main extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		doConfig();
		if(isRecentVersion()) Bukkit.getLogger().warning("Fast death on this server, because 1.15 has doImmediateRespawn gamerule.");
		this.getServer().getPluginManager().registerEvents(new FastDeath(this), this);
        this.getServer().getPluginManager().registerEvents(new TNT(this), this);
        this.getServer().getPluginManager().registerEvents(new HidePlayersInSameLobby(this), this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new org.nfn11.papi.PlaceholderAPIHook(this).register();
        }
	}
	
	private void doConfig() {
		getDataFolder().mkdirs();
		final File file = new File(getDataFolder(), "config.yml");
		final FileConfiguration config = new YamlConfiguration();
		if(!file.exists()) {
        	try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        	saveDefaultConfig();
        	reloadConfig();
        	return;
        }
		try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
		config.set("fast-death", isRecentVersion()?false:true);
		config.set("hide-players-in-lobby-from-different-arenas", true);
        config.set("disabled-games", new ArrayList<String> () {{
        	add("CaseSensetive");
        	add("WhoAteAllTheDonuts");
        	add("ThisListDoesNotAffectOnPlaceholders");
        }});
        config.set("tnt", new HashMap<String, Object>() {{
        	put("enable-distance-detonation", true);
        	put("damage-placer", true);
        	put("detonation-ticks", 100);
        	put("dont-drop-after-breaking", true);
        	put("fuse-sound", org.screamingsandals.bedwars.Main.isLegacy() ? "FUSE":"ENTITY_TNT_PRIMED");
        	put("detonator", new HashMap<String, Object>() {{
        		put("name", "&eDetonator (Right-click in hand)");
        		put("item", Material.TRIPWIRE_HOOK.toString());
        		put("allow-drop", false);
        	}});
        }});
        config.set("placeholders", new HashMap<String, Object>() {{
        	put("waiting", "&aWaiting...");
        	put("starting", "&eStarting soon! Time left: %time%");
        	put("running", "&cRunning! Time left: %time%");
        	put("ended", "&9Game ended!");
        	put("rebuilding", "&7Rebuilding...");
        }});
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
	public FileConfiguration getConfigurator() {return this.getConfig();}
	
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(command.getName().equalsIgnoreCase("scbwa-reload")) {
    		if(sender.hasPermission("misat11.bw.admin") || sender.hasPermission("bw.admin")) {
    			saveDefaultConfig();
                reloadConfig();
    			sender.sendMessage("§aReloaded!");
    			return true;
    		} else {
    			sender.sendMessage("§cYou do not have permission bw.admin to execute this command!");
    		}
    	}
    	if(command.getName().equalsIgnoreCase("scbwa-debug")) {
    		if(sender.hasPermission("misat11.bw.admin") || sender.hasPermission("bw.admin")) {
    			sender.sendMessage(" ");
    			sender.sendMessage("§cSCBWAddon - §fversion §e"+this.getDescription().getVersion());
    			sender.sendMessage("§fConfig values:");
    			sender.sendMessage("  §efast-death: "+(this.getConfigurator().getBoolean("fast-death")?"§atrue":"§cfalse"));
    			sender.sendMessage("  §ehide-players-in-lobby-from-different-arenas: "+(this.getConfigurator().getBoolean("hide-players-in-lobby-from-different-arenas")?"§atrue":"§cfalse"));
    			sender.sendMessage("  §etnt:");
    			sender.sendMessage("    §6enable-distance-detonation: "+(this.getConfigurator().getBoolean("tnt.enable-distance-detonation")?"§atrue":"§cfalse"));
    			sender.sendMessage("    §6damage-placer: "+(this.getConfigurator().getBoolean("tnt.damage-placer")?"§atrue":"§cfalse"));
    			sender.sendMessage("    §6detonation-ticks: §r"+this.getConfigurator().getInt("tnt.detonation-ticks"));
    			sender.sendMessage("    §6fuse-sound: §r"+this.getConfigurator().getString("tnt.fuse-sound"));
    			sender.sendMessage("    §6detonator:");
    			sender.sendMessage("      §ename: §r"+this.getConfigurator().getString("tnt.detonator.name").replace("&", "§"));
    			sender.sendMessage("      §eitem: §r"+this.getConfigurator().getString("tnt.detonator.item"));
    			sender.sendMessage("      §eallow-drop: "+(this.getConfigurator().getBoolean("tnt.detonator.allow-drop")?"§atrue":"§cfalse"));
    			sender.sendMessage("  §eplaceholders:");
    			sender.sendMessage("      §6waiting: §r"+this.getConfigurator().getString("placeholders.waiting").replace("&", "§"));
    			sender.sendMessage("      §6starting: §r"+this.getConfigurator().getString("placeholders.starting").replace("&", "§"));
    			sender.sendMessage("      §6running: §r"+this.getConfigurator().getString("placeholders.running").replace("&", "§"));
    			sender.sendMessage("      §6ended: §r"+this.getConfigurator().getString("placeholders.ended").replace("&", "§"));
    			sender.sendMessage("      §6rebuilding: §r"+this.getConfigurator().getString("placeholders.rebuilding").replace("&", "§"));
    			sender.sendMessage(" ");
    			return true;
    		} else {
    			sender.sendMessage("§cYou do not have permission bw.admin to execute this command!");
    		}
    	}
		return false;
	}
    
    public boolean isRecentVersion() {
    	if (Bukkit.getVersion().contains("1.15")) return true;
    	else return false;
    }
}

