package org.nfn11.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.BedwarsAPI;

public class TNT implements Listener {
	
	org.nfn11.main.Main plugin;
    public TNT(org.nfn11.main.Main plugin) 
    {this.plugin = plugin;}
    
    final List<Location> locations = new ArrayList<Location>();
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
    	BedwarsAPI api = BedwarsAPI.getInstance();
    	if(!api.isPlayerPlayingAnyGame(e.getPlayer())) return;
    	if(!plugin.getConfigurator().getBoolean("tnt.enable-distance-detonation")) return;
    	if(plugin.getConfigurator().getList("disabled-games").contains(api.getGameOfPlayer(e.getPlayer()).getName())) return;
    	if(Main.getConfigurator().config.getBoolean("tnt.auto-ignite")) {
    		Bukkit.getLogger().warning("TNT auto inginition is actually enabled. Please disable because it can make some issues (I think).");
    		return;
    	}
    	if(e.getBlockPlaced().getType().equals(Material.TNT)) {
    		Block tnt = e.getBlockPlaced();
    		tnt.setMetadata(e.getPlayer().getName(), new FixedMetadataValue(plugin, "da"));
    		locations.add(tnt.getLocation());
    		if(!e.getPlayer().getInventory().contains(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item"))) && e.getPlayer().getInventory().firstEmpty() != -1) {
    			ItemStack detonator = new ItemStack(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item")));
    			ItemMeta meta = detonator.getItemMeta();
    			meta.setDisplayName(plugin.getConfigurator().getString("tnt.detonator.name").replace("&", "§"));
    			detonator.setItemMeta(meta);
    			e.getPlayer().getInventory().addItem(detonator);
    		}
    	}
		if(e.getBlockPlaced().getType().equals(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item")))) e.setCancelled(true);
		
    }
	@EventHandler
    public void onBlowUp(PlayerInteractEvent e) {
    	BedwarsAPI api = BedwarsAPI.getInstance();
    	if(!api.isPlayerPlayingAnyGame(e.getPlayer())) return;
    	if(!plugin.getConfigurator().getBoolean("tnt.enable-distance-detonation")) return;
    	if(plugin.getConfigurator().getList("disabled-games").contains(api.getGameOfPlayer(e.getPlayer()).getName())) return;
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getItem() == null) return;
			if(e.getItem().getType().equals(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item")))) {
        		for(Location loc : locations) {
        			if(loc.getBlock().getType().equals(Material.TNT) && loc.getBlock().hasMetadata(e.getPlayer().getName())) {
        				e.setCancelled(true);
        				loc.getBlock().setType(Material.AIR);
        				TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawn(loc.getBlock().getLocation().add(0.5, 0.0, 0.5), TNTPrimed.class);
        				api.registerEntityToGame(tnt, api.getGameOfPlayer(e.getPlayer()));
        				tnt.setMetadata(e.getPlayer().getName(), new FixedMetadataValue(plugin, "da"));
        				for(Player all : api.getGameOfPlayer(e.getPlayer()).getConnectedPlayers()) {
        					all.playSound(tnt.getLocation(), Sound.valueOf(plugin.getConfigurator().getString("tnt.fuse-sound")), 1.0f, 1.0f);
        				}
        				tnt.setFuseTicks(plugin.getConfigurator().getInt("tnt.detonation-ticks"));
        				e.getPlayer().getInventory().remove(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item")));
        			}
        		}
        	}
		}
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
    	if(e.getItemDrop()==null) return;
    	BedwarsAPI api = BedwarsAPI.getInstance();
    	if(!api.isPlayerPlayingAnyGame(e.getPlayer())) return;
    	if(!plugin.getConfigurator().getBoolean("tnt.enable-distance-detonation")) return;
    	if(plugin.getConfigurator().getList("disabled-games").contains(api.getGameOfPlayer(e.getPlayer()).getName())) return;
    	if(plugin.getConfigurator().getBoolean("tnt.detonator.allow-drop")) return;
    	if(e.getItemDrop().getItemStack().getType().equals(Material.valueOf(plugin.getConfigurator().getString("tnt.detonator.item")))) e.setCancelled(true);
    }
    
    @EventHandler
    public void onTntDamage(EntityDamageByEntityEvent e) {
    	if(e.getEntity() instanceof Player) {
    		Player p = (Player) e.getEntity();
    		if(!BedwarsAPI.getInstance().isPlayerPlayingAnyGame(p)) return;
    		if(plugin.getConfigurator().getBoolean("tnt.damage-placer")) return;
        	if(e.getDamager() instanceof TNTPrimed && e.getDamager().hasMetadata(p.getName())) e.setCancelled(true);
    	}
    }
}
