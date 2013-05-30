package io.sunstrike.islandbuilder.items

import net.minecraft.item.{ItemStack, Item}
import net.minecraft.util.Icon
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.creativetab.CreativeTabs
import java.util
import net.minecraft.entity.player.EntityPlayer
import io.sunstrike.islandbuilder.helpers.ObjRepo._

/*
 * FlIsModule
 * io.sunstrike.islandbuilder.items
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
class FlIsModule(id:Int) extends Item(id) {

    val pathMiniTex = "IslandBuilder:module_t1"
    val pathNormTex = "IslandBuilder:module_t2"
    val pathHugeTex = "IslandBuilder:module_t3"

    var miniTex:Icon = null
    var normTex:Icon = null
    var hugeTex:Icon = null

    setCreativeTab(CreativeTabs.tabMaterials)
    setHasSubtypes(true)
    setUnlocalizedName("FlIsModule")

    override def getItemDisplayName(par1ItemStack: ItemStack): String = {
        par1ItemStack.getItemDamage match {
            case 0 => "Mini Island Module"
            case 1 => "Normal Island Module"
            case 2 => "Huge Island Module"
            case _ => "Corrupt Island Module"
        }
    }

    override def getLocalizedName(par1ItemStack: ItemStack): String = getItemDisplayName(par1ItemStack)

    override def getUnlocalizedName(par1ItemStack: ItemStack): String = getItemDisplayName(par1ItemStack)

    override def getSubItems(par1: Int, par2CreativeTabs: CreativeTabs, par3List: util.List[_]) {
        for (i <- 0 to 2) {
            par3List.asInstanceOf[util.List[ItemStack]].add(new ItemStack(par1, 1, i))
        }
    }

    override def registerIcons(iconReg: IconRegister) {
        miniTex = iconReg.registerIcon(pathMiniTex)
        normTex = iconReg.registerIcon(pathNormTex)
        hugeTex = iconReg.registerIcon(pathHugeTex)
    }

    override def addInformation(par1ItemStack: ItemStack, par2EntityPlayer: EntityPlayer, par3List: util.List[_], par4: Boolean) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4)
    }

    /*override def getIconFromDamageForRenderPass(par1: Int, par2: Int): Icon = {
        getIconFromDamage(par1)
    }

    override def getIcon(stack: ItemStack, renderPass: Int, player: EntityPlayer, usingItem: ItemStack, useRemaining: Int): Icon = {
        getIconFromDamage(stack.getItemDamage)
    }

    override def getIcon(stack: ItemStack, pass: Int): Icon = {
        getIconFromDamage(stack.getItemDamage)
    }*/

    override def getIconFromDamage(par1: Int): Icon = {
        par1 match {
            case 0 => miniTex
            case 1 => normTex
            case 2 => hugeTex
            case _ =>
                logger.warning("Meta: " + par1)
                null
        }
    }

}
