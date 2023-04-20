package sh.evan.oauth42.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import sh.evan.oauth42.Oauth42;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Oauth42.getInstance().getPlayerIntraUser().remove(event.getPlayer());
    }
}
