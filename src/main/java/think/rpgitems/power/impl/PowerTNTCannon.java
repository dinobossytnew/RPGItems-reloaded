/*
 *  This file is part of RPG Items.
 *
 *  RPG Items is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RPG Items is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RPG Items.  If not, see <http://www.gnu.org/licenses/>.
 */
package think.rpgitems.power.impl;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import think.rpgitems.I18n;
import think.rpgitems.commands.Property;
import think.rpgitems.power.PowerRightClick;

import static think.rpgitems.utils.PowerUtils.checkCooldown;

/**
 * Power tntcannon.
 * <p>
 * The tntcannon power will fire active tnt on right click.
 * </p>
 */
public class PowerTNTCannon extends BasePower implements PowerRightClick {

    /**
     * Cooldown time of this power
     */
    @Property(order = 0)
    public long cooldown = 20;
    /**
     * Cost of this power
     */
    @Property
    public int consumption = 0;

    @Override
    public void rightClick(Player player, ItemStack stack, Block block, PlayerInteractEvent event) {
        if (!checkCooldown(this, player, cooldown, true)) return;
        if (!getItem().consumeDurability(stack, consumption)) return;
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
        TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 1.8, 0), TNTPrimed.class);
        tnt.setVelocity(player.getLocation().getDirection().multiply(2d));
    }

    @Override
    public String displayText() {
        return I18n.format("power.tntcannon", (double) cooldown / 20d);
    }

    @Override
    public String getName() {
        return "tntcannon";
    }
}
