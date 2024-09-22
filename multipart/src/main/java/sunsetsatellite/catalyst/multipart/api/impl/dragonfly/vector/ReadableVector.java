package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector;


import java.nio.FloatBuffer;

public interface ReadableVector {
	/**
	 * @return the length of the vector
	 */
	float length();
	/**
	 * @return the length squared of the vector
	 */
	float lengthSquared();
	/**
	 * Store this vector in a FloatBuffer
	 * @param buf The buffer to store it in, at the current position
	 * @return this
	 */
	Vector store(FloatBuffer buf);
}
