package sunsetsatellite.catalyst.energy.api;


import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.HashMap;
import java.util.Map;

public class LookupFuelEnergy {
    private static final LookupFuelEnergy fuelBase = new LookupFuelEnergy();
    protected Map<Integer, Integer> fuelList = new HashMap<>();

    public static LookupFuelEnergy fuelEnergy() {
        return fuelBase;
    }

    protected LookupFuelEnergy() {
        this.addFuelEntry(Block.planksOak.id, 3);
        this.addFuelEntry(Block.planksOakPainted.id, 3);
        this.addFuelEntry(Block.stairsPlanksOak.id, 3);
        this.addFuelEntry(Block.slabPlanksOak.id, 1);
        this.addFuelEntry(Block.slabPlanksOakPainted.id, 1);
        this.addFuelEntry(Block.fencePlanksOak.id, 3);
        this.addFuelEntry(Block.fencePlanksOakPainted.id, 3);
        this.addFuelEntry(Block.fencegatePlanksOak.id, 3);
        this.addFuelEntry(Block.fencegatePlanksOakPainted.id, 3);
        this.addFuelEntry(Block.logOak.id, 3);
        this.addFuelEntry(Block.logBirch.id, 3);
        this.addFuelEntry(Block.logPine.id, 3);
        this.addFuelEntry(Block.logCherry.id, 3);
        this.addFuelEntry(Block.logOakMossy.id, 3);
        this.addFuelEntry(Block.logEucalyptus.id, 3);
        this.addFuelEntry(Block.saplingOak.id, 1);
        this.addFuelEntry(Block.saplingPine.id, 1);
        this.addFuelEntry(Block.saplingBirch.id, 1);
        this.addFuelEntry(Block.saplingCherry.id, 1);
        this.addFuelEntry(Block.saplingEucalyptus.id, 1);
        this.addFuelEntry(Block.saplingShrub.id, 1);
        this.addFuelEntry(Block.saplingOakRetro.id, 1);
        this.addFuelEntry(Block.deadbush.id, 1);
        this.addFuelEntry(Block.spinifex.id, 1);
        this.addFuelEntry(Block.blockCoal.id, 64);
        this.addFuelEntry(Block.blockCharcoal.id, 64);
        this.addFuelEntry(Block.blockNetherCoal.id, 128);
        this.addFuelEntry(Item.stick.id, 1);
        this.addFuelEntry(Item.toolPickaxeWood.id, 5);
        this.addFuelEntry(Item.toolSwordWood.id, 5);
        this.addFuelEntry(Item.toolAxeWood.id, 5);
        this.addFuelEntry(Item.toolShovelWood.id, 5);
        this.addFuelEntry(Item.toolHoeWood.id, 5);
        this.addFuelEntry(Item.toolBow.id, 3);
        this.addFuelEntry(Item.toolFishingrod.id, 3);
        this.addFuelEntry(Item.boat.id, 3);
        this.addFuelEntry(Item.doorOak.id, 3);
        this.addFuelEntry(Item.sign.id, 3);
        this.addFuelEntry(Item.bowl.id, 3);
        this.addFuelEntry(Item.coal.id, 8);
        this.addFuelEntry(Item.nethercoal.id, 16);
    }

    public void addFuelEntry(int id, int energyYield) {
        this.fuelList.put(id, energyYield);
    }

    public int getEnergyYield(int id) {
        return this.fuelList.get(id) == null ? 0 : this.fuelList.get(id);
    }

    public Map<Integer, Integer> getFuelList() {
        return this.fuelList;
    }

}
