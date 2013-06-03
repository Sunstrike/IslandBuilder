package io.sunstrike.islandbuilder.helpers

import io.sunstrike.corestrike.util.forge.RegistrationHelper._
import io.sunstrike.islandbuilder.gui.GuiHandler
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.blocks._
import io.sunstrike.islandbuilder._
import net.minecraft.item.ItemStack
import cpw.mods.fml.common.registry.{GameRegistry, LanguageRegistry}
import collection.JavaConversions._
import cpw.mods.fml.common.network.NetworkRegistry

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

    private def registerNetwork() {
        NetworkRegistry.instance().registerGuiHandler(IslandBuilder, GuiHandler)
    }

    private def registerBlocks() {
        registerTE(classOf[FlIsBuilderTile], "FlIsBuilderTile")
        GameRegistry.registerBlock(block_FlIsBuilder, classOf[FlIsBuilderItemBlock], "FlIsBuilder")
        val flIsBuilderNames = Array("Mini Island-on-a-Box", "Island-in-a-Box", "Huge Island-in-a-Box")
        for (i <- 0 to 2) {
            val stack = new ItemStack(block_FlIsBuilder, 1, i)
            LanguageRegistry.addName(stack, flIsBuilderNames(stack.getItemDamage))
        }

        registerTE(classOf[EmptyCrateTile], "CrateTile")
        GameRegistry.registerBlock(block_EmptyCrate, classOf[EmptyCrateItemBlock], "emptyCrate")
        LanguageRegistry.addName(block_EmptyCrate, "Empty Crate")

        registerTE(classOf[CrateAssemblerTile], "CrateAssemblerTile")
        GameRegistry.registerBlock(block_CrateAssembler, classOf[CrateAssemblerItemBlock], "CrateAssembler")
        LanguageRegistry.addName(block_CrateAssembler, "Crate Assembler")
    }

    private def registerItems() {
        registerItemWithoutLang(item_FlIsModule, "FlIsModule", modId)
        val flIsModuleNames = Array("Mini Island Module", "Normal Island Module", "Huge Island Module")
        for (i <- 0 to 2) {
            val stack = new ItemStack(item_FlIsModule, 1, i)
            LanguageRegistry.addName(stack, flIsModuleNames(stack.getItemDamage))
        }
    }

    private def registerRecipes() {
        // Items
        GameRegistry.addRecipe(recipe_flIsModuleMini)
        GameRegistry.addRecipe(recipe_flIsModuleNorm)
        GameRegistry.addRecipe(recipe_flIsModuleHuge)

        // Blocks
        GameRegistry.addRecipe(recipe_emptyCrate)
        GameRegistry.addRecipe(recipe_crateAssembler)
        //GameRegistry.addRecipe(recipe_flIsBuilderMini)
        //GameRegistry.addRecipe(recipe_flIsBuilderNorm)
        //GameRegistry.addRecipe(recipe_flIsBuilderHuge)
    }

    def registerAll() {
        registerNetwork()
        registerBlocks()
        registerItems()
        ObjRepo.setupRecipes()
        registerRecipes()
    }

}
