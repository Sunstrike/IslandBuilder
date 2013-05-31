package io.sunstrike.islandbuilder.blocks

import io.sunstrike.corestrike.struts.forge.{CSBlock, IDebuggableTile}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{IBlockAccess, World}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.block.material.Material
import net.minecraftforge.common.ForgeDirection
import scala.concurrent._
import ExecutionContext.Implicits.global
import io.sunstrike.corestrike.algorithms.FloatingIslandGeneration
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.helpers.IsBlConfig
import net.minecraft.block.{StepSound, Block}
import net.minecraft.util.Icon
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.creativetab.CreativeTabs
import java.util
import net.minecraft.item.{Item, ItemBlock, ItemStack}

/*
 * FlIsBuilder
 * io.sunstrike.islandbuilder.blocks
 */

/**
 * Floating Island Builder - Block
 *
 * @author Sunstrike
 */
class FlIsBuilder(id:Int, mat:Material) extends CSBlock(id, mat) {

    val pathSideTexMini = "IslandBuilder:MiniBuilder"
    val pathSideTexNorm = "IslandBuilder:NormBuilder"
    val pathSideTexHuge = "IslandBuilder:HugeBuilder"
    val pathTopTex = "IslandBuilder:TopBuilder"

    var sideTexMini:Icon = null
    var sideTexNorm:Icon = null
    var sideTexHuge:Icon = null
    var topTex:Icon = null

    setStepSound(Block.soundMetalFootstep)
    setUnlocalizedName("FlIsBuilder")
    setCreativeTab(CreativeTabs.tabRedstone)

    override def registerIcons(iconReg: IconRegister) {
        sideTexMini = iconReg.registerIcon(pathSideTexMini)
        sideTexNorm = iconReg.registerIcon(pathSideTexNorm)
        sideTexHuge = iconReg.registerIcon(pathSideTexHuge)
        topTex = iconReg.registerIcon(pathTopTex)
    }

    override def getBlockTexture(blAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Icon = {
        getIcon(side, blAccess.getBlockMetadata(x, y, z))
    }

    override def getIcon(side: Int, meta: Int): Icon = {
        val fDir = ForgeDirection.getOrientation(side)
        if (fDir == ForgeDirection.UP) return topTex

        meta match {
            case 0 => sideTexMini
            case 1 => sideTexNorm
            case 2 => sideTexHuge
            case _ => null
        }
    }

    override def isBlockSolid(par1IBlockAccess: IBlockAccess, par2: Int, par3: Int, par4: Int, par5: Int): Boolean = true

    override def isOpaqueCube: Boolean = true

    override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean = true

    override def hasTileEntity(metadata: Int): Boolean = metadata >= 0 && metadata <= 2

    override def createTileEntity(world: World, metadata: Int): TileEntity = {
        metadata match {
            case 0 => new FlIsBuilderTile(10)
            case 1 => new FlIsBuilderTile(20)
            case 2 => new FlIsBuilderTile(50)
            case _ => throw new RuntimeException("Invalid meta to createTileEntity!")
        }
    }

    override def damageDropped(par1: Int): Int = par1

    override def getSubBlocks(par1: Int, par2CreativeTabs: CreativeTabs, par3List: util.List[_]) {
        for (i <- 0 to 2) {
            par3List.asInstanceOf[util.List[ItemStack]].add(new ItemStack(par1, 1, i))
        }
    }
}

class FlIsBuilderItemBlock(id:Int) extends ItemBlock(id) {

    val subNames = Array("mini", "norm", "huge")

    setHasSubtypes(true)

    override def addInformation(iStack: ItemStack, player: EntityPlayer, list: util.List[_], par4: Boolean) {
        val lore = iStack.getItemDamage match {
            case 0 => "10x10x10 - Labled 'Beginner Pack'."
            case 1 => "20x20x20 - Is this safe?"
            case 2 => "50x50x50 - \"DANGER! High pressure!\""
            case _ => "It looks broken..."
        }

        list.asInstanceOf[util.List[String]].add(lore)
    }

    override def getItemDisplayName(iStack: ItemStack): String = {
        iStack.getItemDamage match {
            case 0 => "Mini Island-in-a-Box"
            case 1 => "Island-in-a-Box"
            case 2 => "Huge Island-in-a-Box"
            case _ => "Damaged Island Crate"
        }
    }

    override def getLocalizedName(par1ItemStack: ItemStack): String = getItemDisplayName(par1ItemStack)

    override def getMetadata(par1: Int): Int = par1

}

/**
 * Floating Island Builder - TileEntity
 *
 * @author Sunstrike
 */
class FlIsBuilderTile(maxIslSize:Int) extends TileEntity with IDebuggableTile {

    var hasWork = true
    var futureStore:Future[Array[Array[Array[Int]]]] = null
    var gridReceived = false
    var grid:Array[Array[Array[Int]]] = null
    var islandBuilt = false
    val islSize = maxIslSize

    def sendDebugToPlayer(world: World, player: EntityPlayer) {
        if (world.isRemote) return
        player.addChatMessage("[FlIsBuilderTile] hasWork: " + hasWork)
        player.addChatMessage("[FlIsBuilderTile] futureStore: " + futureStore)
        player.addChatMessage("[FlIsBuilderTile] gridReceived: " + gridReceived)
        player.addChatMessage("[FlIsBuilderTile] grid: " + grid)
        player.addChatMessage("[FlIsBuilderTile] islandBuilt: " + islandBuilt)
    }

    override def onInventoryChanged() {
        super.onInventoryChanged()
    }

    override def updateEntity() {
        super.updateEntity()
        if (worldObj.isRemote) return
        if (hasWork && worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 6) {
            hasWork = false
            futureStore = future {
                val generator = new FloatingIslandGeneration()
                logger.info("[FlIsBuilderTile-Future] Generator: " + generator)
                val g = generator.generate(islSize, islSize)
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

    private def buildIsland {
        // Find offset of centre to adjust coords to block position
        val centre:Int = islSize/2

        // The algo generates on a DIFFERENT axis system to MC (x/y horizon, z vertical)
        val offsetX = xCoord - centre
        val offsetY = zCoord - centre
        val offsetZ = yCoord + 1

        // World gen
        for (y <- 0 to grid(0)(0).length - 1) {
            for (x <- 0 to grid.length - 1) {
                for (z <- 0 to grid.length - 1) {
                    if (grid(x)(y)(z) == 1) {
                        if (worldObj.isAirBlock(x+offsetX, z+offsetZ, y+offsetY) || worldObj.getBlockMaterial(x+offsetX, z+offsetZ, y+offsetY).isReplaceable) {
                            worldObj.setBlock(x+offsetX, z+offsetZ, y+offsetY, Block.dirt.blockID)
                        }
                    }
                }
            }
        }
    }

    override def readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
    }

    override def writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
    }

}