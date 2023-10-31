package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.PlayerCommandSender;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.Vec3i;

public class StructureCommand extends Command {
    public StructureCommand(String name, String... alts) {
        super(name, alts);
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
        World world = Minecraft.getMinecraft(Minecraft.class).theWorld;
        if(commandSender instanceof PlayerCommandSender){
            EntityPlayer player = commandSender.getPlayer();
            if(args.length > 0){
                if (args[0].equals("list")) {
                    commandSender.sendMessage("List of internal structures:");
                    commandSender.sendMessage(Structure.internalStructures.keySet().toString());
                    commandSender.sendMessage("List of multiblocks:");
                    commandSender.sendMessage(Multiblock.multiblocks.keySet().toString());
                    return true;
                } else if(args.length == 3){
                    if(args[0].equals("place")){
                        if (Structure.internalStructures.containsKey(args[1])) {
                            Structure struct = Structure.internalStructures.get(args[1]);
                            try {
                                boolean r = struct.placeStructure(player.world, (int) player.x, (int) player.y, (int) player.z, args[2]);
                                if (r) {
                                    commandSender.sendMessage(String.format("Structure '%s' placed at %s facing %s!", args[1], new Vec3i((int) player.x, (int) player.y, (int) player.z), args[2]));
                                }
                                return r;
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage("Invalid coordinates provided!");
                                return true;
                            }
                        } else if (Multiblock.multiblocks.containsKey(args[1])) {
                            Structure struct = Multiblock.multiblocks.get(args[1]);
                            try {
                                boolean r = struct.placeStructure(player.world, (int) player.x, (int) player.y, (int) player.z, args[2]);
                                if (r) {
                                    commandSender.sendMessage(String.format("Structure '%s' placed at %s facing %s!", args[1], new Vec3i((int) player.x, (int) player.y, (int) player.z), args[2]));
                                }
                                return r;
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage("Invalid coordinates provided!");
                                return true;
                            }
                        } else {
                            commandSender.sendMessage("Invalid structure!");
                        }
                    }
                } else if(args.length == 6) {
                    if(args[0].equals("place")){
                        if (Structure.internalStructures.containsKey(args[1])) {
                            Structure struct = Structure.internalStructures.get(args[1]);
                            try {
                                boolean r = struct.placeStructure(player.world, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
                                if (r) {
                                    commandSender.sendMessage(String.format("Structure '%s' placed at %s facing %s!", args[1], new Vec3i(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), args[5]));
                                }
                                return r;
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage("Invalid coordinates provided!");
                                return true;
                            }
                        } else if (Multiblock.multiblocks.containsKey(args[1])) {
                            Structure struct = Multiblock.multiblocks.get(args[1]);
                            try {
                                boolean r = struct.placeStructure(player.world, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
                                if (r) {
                                    commandSender.sendMessage(String.format("Structure '%s' placed at %s facing %s!", args[1], new Vec3i(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), args[5]));
                                }
                                return r;
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage("Invalid coordinates provided!");
                                return true;
                            }
                        } else {
                            commandSender.sendMessage("Invalid structure!");
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean opRequired(String[] args) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        if (commandSender instanceof PlayerCommandSender) {
            commandSender.sendMessage("/structure place <name> <x> <y> <z> <r>");
            commandSender.sendMessage("/structure place <name> <r>");
            commandSender.sendMessage("/structure list");
        }
    }
}
