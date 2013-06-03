package io.sunstrike.islandbuilder.network

import net.minecraft.world.World
import net.minecraft.network.packet.Packet250CustomPayload
import java.io.{DataInputStream, ByteArrayInputStream, DataOutputStream, ByteArrayOutputStream}
import net.minecraftforge.common.DimensionManager

/*
 * PacketGuiButton
 * io.sunstrike.islandbuilder.network
 */

/**
 * Packet for button interactions in GUIs.
 *
 * @author Sunstrike
 */
class PacketGuiButton(w:World, xPos:Int, yPos:Int, zPos:Int, buttonId:Int, shift:Boolean) {

    val packetId = 0

    val world = w
    val x = xPos
    val y = yPos
    val z = zPos
    val button = buttonId
    val shifting = shift

    def toPacket250CustomPayload:Packet250CustomPayload = {
        val bos = new ByteArrayOutputStream()
        val outputStream = new DataOutputStream(bos)
        try {
            outputStream.writeInt(packetId)
            outputStream.writeInt(world.getWorldInfo.getDimension)
            outputStream.writeInt(x)
            outputStream.writeInt(y)
            outputStream.writeInt(z)
            outputStream.writeInt(button)
            outputStream.writeBoolean(shifting)
        } catch {
            case ex:Exception => ex.printStackTrace()
        }

        val packet = new Packet250CustomPayload()
        packet.channel = io.sunstrike.islandbuilder.netChannel
        packet.data = bos.toByteArray
        packet.length = bos.size

        packet
    }

}

object PacketGuiButton {

    def fromPacket250CustomPayload(packet:Packet250CustomPayload): PacketGuiButton = {
        val bis = new DataInputStream(new ByteArrayInputStream(packet.data))
        var world = 0
        var x = 0
        var y = 0
        var z = 0
        var button = 0
        var shifting = false
        try {
            bis.readInt()
            world = bis.readInt()
            x = bis.readInt()
            y = bis.readInt()
            z = bis.readInt()
            button = bis.readInt()
            shifting = bis.readBoolean()
        } catch {
            case ex:Exception => ex.printStackTrace()
        }

        new PacketGuiButton(DimensionManager.getWorld(world), x, y, z, button, shifting)
    }

}
