package org.nfn11.papi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion{

	org.nfn11.main.Main plugin;
    public PlaceholderAPIHook(org.nfn11.main.Main plugin) 
    {this.plugin = plugin;}
    
	@Override
	public String getAuthor() {return "notfoundname11";}

	@Override
	public String getIdentifier() {return "scbwa";}

	@Override
	public String getVersion() {return "1.0";}

	@Override
	public String onPlaceholderRequest(Player player, String parsed) {
		//if player does not exist, so it will return nothing
		if(player == null) return "";
		
		// === Information about specific arena ===
		// %scbwa_NAME_count_game%
		if(parsed.endsWith("_count_game")) {
			parsed = parsed.replace("_count_game", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "";
			else {
				GameStatus status = BedwarsAPI.getInstance().getGameByName(parsed).getStatus();
				if(status==GameStatus.RUNNING || status==GameStatus.GAME_END_CELEBRATING) 
        			return Integer.toString(BedwarsAPI.getInstance().getGameByName(parsed).countConnectedPlayers());
        		else return "0";
			}
		}
		
		// %scbwa_NAME_state%
		if(parsed.endsWith("_state")) {
			parsed = parsed.replace("_state", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "Game does not exist";
			GameStatus status = Main.getGame(parsed).getStatus();
			if(status==GameStatus.WAITING && BedwarsAPI.getInstance().getGameByName(parsed).getMinPlayers()
					>= BedwarsAPI.getInstance().getGameByName(parsed).countConnectedPlayers()) {
				return plugin.getConfigurator().getString("placeholders.waiting").replace("&", "§");
			}
			if(status==GameStatus.RUNNING) {
				return plugin.getConfigurator().getString("placeholders.running").replace("&", "§").replace("%time%", Main.getGame(parsed).getFormattedTimeLeft());
			}
			if(status==GameStatus.WAITING 
					&& BedwarsAPI.getInstance().getGameByName(parsed).getMinPlayers()
					<= BedwarsAPI.getInstance().getGameByName(parsed).countConnectedPlayers()) {
				return plugin.getConfigurator().getString("placeholders.starting").replace("&", "§").replace("%time%", Main.getGame(parsed).getFormattedTimeLeft());
			}
			if(status==GameStatus.GAME_END_CELEBRATING) {
				return plugin.getConfigurator().getString("placeholders.ended").replace("&", "§");
			}
			if(status==GameStatus.REBUILDING) {
				return plugin.getConfigurator().getString("placeholders.rebuilding").replace("&", "§");
			}
    		else return "none";
			
		}
		
		// %scbwa_NAME_count_lobby%
		if(parsed.endsWith("_count_lobby")) {
			parsed = parsed.replace("_count_lobby", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "";
			else {
				if(BedwarsAPI.getInstance().getGameByName(parsed).getStatus()==GameStatus.WAITING) 
        			return Integer.toString(BedwarsAPI.getInstance().getGameByName(parsed).countConnectedPlayers());
        		else return "0";
			}
		}
		
		// %scbwa_NAME_maxplayers%
		if(parsed.endsWith("_maxplayers")) {
			parsed = parsed.replace("_maxplayers", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "";
			else return Integer.toString(BedwarsAPI.getInstance().getGameByName(parsed).getMaxPlayers());
		}
		
		// %scbwa_NAME_minplayers%
		if(parsed.endsWith("_minplayers")) {
			parsed = parsed.replace("_minplayers", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "";
			else return Integer.toString(BedwarsAPI.getInstance().getGameByName(parsed).getMinPlayers());
		}
		
		// %scbwa_NAME_time%
		if(parsed.endsWith("_time")) {
			parsed = parsed.replace("_time", "");
			if(!BedwarsAPI.getInstance().isGameWithNameExists(parsed)) return "";
			else return Main.getGame(parsed).getFormattedTimeLeft();
		}
		
		// %scbwa_color% set this as prefix and it will color player's name if he is playing a game and he choosed some team
		if(parsed.equals("color")) {
        	if (BedwarsAPI.getInstance().isPlayerPlayingAnyGame(player)) {
        		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        		Game game = gPlayer.getGame();
        		if (gPlayer.isSpectator) {
        			return ChatColor.GRAY + "";
                } else {
                    CurrentTeam team = game.getPlayerTeam(gPlayer);
                    if (team != null) {
                        return team.teamInfo.color.chatColor + "";
                    } else return ChatColor.RESET + "";
                }
        	}
        	else return ChatColor.RESET + "";
		}
		
		// %scbwa_time% returns time left (works in lobby too!)
		if(parsed.equals("time")) {
			if(Main.isPlayerInGame(player)) 
				return Main.getPlayerGameProfile(player).getGame().getFormattedTimeLeft();
			else return "0";
		}
		
		//Other player's stats
		//%scbwa_PLAYER_stats_<something>%
		if(parsed.endsWith("_stats_kills")) {
			parsed = parsed.replace("_stats_kills", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentKills() + stats.getKills());
			}
		}
		if(parsed.endsWith("_stats_deaths")) {
			parsed = parsed.replace("_stats_deaths", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentDeaths() + stats.getDeaths());
			}
		}
		
		if(parsed.endsWith("_stats_destroyed_beds")) {
			parsed = parsed.replace("_stats_destroyed_beds", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentDestroyedBeds() + stats.getDestroyedBeds());
			}
		}
		
		if(parsed.endsWith("_stats_loses")) {
			parsed = parsed.replace("_stats_loses", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentLoses() + stats.getLoses());
			}
		}
		
		if(parsed.endsWith("_stats_score")) {
			parsed = parsed.replace("_stats_score", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentScore() + stats.getScore());
			}
		}
		
		if(parsed.endsWith("_stats_wins")) {
			parsed = parsed.replace("_stats_wins", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentWins() + stats.getWins());
			}
		}
		
		if(parsed.endsWith("_stats_games")) {
			parsed = parsed.replace("_stats_games", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Integer.toString(stats.getCurrentGames() + stats.getGames());
			}
		}
		
		if(parsed.endsWith("_stats_kd")) {
			parsed = parsed.replace("_stats_loses", "");
			if(!Main.isPlayerGameProfileRegistered(Bukkit.getPlayer(parsed))) return "Player does not exist";
			else {
				PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getPlayer(parsed));
				return Double.toString(stats.getCurrentKD());
			}
		}
		
		return null;
	}
}
