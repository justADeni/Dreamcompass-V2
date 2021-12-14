package justadeni.goodcraft.dreamcompass2;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;


public class Methods implements Listener {

    //private Dreamcompass2 main = Dreamcompass2.getInstance();

    HashMap<Player,Location> PortalMap = new HashMap<>();



    public static String colorThis(String text){
        return (ChatColor.translateAlternateColorCodes('&',text));
    }

    public static ItemStack NewCompass(Player target){
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        ArrayList<String> list = new ArrayList<>();
        list.add(colorThis("&6target: &3" + target.getName()));
        meta.setLore(list);
        meta.addEnchant(Enchantment.LUCK,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        compass.setItemMeta(meta);

        return compass;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if (e.getPlayer().hasPermission("dreamcompass.use")) {
            try {
                if (e.getItem().getType().equals(Material.COMPASS)) {
                    ItemMeta meta = e.getItem().getItemMeta();
                    if (meta.hasEnchant(Enchantment.LUCK)) {
                        if (!meta.getLore().isEmpty()) {
                            String lore = meta.getLore().get(0);
                            lore = ChatColor.stripColor(lore);
                            String[] list = lore.split(" ");
                            Player target = Bukkit.getServer().getPlayer(list[1]);
                            if (Dreamcompass2.getInstance().getServer().getOnlinePlayers().contains(target)) {
                                if (e.getPlayer().getWorld().equals(target.getWorld())) {
                                    //Different method depending on world
                                    if (e.getPlayer().getWorld().getName().equals("world")) {
                                        CompassMeta compass = (CompassMeta) e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
                                        if (compass.hasLodestone()){
                                            e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), Methods.NewCompass(target));
                                        }
                                        e.getPlayer().setCompassTarget(target.getLocation());
                                    } else {
                                        //We create a Compass from the Item
                                        CompassMeta compass = (CompassMeta) e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
                                        //In the nether, the compass can only point to a lodestone, unlike in the overworld where it can point to any coordinate.
                                        compass.setLodestone(target.getLocation());
                                        compass.setLodestoneTracked(false);
                                        //Change the meta to the new meta
                                        e.getPlayer().getInventory().getItemInMainHand().setItemMeta(compass);

                                        //Snippet copied from https://github.com/DusanS13/TrackerCompass/blob/main/src/me/Allt/Tracker/Events.java
                                    }

                                    //Depending on config, action bar or chat only
                                    if (Dreamcompass2.getInstance().PrintY) {
                                        e.getPlayer().sendMessage(colorThis("&6Pointing to &3&l" + target.getName()));
                                        String targetY = colorThis("&6target Y: &3&l" + Math.floor(target.getLocation().getY()));
                                        //targetY = targetY.replaceAll(".0","");
                                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(targetY));
                                    } else {
                                        e.getPlayer().sendMessage(colorThis("&6Pointing to &3&l" + target.getName()));
                                    }
                                } else {
                                    if (PortalMap.containsKey(target)) {
                                        if (Dreamcompass2.getInstance().TrackPortal) {
                                            e.getPlayer().setCompassTarget(PortalMap.get(target));
                                            e.getPlayer().sendMessage(colorThis("&6Pointing to &3&l" + target.getName() + " &r&6portal"));
                                        }
                                    }
                                }
                            } else {
                                e.getPlayer().sendMessage(colorThis("&c&lPlayer not found"));
                            }
                        }
                    }
                }
            } catch (NullPointerException oof){

            }
        }
    }

    @EventHandler
    public void onNether(PlayerPortalEvent e){
        Location portalLoc = e.getPlayer().getLocation();
        String worldBefore = e.getPlayer().getWorld().getName();

        new BukkitRunnable(){
            @Override
            public void run(){
                if (!worldBefore.equals(e.getPlayer().getWorld().getName())) {
                    PortalMap.put(e.getPlayer(), portalLoc);
                }
            }
        }.runTaskLaterAsynchronously(Dreamcompass2.getInstance(), 200);
    }

}