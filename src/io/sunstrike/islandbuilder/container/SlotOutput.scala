package io.sunstrike.islandbuilder.container

import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import io.sunstrike.islandbuilder.blocks.CrateAssemblerTile

/*
 * SlotOutput
 * io.sunstrike.islandbuilder.container
 */

/**
 * Output slot for Crate Assembler
 *
 * @author Sunstrike
 */
class SlotOutput(inventory:CrateAssemblerTile, slotId:Int, xPos:Int, yPos:Int) extends Slot(inventory, slotId, xPos, yPos) {

    override def getSlotStackLimit: Int = 1

    override def isItemValid(par1ItemStack: ItemStack): Boolean = false

    override def canTakeStack(par1EntityPlayer: EntityPlayer): Boolean = true

    override def onPickupFromSlot(par1EntityPlayer: EntityPlayer, par2ItemStack: ItemStack) {
        inventory.onPullCrate()
    }
}
