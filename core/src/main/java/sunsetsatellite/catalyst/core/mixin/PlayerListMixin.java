package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.core.util.mp.PacketBlockNetworkData;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.UUID;

@Mixin(value = PlayerList.class,remap = false)
public class PlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "getPlayerForLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/net/handler/PacketHandlerServer;kickPlayer(Ljava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void getPlayerForLogin(PacketHandlerLogin handler, String username, UUID uuid, CallbackInfoReturnable<PlayerServer> cir, @Local(name = "player") PlayerServer player) {
        if(player.uuid == null && uuid == null){
            cir.setReturnValue(new PlayerServer(server, server.getDimensionWorld(0), username, uuid, new ServerPlayerController(server.getDimensionWorld(0))));
        }
    }

    @Inject(method = "sendPlayerToOtherDimension", at = @At("TAIL"))
    public void sendPlayerToOtherDimension(PlayerServer playerServer, int targetDim, DyeColor portalColor, boolean generatePortal, CallbackInfo ci){
        if (playerServer.world != null) {
            for (Network network : NetworkManager.getNetsForDimension(playerServer.world.dimension.id)) {
                network.update();
            }
            NetworkHandler.sendToPlayer(playerServer, new PacketBlockNetworkData(playerServer.world));
        }
    }

}
