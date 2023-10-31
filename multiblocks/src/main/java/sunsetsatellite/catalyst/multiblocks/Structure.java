package sunsetsatellite.catalyst.multiblocks;

import com.mojang.nbt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.CatalystMultiblocks;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Vec3i;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Structure {
    public String modId;
    public Class<?>[] modClasses;
    public String translateKey;
    public String filePath;
    public CompoundTag data;
    public boolean placeAir;
    public boolean replaceBlocks;

    public static HashMap<String,Structure> internalStructures = new HashMap<>();

    public Structure(String modId, Class<?>[] modClasses, String translateKey, CompoundTag data, boolean placeAir, boolean replaceBlocks){
        this.modId = modId;
        this.modClasses = modClasses;
        this.translateKey = "structure."+modId+"."+translateKey+".name";
        this.data = data;
        this.filePath = null;
        this.placeAir = placeAir;
        this.replaceBlocks = replaceBlocks;
    }

    public Structure(String modId, Class<?>[] modClasses, String translateKey, String filePath, boolean placeAir, boolean replaceBlocks){
        this.modId = modId;
        this.modClasses = modClasses;
        this.translateKey = "structure."+modId+"."+translateKey+".name";
        this.placeAir = placeAir;
        this.replaceBlocks = replaceBlocks;
        loadFromNBT(filePath);
    }

    public String getTranslatedName(){
        return I18n.getInstance().translateNameKey(this.translateKey);
    }

    public String getFullFilePath(){
        if(filePath != null){
            return "/assets/"+modId+"/structures/"+filePath+".nbt";
        } else {
            return null;
        }
    }

    public boolean placeStructure(World world, int originX, int originY, int originZ){
        Vec3i origin = new Vec3i(originX,originY,originZ);
        ArrayList<BlockInstance> blocks = getBlocks(origin);
        blocks.add(getOrigin(origin));
        for (BlockInstance block : blocks) {
            if (!replaceBlocks && world.getBlockId(block.pos.x, block.pos.y, block.pos.z) != 0) {
                return false;
            }
        }
        for (BlockInstance block : blocks) {
            world.setBlockAndMetadataWithNotify(block.pos.x,block.pos.y,block.pos.z,block.block.id,block.meta);
        }
        return true;
    }

    public boolean placeStructure(World world, int originX, int originY, int originZ, String direction){
        Direction dir = Direction.getFromName(direction);
        if(dir == null) return false;
        Vec3i origin = new Vec3i(originX,originY,originZ);
        ArrayList<BlockInstance> blocks = getBlocks(origin,dir);
        blocks.add(getOrigin(origin));
        for (BlockInstance block : blocks) {
            if (!replaceBlocks && world.getBlockId(block.pos.x, block.pos.y, block.pos.z) != 0) {
                return false;
            }
        }
        for (BlockInstance block : blocks) {
            world.setBlockAndMetadataWithNotify(block.pos.x,block.pos.y,block.pos.z,block.block.id,block.meta);
        }
        return true;
    }

    public BlockInstance getOrigin(){
        CompoundTag blockTag = data.getCompound("Origin");
        int meta = blockTag.getInteger("meta");
        int id = getBlockId(blockTag);
        Block block = Block.getBlock(id);
        return new BlockInstance(block, new Vec3i(),meta,null);
    }

    public BlockInstance getOrigin(Vec3i origin){
        CompoundTag blockTag = data.getCompound("Origin");
        int meta = blockTag.getInteger("meta");
        int id = getBlockId(blockTag);
        Block block = Block.getBlock(id);
        return new BlockInstance(block, origin,meta,null);
    }

    public BlockInstance getOrigin(World world, Vec3i origin){
        CompoundTag blockTag = data.getCompound("Origin");
        Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
        int meta = blockTag.getInteger("meta");
        int id = getBlockId(blockTag);
        Block block = Block.getBlock(id);
        return new BlockInstance(block,pos,meta,world.getBlockTileEntity(pos.x, pos.y, pos.z));
    }

    public ArrayList<BlockInstance> getTileEntities(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("TileEntities").getValues()) {
            CompoundTag tileEntity = (CompoundTag) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos"));
            int meta = tileEntity.getInteger("meta");
            int id = getBlockId(tileEntity);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("TileEntities").getValues()) {
            CompoundTag tileEntity = (CompoundTag) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).add(origin);
            int meta = tileEntity.getInteger("meta");
            int id = getBlockId(tileEntity);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("TileEntities").getValues()) {
            CompoundTag tileEntity = (CompoundTag) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).add(origin);
            int meta = tileEntity.getInteger("meta");
            int id = getBlockId(tileEntity);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,world.getBlockTileEntity(pos.x, pos.y, pos.z));
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin, Direction dir){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Blocks").getValues()) {
            CompoundTag blockTag = (CompoundTag) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos")).rotate(origin,dir);
            int meta = blockTag.getInteger("meta");
            if(meta != -1){
                if(dir == Direction.Z_NEG){
                    meta = Direction.getDirectionFromSide(meta).getOpposite().getSide();
                } else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
                    Direction blockDir = Direction.getDirectionFromSide(meta);
                    blockDir = blockDir == Direction.X_NEG || blockDir == Direction.X_POS ? blockDir.rotate(1).getOpposite() : blockDir.rotate(1);
                    meta = dir == Direction.X_NEG ? blockDir.getSide() : blockDir.getOpposite().getSide();
                }
            }
            int id = getBlockId(blockTag);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,world.getBlockTileEntity(pos.x, pos.y, pos.z));
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getBlocks(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Blocks").getValues()) {
            CompoundTag blockTag = (CompoundTag) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
            int meta = blockTag.getInteger("meta");
            int id = getBlockId(blockTag);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getBlocks(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Blocks").getValues()) {
            CompoundTag blockTag = (CompoundTag) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos")).add(origin);
            int meta = blockTag.getInteger("meta");
            int id = getBlockId(blockTag);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getBlocks(Vec3i origin, Direction dir){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Blocks").getValues()) {
            CompoundTag blockTag = (CompoundTag) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos")).rotate(origin,dir);
            int meta = blockTag.getInteger("meta");
            if(meta != -1){
                if(dir == Direction.Z_NEG){
                    meta = Direction.getDirectionFromSide(meta).getOpposite().getSide();
                } else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
                    Direction blockDir = Direction.getDirectionFromSide(meta);
                    blockDir = blockDir == Direction.X_NEG || blockDir == Direction.X_POS ? blockDir.rotate(1).getOpposite() : blockDir.rotate(1);
                    meta = dir == Direction.X_NEG ? blockDir.getSide() : blockDir.getOpposite().getSide();
                }
            }
            int id = getBlockId(blockTag);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Substitutions").getValues()) {
            CompoundTag sub = (CompoundTag) tag;
            Vec3i pos = new Vec3i(sub.getCompound("pos"));
            int meta = sub.getInteger("meta");
            int id = getBlockId(sub);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Substitutions").getValues()) {
            CompoundTag sub = (CompoundTag) tag;
            Vec3i pos = new Vec3i(sub.getCompound("pos")).add(origin);
            int meta = sub.getInteger("meta");
            int id = getBlockId(sub);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(Vec3i origin, Direction dir){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Tag<?> tag : data.getCompound("Substitutions").getValues()) {
            CompoundTag tileEntity = (CompoundTag) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).rotate(origin, dir);
            int meta = tileEntity.getInteger("meta");
            if(meta != -1){
                if(dir == Direction.Z_NEG){
                    meta = Direction.getDirectionFromSide(meta).getOpposite().getSide();
                } else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
                    Direction blockDir = Direction.getDirectionFromSide(meta);
                    blockDir = blockDir == Direction.X_NEG || blockDir == Direction.X_POS ? blockDir.rotate(1).getOpposite() : blockDir.rotate(1);
                    meta = dir == Direction.X_NEG ? blockDir.getSide() : blockDir.getOpposite().getSide();
                }
            }
            int id = getBlockId(tileEntity);
            Block block = Block.getBlock(id);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public static int getBlockId(CompoundTag block){
        Tag<?> nbt = block.getTag("id");
        if(nbt instanceof IntTag){
            return ((IntTag) nbt).getValue();
        } else if (nbt instanceof StringTag) {
            String[] args = ((StringTag) nbt).getValue().split(":");
            try {
                Class<?> clazz = Class.forName(args[0]);
                Field field = clazz.getDeclaredField(args[1]);
                Block b = (Block) field.get(null);
                return b.id;
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | ClassCastException e) {
               e.printStackTrace();
               return 0;
            }

        }
        return 0;
    }

    protected void loadFromNBT(String name) {
        try (InputStream resource = this.getClass().getResourceAsStream("/assets/" + modId + "/structures/" + name + ".nbt")) {
            if (resource != null) {
                this.data = NbtIo.readCompressed(resource);
                CatalystMultiblocks.LOGGER.info(String.format("Structure '%s' contains %d blocks.",name,this.data.getCompound("Blocks").getValues().size()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBlockFieldName(Block item){
        try{
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(Block.class.getDeclaredFields()));
            for (Field field : fields) {
                if(field.getType().isAssignableFrom(Block.class) && Modifier.isStatic(field.getModifiers())){
                    field.setAccessible(true);
                    Block fieldItem = (Block) field.get(null);
                    if(fieldItem.equals(item)){
                        return "Block."+field.getName();
                    }
                }
            }
            for (Class<?> aClass : modClasses) {
                fields = new ArrayList<>(Arrays.asList(aClass.getDeclaredFields()));
                for (Field field : fields) {
                    if (field.getType().isAssignableFrom(Block.class) && Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        Block fieldItem = (Block) field.get(null);
                        if (fieldItem.equals(item)) {
                            return aClass.getName()+"." + field.getName();
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
