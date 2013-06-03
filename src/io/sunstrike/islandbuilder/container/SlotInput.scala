package io.sunstrike.islandbuilder.container

import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import io.sunstrike.islandbuilder.helpers.ObjRepo._

/*
 * SlotInput
 * io.sunstrike.islandbuilder.container
 */

/**
 * Input slot for Crate Assembler
 *
 * @author Sunstrike
 */
abstract class SlotInput(inventory:IInventory, slotId:Int, xPos:Int, yPos:Int) extends Slot(inventory, slotId, xPos, yPos) {

}

class SlotModuleInput(inventory:IInventory, slotId:Int, xPos:Int, yPos:Int, meta:Int) extends SlotInput(inventory, slotId, xPos, yPos) {

    override def isItemValid(itemStack: ItemStack): Boolean = {
        val item = itemStack.getItem
        val damage = itemStack.getItemDamage
        if (item == item_FlIsModule && damage == meta) true else false
    }

}

class SlotCrateInput(inventory:IInventory, slotId:Int, xPos:Int, yPos:Int) extends SlotInput(inventory, slotId, xPos, yPos) {

    override def isItemValid(itemStack:ItemStack): Boolean = {
        val item = itemStack.getItem.itemID
        item == new ItemStack(block_EmptyCrate).getItem.itemID
    }

}