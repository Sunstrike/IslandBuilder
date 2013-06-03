package io.sunstrike.islandbuilder.network

import net.minecraft.world.World
import net.minecraft.network.packet.Packet250CustomPayload
import java.io.{ByteArrayInputStream, DataInputStream, DataOutputStream, ByteArrayOutputStream}
import net.minecraftforge.common.DimensionManager

/*
 * PacketCrateAssemUpdate
 * io.sunstrike.islandbuilder.network
 */

/**
 * Class description not provided.
 *
 * @author Sunstrike
 */
class PacketCrateAssemUpdate(w:World, xPos:Int, yPos:Int, zPos:Int, rad:Int, hei:Int) {

    val packetId = 1

    val world = w
    val x = xPos
    val y = yPos
    val z = zPos
    val radius = rad
    val height = hei

    def toPacket250CustomPayload:Packet250CustomPayload = {
        val bos = new ByteArrayOutputStream()
        val outputStream = new DataOutputStream(bos)
        try {
            outputStream.writeInt(packetId)
            outputStream.writeInt(world.getWorldInfo.getDimension)
            outputStream.writeInt(x)
            outputStream.writeInt(y)
            outputStream.writeInt(z)
            outputStream.writeInt(radius)
            outputStream.writeInt(height)
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

object PacketCrateAssemUpdate {

    def fromPacket250CustomPayload(packet:Packet250CustomPayload): PacketCrateAssemUpdate = {
        val bis = new DataInputStream(new ByteArrayInputStream(packet.data))
        var world = 0
        var x = 0
        var y = 0
        var z = 0
        var radius = 0
        var height = 0
        try {
            bis.readInt
            world = bis.readInt
            x = bis.readInt
            y = bis.readInt
            z = bis.readInt
            radius = bis.readInt
            height = bis.readInt
        } catch {
            case ex:Exception => ex.printStackTrace()
        }

        new PacketCrateAssemUpdate(DimensionManager.getWorld(world), x, y, z, radius, height)
    }

}
