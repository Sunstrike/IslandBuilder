package io.sunstrike.islandbuilder.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.InventoryPlayer
import org.lwjgl.opengl.GL11
import io.sunstrike.islandbuilder.blocks.CrateAssemblerTile
import io.sunstrike.islandbuilder.container.ContainerAssembler
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import net.minecraft.util.StatCollector
import net.minecraft.client.gui.GuiButton
import org.lwjgl.input.Keyboard
import java.util
import cpw.mods.fml.common.network.PacketDispatcher
import io.sunstrike.islandbuilder.network.PacketGuiButton

/*
 * GuiCrateAssembler
 * io.sunstrike.islandbuilder.gui
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
class GuiCrateAssembler(inventoryPlayer:InventoryPlayer, tileEntity:CrateAssemblerTile) extends GuiContainer(new ContainerAssembler(inventoryPlayer, tileEntity)) {

    override def initGui() {
        super.initGui()
        buttonList.asInstanceOf[util.List[GuiButton]].add(new GuiButton(0, this.guiLeft + 88, this.guiTop + 28, 15, 12, "-"))
        buttonList.asInstanceOf[util.List[GuiButton]].add(new GuiButton(1, this.guiLeft + 139, this.guiTop + 28, 15, 12, "+"))
        buttonList.asInstanceOf[util.List[GuiButton]].add(new GuiButton(2, this.guiLeft + 88, this.guiTop + 54, 15, 12, "-"))
        buttonList.asInstanceOf[util.List[GuiButton]].add(new GuiButton(3, this.guiLeft + 139, this.guiTop + 54, 15, 12, "+"))
    }

    protected override def actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        button.id match {
            case 0 =>
                logger.info("[CrateAssemblerGUI] Action performed on minusRadius")
                if (Keyboard.isKeyDown(42)) tileEntity.addRadius(-10) else tileEntity.addRadius(-5)
            case 1 =>
                logger.info("[CrateAssemblerGUI] Action performed on plusRadius")
                if (Keyboard.isKeyDown(42)) tileEntity.addRadius(10) else tileEntity.addRadius(5)
            case 2 =>
                logger.info("[CrateAssemblerGUI] Action performed on minusHeight")
                if (Keyboard.isKeyDown(42)) tileEntity.addHeight(-10) else tileEntity.addHeight(-5)
            case 3 =>
                logger.info("[CrateAssemblerGUI] Action performed on plusHeight")
                if (Keyboard.isKeyDown(42)) tileEntity.addHeight(10) else tileEntity.addHeight(5)
        }
        PacketDispatcher.sendPacketToServer(new PacketGuiButton(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, button.id, Keyboard.isKeyDown(42)).toPacket250CustomPayload)
    }

    protected override def drawGuiContainerForegroundLayer(p1:Int, p2:Int) {
        // GUI rendering
        // the parameters for drawString are: string, x, y, color
        fontRenderer.drawString("Crate Assembler", 8, 6, 4210752)
        // draws "Inventory" or your regional equivalent
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752)
        fontRenderer.drawString("Radius", 87, 18, 4210752)
        fontRenderer.drawString("Height", 87, 44, 4210752)
        fontRenderer.drawString(tileEntity.radius.toString, 114, 31, 4210752)
        fontRenderer.drawString(tileEntity.height.toString, 114, 57, 4210752)
    }

    protected override def drawGuiContainerBackgroundLayer(par1:Float, par2:Int, par3:Int) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        this.mc.renderEngine.bindTexture("/mods/IslandBuilder/textures/gui/CrateAssembler.png")
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
    }

}
