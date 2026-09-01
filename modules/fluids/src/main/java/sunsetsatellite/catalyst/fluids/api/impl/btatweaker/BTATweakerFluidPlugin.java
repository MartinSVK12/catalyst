package sunsetsatellite.catalyst.fluids.api.impl.btatweaker;

import turing.btatweaker.BTATweakerEntrypoint;
import turing.btatweaker.lua.ScriptGlobals;

public class BTATweakerFluidPlugin implements BTATweakerEntrypoint {
	@Override
	public void addGlobals(ScriptGlobals globals) {
		globals.addGlobalLib(new FluidLuaLib());
	}
}
