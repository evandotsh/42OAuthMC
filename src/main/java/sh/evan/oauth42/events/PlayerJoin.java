package sh.evan.oauth42.events;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sh.evan.oauth42.Oauth42;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        String url = "https://42mc.evan.sh/" + username;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // player is allowed to login
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String jsonResponse = reader.readLine();
                reader.close();
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
                String intraUser = json.get("intra_account").getAsString();
                System.out.println("Player " + player.getName() + " logged in (intra account: " + intraUser + ")");
                event.setJoinMessage(ChatColor.YELLOW + player.getName() + " (" + intraUser + ") joined the game.");
                Oauth42.getInstance().getPlayerIntraUser().put(event.getPlayer(), intraUser);
            } else if (responseCode == 401) {
                String loginLink = url + "/link";
                player.kickPlayer(ChatColor.RED + "You need to link your 42 account before joining this server:\n"
                        + loginLink);
            } else {
                player.kickPlayer(ChatColor.RED + "Server returned an incorrect HTTP code (" + responseCode + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
            player.kickPlayer(ChatColor.RED + "Please try again :)");
        }
    }
}
