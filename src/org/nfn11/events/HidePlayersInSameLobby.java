package org.nfn11.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;

public class HidePlayersInSameLobby implements Listener {
	
	org.nfn11.main.Main plugin;
    public HidePlayersInSameLobby(org.nfn11.main.Main plugin) 
    {this.plugin = plugin;}
	
	@EventHandler
	public void onJoin(BedwarsPlayerJoinEvent e) {
		if(!plugin.getConfigurator().getBoolean("hide-players-in-lobby-from-different-arenas")) return;
		if(plugin.getConfigurator().getList("disabled-games").contains(e.getGame().getName())) return;
		if(e.getGame().getStatus()==GameStatus.WAITING) {
			for(Player insameworld : e.getGame().getLobbyWorld().getPlayers()) {
				insameworld.hidePlayer(e.getPlayer());
				e.getPlayer().hidePlayer(insameworld);
			}
			for(Player insamegame : e.getGame().getConnectedPlayers()) {
				insamegame.showPlayer(e.getPlayer());
				e.getPlayer().showPlayer(insamegame);
			}
		}
	}
	
	@EventHandler
	public void onJoin(BedwarsPlayerLeaveEvent e) {
		if(!plugin.getConfigurator().getBoolean("hide-players-in-lobby-from-different-arenas")) return;
		if(plugin.getConfigurator().getList("disabled-games").contains(e.getGame().getName())) return;
		
		for(Player inworld : e.getPlayer().getWorld().getPlayers()) {
			inworld.showPlayer(e.getPlayer());
			e.getPlayer().showPlayer(inworld);
		}
		for(Player ingame : e.getGame().getConnectedPlayers()) {
			ingame.hidePlayer(e.getPlayer());
			e.getPlayer().hidePlayer(ingame);
		}
	}
}

