package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.net.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.PacketOpenGui;

@Mixin(value = Packet.class, remap = false)
public abstract class PacketMixin {
	@Shadow
	static void addIdClassMapping(int id, boolean clientbound, boolean serverbound, Class<? extends Packet> packetClass) {
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void registerPacket(CallbackInfo ci){
		addIdClassMapping(231, true, false, PacketOpenGui.class);
	}
}
