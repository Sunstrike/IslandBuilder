package io.sunstrike.islandbuilder.helpers

import io.sunstrike.corestrike.util.forge.RegistrationHelper._
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.blocks.{FlIsBuilderItemBlock, FlIsBuilderTile}
import io.sunstrike.islandbuilder._
import net.minecraft.item.{Item, ItemStack}
import cpw.mods.fml.common.registry.{GameRegistry, LanguageRegistry}
import collection.JavaConversions._
import net.minecraft.block.Block
import net.minecraftforge.oredict.ShapedOreRecipe

/*
 * ForgeRegistration
 * io.sunstrike.islandbuilder.helpers
 */

/**
 * Handler to register objects with Forge
 *
 * @author Sunstrike
 */
object ForgeRegistration {

    private def registerBlocks() {
        val stick = new ItemStack(Item.stick)
        val miniMod = new ItemStack(item_FlIsModule, 1, 0)
        val normMod = new ItemStack(item_FlIsModule, 1, 1)
        val hugeMod = new ItemStack(item_FlIsModule, 1, 2)

        registerTE(classOf[FlIsBuilderTile], "FlIsBuilderTile")
        GameRegistry.registerBlock(block_FlIsBuilder, classOf[FlIsBuilderItemBlock], "FlIsBuilder")
        val flIsBuilderNames = Array("Mini Island-on-a-Box", "Island-in-a-Box", "Huge Island-in-a-Box")
        for (i <- 0 to 2) {
            val stack = new ItemStack(block_FlIsBuilder, 1, i)
            LanguageRegistry.addName(stack, flIsBuilderNames(stack.getItemDamage))
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 0), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], miniMod))
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 1), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], normMod))
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 2), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], hugeMod))
    }

    private def registerItems() {
        val pearl = new ItemStack(Item.enderPearl)
        val dirt = new ItemStack(Block.dirt)
        val miniMod = new ItemStack(item_FlIsModule, 1, 0)
        val normMod = new ItemStack(item_FlIsModule, 1, 1)
        val hugeMod = new ItemStack(item_FlIsModule, 1, 2)

        registerItemWithoutLang(item_FlIsModule, "FlIsModule", modId)
        val flIsModuleNames = Array("Mini Island Module", "Normal Island Module", "Huge Island Module")
        for (i <- 0 to 2) {
            val stack = new ItemStack(item_FlIsModule, 1, i)
            LanguageRegistry.addName(stack, flIsModuleNames(stack.getItemDamage))
        }
        GameRegistry.addShapedRecipe(miniMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], dirt, 'E'.asInstanceOf[Character], pearl)
        GameRegistry.addShapedRecipe(normMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], miniMod, 'E'.asInstanceOf[Character], pearl)
        GameRegistry.addShapedRecipe(hugeMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], normMod, 'E'.asInstanceOf[Character], pearl)
    }

    def registerAll() {
        ObjRepo.setupRecipes()
        registerBlocks()
        registerItems()
    }

}
