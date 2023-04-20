package sh.evan.oauth42.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import sh.evan.oauth42.Oauth42;

public class PlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        event.setFormat("%s (" + Oauth42.getInstance().getPlayerIntraUser().get(event.getPlayer()) + "): %s");
    }
}
