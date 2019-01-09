package com.gmail.eduardokanp.nextsky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class MySQL {
	
	private Main main;
	
	private Logger Log = Bukkit.getLogger();
	
	private Connection conexion;
	
	public MySQL(String ip, String userName, String password, String db) {
		
		try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + db + "?user=" + userName + "&password=" + password);
        } catch (Exception e) {
            Log.severe("Ha ocurrido un error: " + e);
            main.desactivarPlugin();
            Log.severe("Desactivando este plugin");
            
        }
		
	}
	
	public String proximosv() {
        try {
                PreparedStatement statement = conexion.prepareStatement("select * from SkyWars ORDER BY `SkyWars`.`ESTADO` ASC LIMIT 1");
                ResultSet result = statement.executeQuery();
                /*String stas = result.getString("ESTADO");
                int stan = Integer.parseInt(stas);
                if(stan == 0)
                {
                	Bukkit.getLogger().info("Estado 0");
                	String plys = result.getString("JUGADORES");
                	int plyn = Integer.parseInt(plys);
                	if(plyn < 12) {
                		Bukkit.getLogger().info("Hay menos de 12 jugadores");
                		return result.getString("ID");
                	} else { 
                		Bukkit.getLogger().info("Hay mas de 12 jugadores");
                		return null;
                		}
                	
                } else {
                	Bukkit.getLogger().info("Estado no es 0");
                	return null;
                }*/
                if (result.next()) {
                	String stas = result.getString("ESTADO");
                	int stan = Integer.parseInt(stas);
                	if (stan == 0) {
                		String plys = result.getString("JUGADORES");
                		int plyn = Integer.parseInt(plys);
                		if (plyn < 12){
                			return result.getString("ID");
                		} else {
                			return null;
                		}
                	}
                } else {
                        return null;
                }
   
        } catch (Exception e) {
                e.printStackTrace();
                return ChatColor.RED + "Ha ocurrido un error" + e;
        }
		return null;
    }

}
