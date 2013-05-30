package io.sunstrike.islandbuilder.helpers

import java.util.logging.Logger
import io.sunstrike.islandbuilder.blocks.FlIsBuilder
import io.sunstrike.islandbuilder.items.FlIsModule
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.block.Block

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

    //=== Blocks ====================================
    var block_FlIsBuilder:FlIsBuilder = null

    //=== Items =====================================
    var item_FlIsModule:FlIsModule = null

    //=== Recipes ===================================
    var recipe_flIsBuilderMini:Array[Object] = null
    var recipe_flIsBuilderNorm:Array[Object] = null
    var recipe_flIsBuilderHuge:Array[Object] = null

    var recipe_flIsModuleMini:Array[Object] = null
    var recipe_flIsModuleNorm:Array[Object] = null
    var recipe_flIsModuleHuge:Array[Object] = null

    //=== Functions =================================
    def setupRecipes() {
        val stick = new ItemStack(Item.stick)
        val plank = new ItemStack(Block.planks)
        val pearl = new ItemStack(Item.enderPearl)
        val dirt = new ItemStack(Block.dirt)
        val miniMod = new ItemStack(item_FlIsModule, 1, 0)
        val normMod = new ItemStack(item_FlIsModule, 1, 1)
        val hugeMod = new ItemStack(item_FlIsModule, 1, 2)

        recipe_flIsBuilderMini = Array("PSP", "SMS", "PSP", "P", plank, "S", stick, "M", miniMod)
        recipe_flIsBuilderNorm = Array("PSP", "SMS", "PSP", "P", plank, "S", stick, "M", normMod)
        recipe_flIsBuilderHuge = Array("PSP", "SMS", "PSP", "P", plank, "S", stick, "M", hugeMod)

        recipe_flIsModuleMini = Array("DDD", "DED", "DDD", "D", dirt, "E", pearl)
        recipe_flIsModuleNorm = Array("DDD", "DMD", "DDD", "D", dirt, "M", miniMod)
        recipe_flIsModuleHuge = Array("DDD", "DMD", "DDD", "D", dirt, "M", normMod)
    }

}
