package think.rpgitems.power.impl;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import think.rpgitems.I18n;
import think.rpgitems.commands.Property;
import think.rpgitems.power.PowerHit;

public class PowerAirborne extends BasePower implements PowerHit {

    @Property
    public int percentage = 50;

    @Property
    public double cap = 300.0;

    @Override
    public String getName() {
        return "airborne";
    }

    @Override
    public String displayText() {
        return I18n.format("power.airborne", percentage);
    }


    @Override
    public void hit(Player player, ItemStack stack, LivingEntity entity, double damage, EntityDamageByEntityEvent event) {
        if (!player.isGliding())
            return;
        double originDamage = damage;
        damage = damage * (1 + percentage / 100.0);
        damage = Math.max(Math.min(damage, cap), originDamage);
        event.setDamage(damage);
        if (damage > originDamage) {
            player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.1f, 0.1f);
        }
    }
}
