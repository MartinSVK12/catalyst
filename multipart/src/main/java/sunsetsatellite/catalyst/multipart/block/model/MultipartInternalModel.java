package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.core.util.helper.Side;
import org.useless.dragonfly.model.block.InternalModel;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import sunsetsatellite.catalyst.multipart.api.Multipart;

public class MultipartInternalModel extends InternalModel {

	public final Side side;
	public final Multipart part;

	public MultipartInternalModel(ModernBlockModel model, Side side, Multipart part) {
		super(model, 0, 0);
		this.side = side;
		this.part = part;
	}
}
