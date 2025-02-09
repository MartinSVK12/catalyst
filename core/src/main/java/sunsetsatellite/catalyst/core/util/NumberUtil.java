package sunsetsatellite.catalyst.core.util;

public class NumberUtil {
    public static String[] shortSuffixes = new String[]{"k","M","B","T","qd","Qn"};
	public static String[] shortMetricSuffixes = new String[]{"k","M","G","T","P","E"};
    public static String[] longSuffixes = new String[]{"thousand","million","billion","trillion","quadrillion","quintillion"};
	public static String[] longMetricSuffixes = new String[]{"kilo","Mega","Giga","Tera","Peta","Exa"};

    public static String format(double value){
        if(Double.isInfinite(value)){
            return "inf";
        }
        for (String shortSuffix : shortSuffixes) {
            if (value >= 1000) {
                value /= 1000;
                if (value < 1000) {
                    return String.format("%.1f%s", value, shortSuffix);
                }
            } else {
                return String.valueOf((long)value);
            }
        }
        return String.valueOf((long)value);
    }

	public static String formatMetric(double value){
		if(Double.isInfinite(value)){
			return "inf";
		}
		for (String shortSuffix : shortMetricSuffixes) {
			if (value >= 1000) {
				value /= 1000;
				if (value < 1000) {
					return String.format("%.1f%s", value, shortSuffix);
				}
			} else {
				return String.valueOf((long)value);
			}
		}
		return String.valueOf((long)value);
	}

	public static String formatTime(double seconds){
		if(seconds < 60){
			return ((long) seconds) + "s";
		} else if(seconds < 3600){
			return ((long) seconds/60) + "min";
		} else if(seconds < 86400) {
			return ((long) seconds/3600) + "h";
		} else if(seconds < 86400 * 365) {
			return ((long) seconds/86400) + "d";
		} else {
			return ((long) seconds/(86400 * 365)) + "yr";
		}
	}
}
