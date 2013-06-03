package io.sunstrike.islandbuilder.blocks

import io.sunstrike.corestrike.struts.forge.{IDebuggableTile, CSBlock}
import net.minecraft.block.material.Material
import net.minecraft.util.Icon
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.world.{World, IBlockAccess}
import net.minecraftforge.common.ForgeDirection
import net.minecraft.tileentity.TileEntity
import java.util
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.entity.player.EntityPlayer
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.inventory.IInventory
import net.minecraft.entity.item.EntityItem
import io.sunstrike.islandbuilder.IslandBuilder
import io.sunstrike.islandbuilder.blocks.traits.IGuiEventable
import io.sunstrike.islandbuilder.network.{PacketCrateAssemUpdate, PacketGuiButton}
import cpw.mods.fml.common.network.{PacketDispatcher, Player}
import net.minecraft.network.packet.{Packet132TileEntityData, Packet}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import cpw.mods.fml.common.FMLCommonHandler
import net.minecraft.network.INetworkManager
import scala.math._

/*
 * CrateAssembler
 * io.sunstrike.islandbuilder.blocks
 */

/**
 * Crate Assembler - Block
 *
 * @author Sunstrike
 */
class CrateAssembler(id:Int) extends CSBlock(id, Material.circuits) {

    val pathSideTex = "IslandBuilder:AssemblerSide"
    val pathTopTex = "IslandBuilder:AssemblerTop"

    var sideTex:Icon = null
    var topTex:Icon = null

    setStepSound(Block.soundMetalFootstep)
    setUnlocalizedName("CrateAssembler")
    setCreativeTab(CreativeTabs.tabRedstone)

    /**
     * Debug hook inside onBlockClicked to detect sticks.
     *
     * Subclasses must include a super call to retain the debug helper.
     *
     * @param world The current world
     * @param x x coord
     * @param y y coord
     * @param z z coord
     * @param player Player clicking it
     */
    override def onBlockClicked(world: World, x: Int, y: Int, z: Int, player: EntityPlayer) {
        // We want CrateAssem to dump client AND server debug
        val held:ItemStack = player.getCurrentEquippedItem
        if (held == null || held.getItem != Item.stick) return

        val te:TileEntity = world.getBlockTileEntity(x, y, z)
        te match {
            case t:IDebuggableTile => t.sendDebugToPlayer(world, player)
            case _ => return
        }
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {
        val tileEntity = world.getBlockTileEntity(x, y, z)
        if (tileEntity == null || player.isSneaking) {
            return false
        }
        player.openGui(IslandBuilder, 0, world, x, y, z)
        true
    }

    override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
        val te = world.getBlockTileEntity(x, y, z)
        if (te.isInstanceOf[IInventory]) {
            for (i <- 0 to te.asInstanceOf[IInventory].getSizeInventory - 1) {
                val item = te.asInstanceOf[IInventory].getStackInSlot(i)
                if (item != null && item.stackSize > 0) {
                    val entity = new EntityItem(world, x, y, z, new ItemStack(item.itemID, item.stackSize, item.getItemDamage))
                    world.spawnEntityInWorld(entity)
                    item.stackSize = 0
                }
            }
        }
        super.breakBlock(world, x, y, z, par5, par6)
    }

    setStepSound(Block.soundMetalFootstep)
    setUnlocalizedName("FlIsBuilder")
    setCreativeTab(CreativeTabs.tabRedstone)

    override def getBlockHardness(par1World: World, par2: Int, par3: Int, par4: Int): Float = 1.0f

    override def registerIcons(iconReg: IconRegister) {
        sideTex = iconReg.registerIcon(pathSideTex)
        topTex = iconReg.registerIcon(pathTopTex)
    }

    override def getBlockTexture(blAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Icon = {
        getIcon(side, 0)
    }

    override def getIcon(side: Int, meta: Int): Icon = {
        val fDir = ForgeDirection.getOrientation(side)
        if (fDir == ForgeDirection.UP || fDir == ForgeDirection.DOWN) topTex else sideTex
    }

    override def isBlockSolid(par1IBlockAccess: IBlockAccess, par2: Int, par3: Int, par4: Int, par5: Int): Boolean = true

    override def isOpaqueCube: Boolean = true

    override def hasTileEntity(metadata: Int): Boolean = true

    override def createTileEntity(world: World, metadata: Int): TileEntity = new CrateAssemblerTile

}

class CrateAssemblerItemBlock(id:Int) extends ItemBlock(id) {

    override def addInformation(iStack: ItemStack, player: EntityPlayer, list: util.List[_], par4: Boolean) {
        list.asInstanceOf[util.List[String]].add("Used to pack islands into boxes.")
    }

    override def getItemDisplayName(iStack: ItemStack): String = "Crate Assembler"

    override def getLocalizedName(par1ItemStack: ItemStack): String = getItemDisplayName(par1ItemStack)

}

/**
 * Crate Assembler - TileEntity
 *
 * @author Sunstrike
 */
class CrateAssemblerTile extends TileEntity with IInventory with IGuiEventable with IDebuggableTile {

    var inv = new Array[ItemStack](5)
    // Default values
    var radius = 10
    var height = 10

    override def getSizeInventory:Int = inv.length

    override def getStackInSlot(slot:Int):ItemStack = inv(slot)

    override def setInventorySlotContents(slot:Int, stack:ItemStack) {
        inv(slot) = stack
        if (stack != null && stack.stackSize > getInventoryStackLimit) {
            stack.stackSize = getInventoryStackLimit
        }
    }

    override def decrStackSize(slot:Int, amt:Int):ItemStack = {
        var stack = getStackInSlot(slot)
        if (stack != null) {
            if (stack.stackSize <= amt) {
                setInventorySlotContents(slot, null)
            } else {
                stack = stack.splitStack(amt)
                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null)
                }
            }
        }
        stack
    }

    override def getStackInSlotOnClosing(slot:Int):ItemStack = {
        val stack = getStackInSlot(slot)
        if (stack != null) {
            setInventorySlotContents(slot, null)
        }
        stack
    }

    override def getInventoryStackLimit:Int = 64

    override def isUseableByPlayer(player:EntityPlayer):Boolean = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64

    override def readFromNBT(tagCompound:NBTTagCompound) {
        super.readFromNBT(tagCompound)

        val tagList = tagCompound.getTagList("Inventory")
        for (i <- 0 to tagList.tagCount() - 1) {
            val tag = tagList.tagAt(i).asInstanceOf[NBTTagCompound]
            val slot = tag.getByte("Slot")
            if (slot >= 0 && slot < inv.length) {
                inv(slot) = ItemStack.loadItemStackFromNBT(tag)
            }
        }

        radius = tagCompound.getInteger("Radius")
        height = tagCompound.getInteger("Height")

        if (radius < 10) radius = 10
        if (height < 10) height = 10
    }

    override def writeToNBT(tagCompound:NBTTagCompound) {
        super.writeToNBT(tagCompound)

        val itemList = new NBTTagList()
        for (i <- 0 to inv.length - 1) {
            val stack = inv(i)
            if (stack != null) {
                val tag = new NBTTagCompound()
                tag.setByte("Slot", i.asInstanceOf[Byte])
                stack.writeToNBT(tag)
                itemList.appendTag(tag)
            }
        }
        tagCompound.setTag("Inventory", itemList)

        tagCompound.setInteger("Radius", radius)
        tagCompound.setInteger("Height", height)
    }

    override def getInvName:String = "Crate Assembler"

    def openChest() {}

    def closeChest() {}

    override def isStackValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        val i = stack.getItem
        val d = stack.getItemDamage
        slot match {
            case 1 =>
                d == 0 && i == item_FlIsModule // Slot 1 - Mini modules
            case 2 =>
                d == 1 && i == item_FlIsModule // Slot 2 - Norm Modules
            case 3 =>
                d == 2 && i == item_FlIsModule // Slot 3 - Huge modules
            case 4 =>
                i.itemID == new ItemStack(block_EmptyCrate).getItem.itemID // Slot 4 - Crates (empty)
            case 5 =>
                false // Slot 5 - Output
            case _ => false
        }
    }

    def isInvNameLocalized: Boolean = true

    override def onInventoryChanged() {
        super.onInventoryChanged()
        updateOutput()
    }

    def updateOutput() {
        if (inv(3) == null) {
            inv(4) = null
            return
        }

        val blocks = calculateReqBlocks(radius, height)
        val countHuge = if (inv(2) != null) inv(2).stackSize else 0
        val countNorm = if (inv(1) != null) inv(1).stackSize else 0
        val countMini = if (inv(0) != null) inv(0).stackSize else 0
        val moduleMax = countHuge*value_ModuleHuge + countNorm*value_ModuleNorm + countMini*value_ModuleMini

        logger.info("[CrateAssem] moduleMax: " + moduleMax + ", blocks: " + blocks)

        if (moduleMax >= blocks) {
            inv(4) = new ItemStack(block_EmptyCrate)
            val nbt = new NBTTagCompound()
            nbt.setInteger("radius", radius)
            nbt.setInteger("height", height)
            inv(4).setTagCompound(nbt)
        } else inv(4) = null
    }

    def onPullCrate() {
        // Subtract crate
        inv(3).stackSize -= 1
        if (inv(3) == null || inv(3).stackSize <= 0) inv(3) = null

        val blocks = calculateReqBlocks(radius, height)
        val countHuge = if (inv(2) != null) inv(2).stackSize else 0
        val countNorm = if (inv(1) != null) inv(1).stackSize else 0
        val countMini = if (inv(0) != null) inv(0).stackSize else 0

        if (countMini*value_ModuleMini >= blocks) {
            val toDeduct = math.ceil(blocks.toDouble/value_ModuleMini.toDouble)
            inv(0).stackSize -= toDeduct.asInstanceOf[Int]
            if (inv(0).stackSize <= 0) inv(0) = null
            return
        } else if (countMini*value_ModuleMini + countNorm*value_ModuleNorm >= blocks) {
            val normUsed = math.floor(blocks.toDouble/value_ModuleNorm.toDouble).toInt
            val valMissing = (countNorm - normUsed)*value_ModuleNorm
            val miniUsed = math.ceil(valMissing/value_ModuleMini.toDouble).toInt

            inv(1).stackSize -= normUsed.toInt
            if (inv(1).stackSize <= 0) inv(1) = null

            inv(0).stackSize -= miniUsed.toInt
            if (inv(0).stackSize <= 0) inv(0) = null
        } else if (countHuge*value_ModuleHuge + countMini*value_ModuleMini + countNorm*value_ModuleNorm >= blocks) {
            val hugeUsed = math.floor(blocks.toDouble/value_ModuleHuge.toDouble).toInt
            val hugeMissing = (countNorm - hugeUsed)*value_ModuleHuge
            val normUsed = math.floor(hugeMissing.toDouble/value_ModuleNorm.toDouble).toInt
            val normMissing = (countNorm - normUsed)*value_ModuleNorm
            val miniUsed = math.ceil(normMissing/value_ModuleMini.toDouble).toInt

            inv(2).stackSize -= hugeUsed.toInt
            if (inv(2).stackSize <= 0) inv(2) = null

            inv(1).stackSize -= normUsed.toInt
            if (inv(1).stackSize <= 0) inv(1) = null

            inv(0).stackSize -= miniUsed.toInt
            if (inv(0).stackSize <= 0) inv(0) = null
        }
    }

    private def calculateReqBlocks(r:Int, h:Int):Int = {
        val bl = Pi * h * r * r
        (bl/3).asInstanceOf[Int]
    }

    /**
     * Debug hook for CSBlock.
     *
     * Used to send debugging information to the acting player. Send messages with player.addChatMessage, preferably in form "[TileClassName] Debug message".
     *
     * @param world World the player is in.
     * @param player The player acting to get debug info.
     */
    def sendDebugToPlayer(world: World, player: EntityPlayer) {
        if (FMLCommonHandler.instance().getEffectiveSide == Side.CLIENT){
            player.addChatMessage("[CrateAssembler-Client] Radius: " + radius)
            player.addChatMessage("[CrateAssembler-Client] Height: " + height)
        } else {
            player.addChatMessage("[CrateAssembler-Server] Radius: " + radius)
            player.addChatMessage("[CrateAssembler-Server] Height: " + height)
        }
    }

    def receivePacketGuiButton(packet: PacketGuiButton) {
        packet.button match {
            case 0 => if (packet.shifting) addRadius(-10) else addRadius(-5)
            case 1 => if (packet.shifting) addRadius(10) else addRadius(5)
            case 2 => if (packet.shifting) addHeight(-10) else addHeight(-5)
            case 3 => if (packet.shifting) addHeight(10) else addHeight(5)
            case _ => return
        }
    }

    override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
        readFromNBT(pkt.customParam1)
        updateOutput
    }

    override def getDescriptionPacket: Packet = {
        val tag = new NBTTagCompound()
        writeToNBT(tag)
        new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag)
    }

    def addRadius(i:Int) {
        if (radius + i < 10 || radius + i < height || radius + i > 125) return
        radius += i
        updateOutput
    }

    def addHeight(i:Int) {
        if (height + i < 10 || height + i > radius || height + i > 125) return
        height += i
        updateOutput
    }

}