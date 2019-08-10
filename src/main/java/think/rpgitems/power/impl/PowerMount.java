package think.rpgitems.power.impl;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import think.rpgitems.I18n;
import think.rpgitems.RPGItems;
import think.rpgitems.power.*;

import java.util.List;

import static think.rpgitems.power.Utils.*;

@PowerMeta(immutableTrigger = true, withSelectors = true, implClass = PowerMount.Impl.class)
public class PowerMount extends BasePower {
    @Property(order = 0)
    private long cooldown = 0L;
    @Property(order = 1)
    private int maxDistance = 5;
    @Property(order = 2)
    private int maxTicks = 200;

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    @Override
    public String getName() {
        return "mount";
    }

    @Override
    public String displayText() {
        return I18n.format("power.mount", (double) getCooldown() / 20D);
    }

    public long getCooldown() {
        return cooldown;
    }

    public class Impl implements PowerRightClick {

        @Override
        public PowerResult<Void> rightClick(Player player, ItemStack stack, PlayerInteractEvent event) {
            if (!checkCooldown(getPower(), player, getCooldown(), true, true)) return PowerResult.cd();
            if (player.isInsideVehicle()) {
                return PowerResult.fail();
            }
            List<LivingEntity> entities = getLivingEntitiesInCone(getNearestLivingEntities(getPower(), player.getEyeLocation(), player, getMaxDistance(), 0), player.getLocation().toVector(), 30, player.getLocation().getDirection());
            for (LivingEntity entity : entities) {
                if (entity.isValid() && entity.getType() != EntityType.ARMOR_STAND && !entity.isInsideVehicle() &&
                            entity.getPassengers().isEmpty() && player.hasLineOfSight(entity) && entity.addPassenger(player)) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_ARMOR, 1.0F, 1.0F);
                    Listener listener = new Listener() {
                        @EventHandler
                        public void onPlayerQuit(PlayerQuitEvent e) {
                            if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                                entity.removePassenger(player);
                                player.leaveVehicle();
                            }
                        }
                    };
                    Bukkit.getPluginManager().registerEvents(listener, RPGItems.plugin);
                    new BukkitRunnable() {
                        private long ticks = 0L;

                        @Override
                        public void run() {
                            if (ticks >= getMaxTicks() || entity.isDead() || entity.getPassengers().isEmpty() || player.isDead()) {
                                cancel();
                                HandlerList.unregisterAll(listener);
                                if (!entity.isDead() && !entity.getPassengers().isEmpty()) {
                                    entity.removePassenger(player);
                                    player.leaveVehicle();
                                }
                            }
                            ticks++;
                        }
                    }.runTaskTimer(RPGItems.plugin, 1, 1);
                    return PowerResult.ok();
                }
            }

            return PowerResult.fail();
        }

        @Override
        public Power getPower() {
            return PowerMount.this;
        }
    }
}
