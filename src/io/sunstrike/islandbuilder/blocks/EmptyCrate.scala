package io.sunstrike.islandbuilder.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.Icon
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.entity.player.EntityPlayer
import java.util
import io.sunstrike.corestrike.struts.forge.{IDebuggableTile, CSBlock}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraft.tileentity.TileEntity
import scala.concurrent._
import io.sunstrike.corestrike.algorithms.FloatingIslandGeneration
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import net.minecraft.nbt.NBTTagCompound
import ExecutionContext.Implicits.global
import net.minecraftforge.common.ForgeDirection

/*
 * EmptyCrate
 * io.sunstrike.islandbuilder.blocks
 */

/**
 * Empty Crate - Block
 *
 * @author Sunstrike
 */
class EmptyCrate(id:Int) extends CSBlock(id, Material.wood) {

    val pathEmptyTex = "IslandBuilder:EmptyCrate"
    val pathFullTex = "IslandBuilder:FilledCrate"
    val pathTopTex = "IslandBuilder:EmptyCrate"
    var emptyTex:Icon = null
    var fullTex:Icon = null
    var topTex:Icon = null

    setStepSound(Block.soundMetalFootstep)
    setUnlocalizedName("EmptyCrate")
    setCreativeTab(CreativeTabs.tabMaterials)
    setHardness(1.0f)

    override def renderAsNormalBlock(): Boolean = true

    override def isOpaqueCube: Boolean = true

    override def registerIcons(iconReg: IconRegister) {
        emptyTex = iconReg.registerIcon(pathEmptyTex)
        fullTex = iconReg.registerIcon(pathFullTex)
        topTex = iconReg.registerIcon(pathTopTex)
    }

    override def createTileEntity(world: World, metadata: Int): TileEntity = new EmptyCrateTile

    override def hasTileEntity(metadata: Int): Boolean = true

    override def getIcon(par1: Int, par2: Int): Icon = {
        emptyTex
    }

    override def getBlockTexture(par1IBlockAccess: IBlockAccess, par2: Int, par3: Int, par4: Int, par5: Int): Icon = {
        if (ForgeDirection.getOrientation(par5) == ForgeDirection.UP || ForgeDirection.getOrientation(par5) == ForgeDirection.DOWN) return topTex
        val te = par1IBlockAccess.getBlockTileEntity(par2, par3, par4)
        if (te.isInstanceOf[EmptyCrateTile]) {
            te.asInstanceOf[EmptyCrateTile].getIconId match {
                case 0 => emptyTex
                case 1 => fullTex
            }
        } else emptyTex
    }
}

/**
 * Empty Crate - ItemBlock
 *
 * @author Sunstrike
 */
class EmptyCrateItemBlock(id:Int) extends ItemBlock(id) {

    setMaxStackSize(16)

    override def addInformation(iStack: ItemStack, player: EntityPlayer, list: util.List[_], par4: Boolean) {
        val nbt = iStack.getTagCompound
        if (nbt != null && nbt.hasKey("radius") && nbt.hasKey("height")) {
            list.asInstanceOf[util.List[String]].add("Radius: " + nbt.getInteger("radius") + ", Height: " + nbt.getInteger("height"))
        } else {
            list.asInstanceOf[util.List[String]].add("Empty, but full of potential.")
        }
    }

    override def getItemDisplayName(iStack: ItemStack): String = {
        val nbt = iStack.getTagCompound
        if (nbt != null && nbt.hasKey("radius") && nbt.hasKey("height")) "Packed Crate" else "Empty Crate"
    }

    override def getLocalizedName(par1ItemStack: ItemStack): String = getItemDisplayName(par1ItemStack)

    override def placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Boolean = {
        val ret = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)
        val te = world.getBlockTileEntity(x, y, z)
        if (ret && te.isInstanceOf[EmptyCrateTile]) {
            te.asInstanceOf[EmptyCrateTile].moduleNbt = stack.getTagCompound
        }
        ret
    }
}

class EmptyCrateTile extends TileEntity with IDebuggableTile {

    var moduleNbt:NBTTagCompound = null

    var hasWork = true
    var futureStore:Future[Array[Array[Array[Int]]]] = null
    var gridReceived = false
    var grid:Array[Array[Array[Int]]] = null
    var islandBuilt = false

    def sendDebugToPlayer(world: World, player: EntityPlayer) {
        if (world.isRemote) return
        player.addChatMessage("[CrateTile] module: " + moduleNbt)
        player.addChatMessage("[CrateTile] hasWork: " + hasWork)
        player.addChatMessage("[CrateTile] futureStore: " + futureStore)
        player.addChatMessage("[CrateTile] gridReceived: " + gridReceived)
        player.addChatMessage("[CrateTile] grid: " + grid)
        player.addChatMessage("[CrateTile] islandBuilt: " + islandBuilt)
    }

    def getIconId: Int = {
        // 0=Empty, 1=Full
        if (moduleNbt == null) 0 else 1
    }

    override def onInventoryChanged() {
        super.onInventoryChanged()
    }

    override def updateEntity() {
        super.updateEntity()
        if (worldObj.isRemote) return
        if (moduleNbt != null && hasWork && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {// && worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 6) {
            hasWork = false
            val rad = moduleNbt.getInteger("radius")
            val hei = moduleNbt.getInteger("height")
            futureStore = future {
                val generator = new FloatingIslandGeneration()
                logger.info("[FlIsBuilderTile-Future] Generator: " + generator + " with radius " + rad + " and height " + hei)
                val g = generator.generate(hei, rad)
                logger.info("[FlIsBuilderTile-Future] Generation done: " + g)
                g
            }
        } else if (!gridReceived && futureStore != null && futureStore.isCompleted) {
            futureStore.collect {
                case g:Array[Array[Array[Int]]] =>
                    grid = g
                    gridReceived = true
                case _ =>
                    logger.severe("[FlIsBuilderTile] Invalid result from FlIsGen!")
                    worldObj.destroyBlock(xCoord, yCoord, zCoord, false)
            }
        } else if (gridReceived && !islandBuilt) {
            worldObj.createExplosion(null, xCoord + 0.5, yCoord, zCoord + 0.5, 0, true)
            worldObj.destroyBlock(xCoord, yCoord, zCoord, false)
            buildIsland
        } else if (islandBuilt) {
            worldObj.destroyBlock(xCoord, yCoord, zCoord, false) // Just in case.
        }
    }

    override def getBlockType: Block = block_EmptyCrate

    private def buildIsland {
        val rad = moduleNbt.getInteger("radius")
        val hei = moduleNbt.getInteger("height")

        // Find offset of centre to adjust coords to block position
        val centre:Int = rad/2

        // The algo generates on a DIFFERENT axis system to MC (x/y horizon, z vertical)
        val offsetX = xCoord - centre
        val offsetY = zCoord - centre
        val offsetZ = yCoord + 1

        // World gen
        try {
            for (y <- 0 to rad - 1) {
                for (x <- 0 to rad - 1) {
                    for (z <- 0 to hei - 1) {
                        if (grid(x)(y)(z) == 1) {
                            if (worldObj.isAirBlock(x+offsetX, z+offsetZ, y+offsetY) || worldObj.getBlockMaterial(x+offsetX, z+offsetZ, y+offsetY).isReplaceable) {
                                worldObj.setBlock(x+offsetX, z+offsetZ, y+offsetY, Block.dirt.blockID)
                            }
                        }
                    }
                }
            }
        } catch {
            case ex:ArrayIndexOutOfBoundsException =>
                logger.severe("[PackedCrate] Array index out of bounds! " + ex.toString)
                ex.printStackTrace()
        }
    }

    override def readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        moduleNbt = nbt.getCompoundTag("module")
    }

    override def writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)

        nbt.setCompoundTag("module", moduleNbt)
    }

}