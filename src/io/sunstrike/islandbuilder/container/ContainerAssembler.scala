package io.sunstrike.islandbuilder.container

import net.minecraft.inventory.{Slot, Container}
import net.minecraft.entity.player.{InventoryPlayer, EntityPlayer}
import net.minecraft.item.ItemStack
import io.sunstrike.islandbuilder.blocks.CrateAssemblerTile
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import cpw.mods.fml.common.network.Player

/*
 * ContainerAssembler
 * io.sunstrike.islandbuilder.container
 */

/**
 * Crate Assembler - Container
 *
 * Inherited from Forge docs.
 *
 * @author Sunstrike
 */
class ContainerAssembler(playerInventory:InventoryPlayer, te:CrateAssemblerTile) extends Container {

    val tile = te

    addSlotToContainer(new SlotModuleInput(tile, 0, 9, 17, 0))
    addSlotToContainer(new SlotModuleInput(tile, 1, 9, 35, 1))
    addSlotToContainer(new SlotModuleInput(tile, 2, 9, 53, 2))
    addSlotToContainer(new SlotCrateInput(tile, 3, 48, 17))
    addSlotToContainer(new SlotOutput(tile, 4, 48, 53))

    bindPlayerInventory(playerInventory)

    def canInteractWith(entityplayer: EntityPlayer): Boolean = true

    // Vanilla boilerplate (imports player inv)
    protected def bindPlayerInventory(inventoryPlayer:InventoryPlayer) {
        for (i <- 0 to 2) {
            for (j <- 0 to 8) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (i <- 0 to 8) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142))
        }
    }

    override def transferStackInSlot(player:EntityPlayer, slot:Int): ItemStack = {
        var stack:ItemStack = null
        val slotObject:Slot = this.inventorySlots.get(slot).asInstanceOf[Slot]

        // Not null and can stack?
        if (slotObject != null && slotObject.getHasStack) {
            val stackInSlot = slotObject.getStack
            stack = stackInSlot.copy

            // From TE
            if (slot < 5) {
                if (!this.mergeItemStack(stackInSlot, 5, 41, true)) return null
            }
            // To TE
            else {
                if (!mergeItemStack(stackInSlot, 0, 4, true)) return null
            }

            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null)
            } else {
                slotObject.onSlotChanged()
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null
            }
            slotObject.onPickupFromSlot(player, stackInSlot)
        }
        stack
    }

    // Patched version of Vanilla mergeItemStack that actually checks if a slot is valid (and is in roughly ported Scala)
    protected override def mergeItemStack(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean = {
        var flag1: Boolean = false
        var k: Int = par2
        if (par4) {
            k = par3 - 1
        }
        var slot: Slot = null
        var itemstack1: ItemStack = null
        if (par1ItemStack.isStackable) {
            while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2)) {
                slot = this.inventorySlots.get(k).asInstanceOf[Slot]
                itemstack1 = slot.getStack
                if (itemstack1 != null && slot.isItemValid(par1ItemStack) && itemstack1.itemID == par1ItemStack.itemID && (!par1ItemStack.getHasSubtypes || par1ItemStack.getItemDamage == itemstack1.getItemDamage) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)) {
                    //                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Added section
                    val l: Int = itemstack1.stackSize + par1ItemStack.stackSize
                    if (l <= par1ItemStack.getMaxStackSize) {
                        par1ItemStack.stackSize = 0
                        itemstack1.stackSize = l
                        slot.onSlotChanged
                        flag1 = true
                    }
                    else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize) {
                        par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize - itemstack1.stackSize
                        itemstack1.stackSize = par1ItemStack.getMaxStackSize
                        slot.onSlotChanged
                        flag1 = true
                    }
                }
                if (par4) {
                    k -= 1
                }
                else {
                    k += 1
                }
            }
        }
        if (par1ItemStack.stackSize > 0) {
            if (par4) {
                k = par3 - 1
            }
            else {
                k = par2
            }
            try {
                while (!par4 && k < par3 || par4 && k >= par2) {
                    slot = this.inventorySlots.get(k).asInstanceOf[Slot]
                    itemstack1 = slot.getStack
                    if (itemstack1 == null && slot.isItemValid(par1ItemStack)) {
                        slot.putStack(par1ItemStack.copy)
                        slot.onSlotChanged
                        par1ItemStack.stackSize = 0
                        flag1 = true
                        throw new InterruptedException // We do this instead of Scala's break package as that seems to cause game crashes.
                    }
                    if (par4) {
                        k -= 1
                    }
                    else {
                        k += 1
                    }
                }
            } catch {
                case e:InterruptedException => // NO OP
            }
        }
        flag1
    }

}
