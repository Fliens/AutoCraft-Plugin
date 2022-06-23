/*  AutoCraft plugin
 *
 *  Copyright (C) 2021 Fliens
 *  Copyright (C) 2021 MrTransistor
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fliens.autocraft;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static fliens.autocraft.AutoCraft.collectAutoCrafters;

public class EventListener implements Listener {

    public EventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void checkProtection(PlayerInteractEvent e){
        if(e.getClickedBlock() == null) return;

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(e.getClickedBlock().getLocation(), true, null);
        if(claim == null)
            return;

        if(!(claim.checkPermission(e.getPlayer(), ClaimPermission.Inventory, null) != null ||
                claim.checkPermission(e.getPlayer(), ClaimPermission.Access, null) != null))
            e.setCancelled(true);


    }

    @EventHandler
    public void onItemMove(BlockDispenseEvent e) {
        if (e.getBlock().getType().equals(Material.DISPENSER)) {
            List<Block> autoCrafters = collectAutoCrafters();
            if (autoCrafters.contains(e.getBlock())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHopperDropperItemMove(InventoryMoveItemEvent e) {
        if (AutoCraft.allowBlockRecipeModification) return;

        Inventory initiatior = e.getInitiator();

        if (initiatior.getType() == InventoryType.HOPPER || initiatior.getType() == InventoryType.DROPPER) {
            Inventory source = e.getSource();
            Inventory destination = e.getDestination();
            if (source.getType() == InventoryType.DISPENSER && AutoCraft.autoCrafters.contains(source.getLocation().getBlock()))
                e.setCancelled(true);
            else if (destination.getType() == InventoryType.DISPENSER && AutoCraft.autoCrafters.contains(destination.getLocation().getBlock()))
                e.setCancelled(true);
        }
    }
}
