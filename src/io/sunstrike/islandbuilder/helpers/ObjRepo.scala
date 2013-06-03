package io.sunstrike.islandbuilder.helpers

import java.util.logging.Logger
import io.sunstrike.islandbuilder.blocks.{CrateAssembler, EmptyCrate, FlIsBuilder}
import io.sunstrike.islandbuilder.items.FlIsModule
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.block.Block
import net.minecraftforge.oredict.ShapedOreRecipe

/*
 * ObjRepo
 * io.sunstrike.islandbuilder.helpers
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
object ObjRepo {

    //=== Utilities =================================
    val logger = Logger.getLogger(io.sunstrike.islandbuilder.modId)

    //=== Constants =================================
    val value_ModuleMini = 1000
    val value_ModuleNorm = 7500
    val value_ModuleHuge = 24500

    //=== Blocks ====================================
    var block_FlIsBuilder:FlIsBuilder = null
    var block_EmptyCrate:EmptyCrate = null
    var block_CrateAssembler:CrateAssembler = null

    //=== Items =====================================
    var item_FlIsModule:FlIsModule = null

    //=== Recipes ===================================
    var recipe_emptyCrate:ShapedOreRecipe = null
    var recipe_crateAssembler:ShapedOreRecipe = null
    var recipe_flIsBuilderMini:ShapedOreRecipe = null
    var recipe_flIsBuilderNorm:ShapedOreRecipe = null
    var recipe_flIsBuilderHuge:ShapedOreRecipe = null

    var recipe_flIsModuleMini:ShapedOreRecipe = null
    var recipe_flIsModuleNorm:ShapedOreRecipe = null
    var recipe_flIsModuleHuge:ShapedOreRecipe = null

    //=== Functions =================================
    def setupRecipes() {
        val stick = new ItemStack(Item.stick)
        val pearl = new ItemStack(Item.enderPearl)
        val dirt = new ItemStack(Block.dirt)
        val piston = new ItemStack(Block.pistonBase)
        val emptyCrate = new ItemStack(block_EmptyCrate)
        val crateAssembler = new ItemStack(block_CrateAssembler)
        val miniMod = new ItemStack(item_FlIsModule, 1, 0)
        val normMod = new ItemStack(item_FlIsModule, 1, 1)
        val hugeMod = new ItemStack(item_FlIsModule, 1, 2)

        recipe_flIsBuilderMini = new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 0), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], miniMod)
        recipe_flIsBuilderNorm = new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 1), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], normMod)
        recipe_flIsBuilderHuge = new ShapedOreRecipe(new ItemStack(block_FlIsBuilder, 1, 2), "PSP", "SMS", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick, 'M'.asInstanceOf[Character], hugeMod)

        recipe_emptyCrate = new ShapedOreRecipe(emptyCrate, "PSP", "S S", "PSP", 'P'.asInstanceOf[Character], "plankWood", 'S'.asInstanceOf[Character], stick)
        recipe_crateAssembler = new ShapedOreRecipe(crateAssembler, "WPW", "PCP", "WPW", 'W'.asInstanceOf[Character], "plankWood", 'P'.asInstanceOf[Character], piston, 'C'.asInstanceOf[Character], emptyCrate)
        recipe_flIsModuleMini = new ShapedOreRecipe(miniMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], dirt, 'E'.asInstanceOf[Character], pearl)
        recipe_flIsModuleNorm = new ShapedOreRecipe(normMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], miniMod, 'E'.asInstanceOf[Character], pearl)
        recipe_flIsModuleHuge = new ShapedOreRecipe(hugeMod, "DDD", "DED", "DDD", 'D'.asInstanceOf[Character], normMod, 'E'.asInstanceOf[Character], pearl)
    }

}
