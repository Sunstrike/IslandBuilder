package io.sunstrike.islandbuilder.gui

import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import io.sunstrike.islandbuilder.blocks.CrateAssemblerTile
import io.sunstrike.islandbuilder.container.ContainerAssembler

/*
 * GuiHandler
 * io.sunstrike.islandbuilder.gui
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
object GuiHandler extends IGuiHandler {

    def getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        id match {
            case 0 =>
                val tileEntity = world.getBlockTileEntity(x, y, z)
                if(tileEntity.isInstanceOf[CrateAssemblerTile]){
                    return new ContainerAssembler(player.inventory, tileEntity.asInstanceOf[CrateAssemblerTile])
                }
        }
        null
    }

    def getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        id match {
            case 0 =>
                val tileEntity = world.getBlockTileEntity(x, y, z)
                if(tileEntity.isInstanceOf[CrateAssemblerTile]){
                    return new GuiCrateAssembler(player.inventory, tileEntity.asInstanceOf[CrateAssemblerTile])
                }
        }
        null
    }

}
