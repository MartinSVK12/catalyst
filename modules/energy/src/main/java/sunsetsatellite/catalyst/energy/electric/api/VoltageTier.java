package sunsetsatellite.catalyst.energy.electric.api;

import net.minecraft.core.net.command.TextFormatting;

public enum VoltageTier {
	NONE("No Voltage",Integer.MIN_VALUE,0,TextFormatting.BLACK,0x000000),
	ULV("Ultra Low Voltage", 1, 8, TextFormatting.GRAY, 0x555555),
	LV("Low Voltage", 9, 32, TextFormatting.RED, 0xFF5555),
	MV("Medium Voltage", 33, 128, TextFormatting.ORANGE, 0xFFAA00),
	HV("High Voltage", 129, 512, TextFormatting.YELLOW, 0xFFFF55),
	EV("Extreme Voltage", 513, 2048, TextFormatting.GREEN, 0x55FF55),
	IV("Insane Voltage", 2048,8192, TextFormatting.LIGHT_BLUE, 	0x5555FF),
	UV("Ultimate Voltage", 8192, 32767, TextFormatting.BLUE, 0x0000AA),
	OV("Over Voltage", 32768, 65535, TextFormatting.PURPLE, 0xAA00AA),
	MAX("Maximum Voltage", 65536, Integer.MAX_VALUE, TextFormatting.MAGENTA, 0xFF55FF);

	VoltageTier(String voltageName, int minVoltage, int maxVoltage, TextFormatting textColor, int color) {
		this.voltageName = voltageName;
		this.minVoltage = minVoltage;
		this.maxVoltage = maxVoltage;
		this.textColor = textColor;
		this.color = color;
	}

	public final String voltageName;
	public final int minVoltage;
	public final int maxVoltage;
	public final TextFormatting textColor;
	public final int color;

	public static VoltageTier get(int voltage) {
		for (VoltageTier tier : values()) {
			if (voltage >= tier.minVoltage && voltage <= tier.maxVoltage) {
				return tier;
			}
		}
		return NONE;
	}
}
