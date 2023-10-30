package sunsetsatellite.catalyst.util;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.*;

import java.util.Objects;

public class NBTEditCommand extends Command {
    public NBTEditCommand() {
        super("nbtedit", "nbt");
    }

    public static CompoundTag copy;

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
        if(commandSender instanceof PlayerCommandSender){
            if(Objects.equals(args[0], "hand")){
                if(Objects.equals(args[1],"copy")){
                    if(commandSender.getPlayer().inventory.getCurrentItem() != null){
                        copy = commandSender.getPlayer().inventory.getCurrentItem().getData();
                        commandSender.sendMessage("Copied!");
                        return true;
                    }
                }
                if(Objects.equals(args[1],"paste")){
                    if(copy == null){
                        throw new CommandError("Copy some data first!");
                    }
                    if(commandSender.getPlayer().inventory.getCurrentItem() != null){
                        commandSender.getPlayer().inventory.getCurrentItem().setData(copy);
                        commandSender.sendMessage("Pasted!");
                        return true;
                    }
                }
                if(Objects.equals(args[1],"set")){
                    if(args.length > 4){
                        ItemStack stack = commandSender.getPlayer().inventory.getCurrentItem();
                        switch (args[2]){
                            case "integer":
                                stack.getData().putInt(args[3],Integer.parseInt(args[4]));
                                return true;
                            case "string":
                                stack.getData().putString(args[3],args[4]);
                                return true;
                            case "byte":
                                stack.getData().putByte(args[3],Byte.parseByte(args[4]));
                                return true;
                            case "bool":
                                stack.getData().putBoolean(args[3],Boolean.parseBoolean(args[4]));
                                return true;
                            case "double":
                                stack.getData().putDouble(args[3],Double.parseDouble(args[4]));
                                return true;
                            case "float":
                                stack.getData().putDouble(args[3],Float.parseFloat(args[4]));
                                return true;
                            case "long":
                                stack.getData().putDouble(args[3],Long.parseLong(args[4]));
                                return true;
                            case "short":
                                stack.getData().putDouble(args[3],Short.parseShort(args[4]));
                                return true;
                        }
                    }
                }
                if(Objects.equals(args[1],"get")) {
                    if (args.length > 2) {
                        ItemStack stack = commandSender.getPlayer().inventory.getCurrentItem();
                        switch (args[2]) {
                            case "integer":
                                commandSender.sendMessage(String.valueOf(stack.getData().getInteger(args[3])));
                                return true;
                            case "string":
                                commandSender.sendMessage(stack.getData().getString(args[3]));
                                return true;
                            case "byte":
                                commandSender.sendMessage(String.valueOf(stack.getData().getByte(args[3])));
                                return true;
                            case "bool":
                                commandSender.sendMessage(String.valueOf(stack.getData().getBoolean(args[3])));
                                return true;
                            case "double":
                                commandSender.sendMessage(String.valueOf(stack.getData().getDouble(args[3])));
                                return true;
                            case "float":
                                commandSender.sendMessage(String.valueOf(stack.getData().getFloat(args[3])));
                                return true;
                            case "long":
                                commandSender.sendMessage(String.valueOf(stack.getData().getLong(args[3])));
                                return true;
                            case "short":
                                commandSender.sendMessage(String.valueOf(stack.getData().getShort(args[3])));
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        if (commandSender instanceof PlayerCommandSender) {
            commandSender.sendMessage("/nbt hand copy/paste");
            commandSender.sendMessage("/nbt hand set integer/string/byte/bool/double/float/long/short <name> <value>");
        }
    }
}
