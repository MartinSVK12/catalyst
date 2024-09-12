package sunsetsatellite.catalyst.core.util;

public interface ISideInteractable {

	default boolean needsItemToShowOutline() {
		return true;
	}

	default boolean alwaysShowOutlineWhenHeld() {
		return false;
	}
}
