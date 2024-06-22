package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.*;
import turniplabs.halplibe.helper.NetworkHelper;


public class Catalyst implements ModInitializer {
    public static final String MOD_ID = "catalyst";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Registry<MpGuiEntry> GUIS = new Registry<>();

	static {
		NetworkHelper.register(PacketOpenGui.class,false,true);
	}

    @Override
    public void onInitialize() {
        LOGGER.info("Catalyst initialized.");
    }

	public static void displayGui(EntityPlayer player, IInventory inventory, ItemStack stack){
		((IMpGui)player).displayCustomGUI(inventory,stack);
	}


	public static void displayGui(EntityPlayer player, TileEntity tileEntity, String id){
		((IMpGui)player).displayCustomGUI(tileEntity, id);
	}

	public static Pair<Direction,BlockSection> getBlockSurfaceClickPosition(World world, EntityPlayer player){
		if (!world.isClientSide) {
			HitResult hit = Minecraft.getMinecraft(Catalyst.class).objectMouseOver;
			if(hit.hitType == HitResult.HitType.TILE){
				Direction dir = Direction.getDirectionFromSide(hit.side.getId());
				Vec3f vec3f = new Vec3f(hit.location.xCoord,hit.location.yCoord,hit.location.zCoord);
				Vec2f clickPosition = vec3f.subtract(vec3f.copy().floor()).abs().set(hit.side.getAxis(),0).toVec2f();
				switch (hit.side) {
					case NORTH:
						clickPosition.x = 1-clickPosition.x;
						break;
					case EAST: {
						double temp1 = clickPosition.y;
						double temp2 = clickPosition.x;
						clickPosition.x = 1-temp1;
						clickPosition.y = temp2;
						break;
					}
					case SOUTH:
						//no change needed
						break;
					case WEST: {
						double temp1 = clickPosition.y;
						double temp2 = clickPosition.x;
						clickPosition.x = temp1;
						clickPosition.y = temp2;
						break;
					}
				}
				return Pair.of(dir,BlockSection.getClosestBlockSection(clickPosition));
			}
		}
		return null;
	}

	public static Side calculatePlayerFacing(float rotation) {
		return Side.values()[(2 + ((MathHelper.floor_double((double) ((rotation * 4F) / 360F) + 0.5D) + 2) & 3))];
	}
}
