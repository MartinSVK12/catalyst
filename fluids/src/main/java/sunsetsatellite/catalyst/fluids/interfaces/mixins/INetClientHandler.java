package sunsetsatellite.catalyst.fluids.interfaces.mixins;


import sunsetsatellite.catalyst.fluids.mp.packets.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketUpdateClientFluidRender;

public interface INetClientHandler {

    void handleSetFluidSlot(PacketSetFluidSlot packetSetFluidSlot);


    void handleUpdateClientFluidRender(PacketUpdateClientFluidRender packetUpdateClientFluidRender);
}
