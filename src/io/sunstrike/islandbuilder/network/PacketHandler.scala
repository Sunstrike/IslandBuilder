package io.sunstrike.islandbuilder.network

import cpw.mods.fml.common.network.{Player, IPacketHandler}
import net.minecraft.network.INetworkManager
import net.minecraft.network.packet.Packet250CustomPayload
import io.sunstrike.islandbuilder.blocks.traits.IGuiEventable
import io.sunstrike.islandbuilder.blocks.CrateAssemblerTile
import io.sunstrike.islandbuilder.helpers.ObjRepo._
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side
import java.io.{ByteArrayInputStream, DataInputStream}

/*
 * PacketHandler
 * io.sunstrike.islandbuilder.network
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
class PacketHandler extends IPacketHandler {

    def onPacketData(manager: INetworkManager, packet: Packet250CustomPayload, player: Player) {
        parsePacketId(packet) match {
            case PacketHandler.netId_GuiButton => handleGuiButton(packet)
            case PacketHandler.netId_Invalid => throw new RuntimeException("Invalid packet sent to PacketHandler!")
        }
    }

    private def parsePacketId(packet:Packet250CustomPayload):Int = {
        val bis = new DataInputStream(new ByteArrayInputStream(packet.data))
        var id = PacketHandler.netId_Invalid
        try {
            id = bis.readInt()
        } catch {
            case ex:Exception => ex.printStackTrace()
        }
        id
    }

    private def handleGuiButton(pk250: Packet250CustomPayload) {
        val packet = PacketGuiButton.fromPacket250CustomPayload(pk250)
        val te = packet.world.getBlockTileEntity(packet.x, packet.y, packet.z)
        if (te != null && te.isInstanceOf[IGuiEventable]) {
            te.asInstanceOf[IGuiEventable].receivePacketGuiButton(packet)
        }
    }

}

object PacketHandler {

    final val netId_Invalid = -1
    final val netId_GuiButton = 0

}
