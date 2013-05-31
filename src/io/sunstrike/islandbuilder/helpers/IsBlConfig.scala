package io.sunstrike.islandbuilder.helpers

import net.minecraftforge.common.Configuration
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.blocks.FlIsBuilder
import net.minecraft.block.material.Material
import io.sunstrike.islandbuilder.items.FlIsModule
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.block.Block

/*
 * IsBlConfig
 * io.sunstrike.islandbuilder.helpers
 */

/**
 * Configuration manager
 *
 * @author Sunstrike
 */
object IsBlConfig {

    //=== Defaults/Vars =============================
    var block_FlIsBuilder_id = 1000
    var item_FlIsModule_id = 1000

    //=== Functions =================================
    /**
     * Loads a given Configuration object to fill values in the configuration.
     *
     * @param config Forge Configuration object
     */
    def loadForgeConfig(config:Configuration) {
        config.load()

        block_FlIsBuilder_id = config.getBlock("FlIsBuilder", block_FlIsBuilder_id).getInt
        block_FlIsBuilder = new FlIsBuilder(block_FlIsBuilder_id, Material.anvil)

        item_FlIsModule_id = config.getBlock("FlIsModule", item_FlIsModule_id).getInt
        item_FlIsModule = new FlIsModule(item_FlIsModule_id)

        config.save()

        logger.info("[IsBlConfig] Loaded configuration.")
    }

}
