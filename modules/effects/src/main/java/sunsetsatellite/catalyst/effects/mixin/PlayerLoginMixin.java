package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.net.SyncEffectContainerForEntityNetworkMessage;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public class PlayerLoginMixin {

	@Shadow
	@Final
	private MinecraftServer mcServer;

	@Shadow
	public NetworkManager netManager;

	@Inject(method = {"doLogin"}, at = {@At(value = "TAIL")})
	public void doLogin(PacketLogin loginPacket, CallbackInfo ci) {
		PlayerServer player = this.mcServer.playerList.getPlayerEntity(loginPacket.username);

		if (player != null) {
			NetworkHandler.sendToPlayer(player, new SyncEffectContainerForEntityNetworkMessage(player));
		}
	}

}
