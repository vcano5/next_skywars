package com.gmail.eduardokanp.nextsky;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;


public class Main extends JavaPlugin implements Listener {
	
	private MySQL mysql;
	
	private Logger Log = Bukkit.getLogger();
	String host = "192.168.1.67";
	String us = "test";
	String pw = "test";
	String db = "Servidores";

	String prefix = ChatColor.AQUA + "[SkyWars] " + ChatColor.WHITE;
	String eprefix = ChatColor.AQUA + "[SkyWars] " + ChatColor.RED;
	String plogo = ChatColor.AQUA + "[SkyWars] ";
	
	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		Boolean depen = Bukkit.getServer().getPluginManager().isPluginEnabled("SkyWarsReloaded");
		if(depen == false){
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			Log.severe("No se han encontrado las dependencias");
			Log.severe("Desactivando este plugin");
		} else {
		Log.info("Se ha encontrado SkyWarsReloaded, iniciando");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Log.info(plogo + "se ha activado correctamente");
		Log.info(plogo + "Escrito por EduardoKanp");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		mysql = new MySQL(host, us, pw, db);
		}
	}
	
	@Override
	public void onDisable() {

     }

	
	public ItemStack papel() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.AQUA + "Jugar de nuevo " + ChatColor.GRAY + "(Click derecho)");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Click derecho para ir a otro mapa");
		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);
		return item;
	}
	
	@EventHandler
	public void alRespawnear(PlayerRespawnEvent e) {
		final Player player = e.getPlayer();		
		final GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if(gPlayer.isSpectating()) {
		
		Timer yourtimer = new Timer(true);
		yourtimer.schedule(new TimerTask()
			{
		    	@Override
		    	public void run()
		    		{
		    			/*player.getInventory().addItem(papel());*/
		    			player.getInventory().setItem(8, papel());
		    			player.sendMessage(prefix + "Usa el mapa para ir a otro juego rapidamente");
		    		}
			}, 1000);
		}
	}
	
	@EventHandler
	public void onClickMapa(PlayerInteractEvent e) {
		Action action = e.getAction();
		Player p = e.getPlayer();
		if(p.getItemInHand().equals(papel())){
			 if(action == Action.RIGHT_CLICK_AIR || action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
					String server = mysql.proximosv();
					if (server == null) {
						p.sendMessage(eprefix + "Ha ocurrido un error por favor intentalo de nuevo");
					} else { 
					p.sendMessage(ChatColor.GREEN + "Enviandote a " + server);
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
		            out.writeUTF("Connect");
		            out.writeUTF("sk_" + server); 
		            p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
				}
			}
		}
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		final GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
	    if (gPlayer.isSpectating()) {
	    	e.setCancelled(true);
	    }
	}
	
	public void desactivarPlugin() {
		Bukkit.getServer().getPluginManager().disablePlugin(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("setid"))
		{
			if(sender.hasPermission("SkyWars.editarID"))
			{
			
				if(args.length == 0) {
					sender.sendMessage(eprefix + "Debes de especificar un ID");
				} else {
					String id = args[0];
                    getConfig().set("ID", id);
                    saveConfig();
                    sender.sendMessage(prefix + "ID configurado a: " + id);
                    return true;
					
					
				}
			} else
			{
				sender.sendMessage(eprefix + "Tu rango debe de ser dev o superior");
			
			}
		}
		return false;
	}
}
