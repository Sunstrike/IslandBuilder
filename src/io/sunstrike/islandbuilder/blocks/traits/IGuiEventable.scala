package io.sunstrike.islandbuilder.blocks.traits

import io.sunstrike.islandbuilder.network.PacketGuiButton

/*
 * IGuiEventable
 * io.sunstrike.islandbuilder.blocks.traits
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
trait IGuiEventable {

    def receivePacketGuiButton(packet: PacketGuiButton)

}
