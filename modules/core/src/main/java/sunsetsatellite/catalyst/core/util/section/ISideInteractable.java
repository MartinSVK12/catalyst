package sunsetsatellite.catalyst.core.util.section;

public interface ISideInteractable {

	default boolean needsItemToShowOutline() {
		return true;
	}

	default boolean alwaysShowOutlineWhenHeld() {
		return false;
	}
}
