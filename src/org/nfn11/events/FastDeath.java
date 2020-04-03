package org.nfn11.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;

public class FastDeath implements Listener 
{
	org.nfn11.main.Main plugin;
    public FastDeath(org.nfn11.main.Main plugin) 
    {this.plugin = plugin;}
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(BedwarsPlayerKilledEvent event) 
    {
    	BedwarsAPI api = BedwarsAPI.getInstance();
    	Player player = event.getPlayer();
    	if(!api.isPlayerPlayingAnyGame(player)) return; 
    	if(!plugin.getConfigurator().getBoolean("fast-death")
    			|| plugin.getConfigurator().getList("disabled-games").contains(api.getGameOfPlayer(player))) return; 		
    	player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setSaturation(20);
        player.setFallDistance(0);
        player.closeInventory();
        if(!event.getGame().getOriginalOrInheritedKeepInventory()
        		&& !event.getGame().getOriginalOrInheritedPlayerDrops()) {
        	player.getInventory().setHeldItemSlot(0);
        	player.getInventory().clear();
        	player.getInventory().setArmorContents(null);
        }
        player.setLevel(player.getLevel() / 3);
        if(event.getKiller() != null) {
        	event.getKiller().setLevel(player.getLevel() + event.getKiller().getLevel());
        }
        for (PotionEffect effect : player.getActivePotionEffects())  {
            player.removePotionEffect(effect.getType());
        }
        if(!Main.getPlayerGameProfile(player).isSpectator || player.getGameMode()==GameMode.SPECTATOR) {
        	Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(player, api.getGameOfPlayer(player).getTeamOfPlayer(player).getTeamSpawn(), false));
        } else {
        	Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(player, api.getGameOfPlayer(player).getSpectatorSpawn(), false));
        }
	}
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
    	BedwarsAPI api = BedwarsAPI.getInstance();
    	Player player = event.getPlayer();
    	if(!api.isPlayerPlayingAnyGame(player)) return; 
    	if(!plugin.getConfigurator().getBoolean("fast-death")
    			|| plugin.getConfigurator().getList("disabled-games").contains(api.getGameOfPlayer(player))) return; 	
    	if(!Main.getPlayerGameProfile(player).isSpectator || player.getGameMode()==GameMode.SPECTATOR) {
        	Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
      		  	@Override
      		  	public void run() {
      		  		event.getPlayer().teleport(api.getGameOfPlayer(player).getTeamOfPlayer(player).getTeamSpawn());
      		  	} }, 2L);
    	} else {
        	Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
      		  	@Override
      		  	public void run() {
      		  		event.getPlayer().teleport(api.getGameOfPlayer(player).getSpectatorSpawn());
      		  	} }, 2L);
    	}
    }
}
