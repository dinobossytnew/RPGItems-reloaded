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

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import think.rpgitems.commands.BooleanChoice;
import think.rpgitems.commands.Property;
import think.rpgitems.power.PowerLeftClick;
import think.rpgitems.power.PowerRightClick;

import static think.rpgitems.utils.PowerUtils.AttachPermission;
import static think.rpgitems.utils.PowerUtils.checkCooldownByString;

/**
 * Power command.
 * <p>
 * The item will run {@link #command} on {@link #isRight click}
 * giving the permission {@link #permission} just for the use of the command.
 * </p>
 */
@SuppressWarnings("WeakerAccess")
public class PowerCommand extends BasePower implements PowerRightClick, PowerLeftClick {

    /**
     * Command to be executed
     */
    @Property(order = 4, required = true)
    public String command = "";
    /**
     * Display text of this power
     */
    @Property(order = 3)
    public String display = "Runs command";
    /**
     * Permission will be given to user executing the {@code command}
     */
    @Property(order = 8)
    public String permission = "";
    /**
     * Whether triggers when right click
     */
    @Property(order = 2)
    @BooleanChoice(name = "mouse", falseChoice = "left", trueChoice = "right")
    public boolean isRight = true;
    /**
     * Cooldown time of this power
     */
    @Property(order = 1)
    public long cooldown = 20;
    /**
     * Cost of this power
     */
    @Property
    public int consumption = 0;

    /**
     * Execute command
     *
     * @param player player
     */
    protected void executeCommand(Player player) {
        if (!player.isOnline()) return;

        AttachPermission(player, permission);
        boolean wasOp = player.isOp();

        Runnable run = () -> {
            String cmd = command;
            cmd = cmd.replaceAll("\\{player}", player.getName());
            cmd = cmd.replaceAll("\\{yaw}", Float.toString(player.getLocation().getYaw() + 90));
            cmd = cmd.replaceAll("\\{pitch}", Float.toString(-player.getLocation().getPitch()));
            player.chat("/" + cmd);
        };

        if (permission.equals("*")) {
            try {
                player.setOp(true);
                run.run();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!wasOp) {
                    player.setOp(false);
                }
            }
        } else {
            run.run();
        }
    }

    @Override
    public void rightClick(Player player, ItemStack stack, Block clicked, PlayerInteractEvent event) {
        if (!isRight || !checkCooldownByString(player, getItem(), command, cooldown, true)) return;
        if (!getItem().consumeDurability(stack, consumption)) return;
        executeCommand(player);
    }

    @Override
    public void leftClick(Player player, ItemStack stack, Block clicked, PlayerInteractEvent event) {
        if (isRight || !checkCooldownByString(player, getItem(), command, cooldown, true)) return;
        if (!getItem().consumeDurability(stack, consumption)) return;
        executeCommand(player);
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + display;
    }

    @Override
    public String getName() {
        return "command";
    }
}
