package io.sunstrike.islandbuilder

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.Mod.{PostInit, Init, PreInit}
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import io.sunstrike.islandbuilder.helpers.{ForgeRegistration, IsBlConfig}
import net.minecraftforge.common.Configuration
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler
import io.sunstrike.islandbuilder.network.PacketHandler

/*
 * IslandBuilder
 * io.sunstrike.islandbuilder
 */

/**
 * Core mod container
 *
 * @author Sunstrike
 */
// @Mod filled in from package object values
@Mod(modid = modId,
    name = modName,
    version = modVersion,
    dependencies = dependencies,
    modLanguage = modLanguage)
@NetworkMod(clientSideRequired = true,
    serverSideRequired = false,
    channels = Array(netChannel),
    packetHandler = classOf[PacketHandler])
object IslandBuilder {

    /**
     * FML Preinit
     *
     * Loads configuration
     *
     * @param event FML event
     */
    @PreInit
    def preInit(event:FMLPreInitializationEvent) {
        IsBlConfig.loadForgeConfig(new Configuration(event.getSuggestedConfigurationFile))
    }

    /**
     * FML Init
     *
     * Registers blocks/items
     *
     * @param event FML event
     */
    @Init
    def init(event:FMLInitializationEvent) {
        ForgeRegistration.registerAll
    }

    /**
     * FML Postinit
     *
     * @param event FML event
     */
    @PostInit
    def postInit(event:FMLPostInitializationEvent) {

    }

}
