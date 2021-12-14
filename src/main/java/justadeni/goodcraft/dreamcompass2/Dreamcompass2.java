package justadeni.goodcraft.dreamcompass2;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dreamcompass2 extends JavaPlugin implements Listener {

    private static Dreamcompass2 instance;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getServer().getPluginManager().registerEvents(new Methods(), this);
        instance = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    boolean TrackPortal = getConfig().getBoolean("TrackPortal");
    boolean PrintY = getConfig().getBoolean("PrintY");

    public static Dreamcompass2 getInstance() {
        return instance;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("compass")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("dreamcompass.use")) {
                    if (args.length == 0) {
                        player.sendMessage(Methods.colorThis("&c&lUse /compass <player>"));
                    } else if (args.length == 1) {
                        if (args[0].equals("reload")){
                            reloadConfig();
                            saveConfig();
                            player.sendMessage(Methods.colorThis("&2&lConfig reloaded"));
                        } else {
                            Player target = Bukkit.getPlayer(args[0]);
                            if (target == null) {
                                player.sendMessage(Methods.colorThis("&c&lNo such player found"));
                            } else {
                                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), Methods.NewCompass(target));
                                } else {
                                    player.sendMessage(Methods.colorThis("&c&lYour hand needs to be empty"));
                                }
                            }
                        }
                    } else {
                        player.sendMessage(Methods.colorThis("&c&lWrong arguments"));
                    }
                } else {
                    player.sendMessage(Methods.colorThis("&c&lYou dont have permission for that"));
                }
            } else {
                sender.sendMessage(Methods.colorThis("&c&lOnly player can use this command"));
            }
        }
        return false;
    }
}
