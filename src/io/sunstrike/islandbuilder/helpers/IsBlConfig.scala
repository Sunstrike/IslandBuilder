package io.sunstrike.islandbuilder.helpers

import net.minecraftforge.common.Configuration
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.blocks.{CrateAssembler, EmptyCrate, FlIsBuilder}
import io.sunstrike.islandbuilder.items.FlIsModule

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
    var block_CrateAssembler_id = 1001
    var block_EmptyCrate_id = 1002
    var item_FlIsModule_id = 4000

    //=== Functions =================================
    /**
     * Loads a given Configuration object to fill values in the configuration.
     *
     * @param config Forge Configuration object
     */
    def loadForgeConfig(config:Configuration) {
        config.load()

        block_FlIsBuilder_id = config.getBlock("FlIsBuilder", block_FlIsBuilder_id).getInt
        block_FlIsBuilder = new FlIsBuilder(block_FlIsBuilder_id)

        block_CrateAssembler_id = config.getBlock("CrateAssembler", block_CrateAssembler_id).getInt
        block_CrateAssembler = new CrateAssembler(block_CrateAssembler_id)

        block_EmptyCrate_id = config.getBlock("EmptyCrate", block_EmptyCrate_id).getInt
        block_EmptyCrate = new EmptyCrate(block_EmptyCrate_id)

        item_FlIsModule_id = config.getItem("FlIsModule", item_FlIsModule_id).getInt
        item_FlIsModule = new FlIsModule(item_FlIsModule_id)

        config.save()

        logger.info("[IsBlConfig] Loaded configuration.")
    }

}
