package sh.evan.oauth42;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import sh.evan.oauth42.events.PlayerChat;
import sh.evan.oauth42.events.PlayerJoin;
import sh.evan.oauth42.events.PlayerQuit;

import java.util.HashMap;

public class Oauth42 extends JavaPlugin implements Listener {

    private final HashMap<Player, String> playerIntraUser = new HashMap<>();
    private static Oauth42 instance;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public HashMap<Player, String> getPlayerIntraUser() {
        return playerIntraUser;
    }

    public static Oauth42 getInstance() {
        return instance;
    }
}
