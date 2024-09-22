package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector;


import java.nio.FloatBuffer;

/**
 *
 * Holds a 3-tuple vector.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */

public class Vector3f extends Vector {

	private static final long serialVersionUID = 1L;

	public float x, y, z;

	/**
	 * Constructor for Vector3f.
	 */
	public Vector3f() {
		super();
	}

	/**
	 * Constructor
	 */
	public Vector3f(ReadableVector3f src) {
		set(src);
	}

	/**
	 * Constructor
	 */
	public Vector3f(float x, float y, float z) {
		set(x, y, z);
	}

	/* (non-Javadoc)
	 * @see WritableVector2f#set(float, float)
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see WritableVector3f#set(float, float, float)
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Load from another Vector3f
	 * @param src The source vector
	 * @return this
	 */
	public Vector3f set(ReadableVector3f src) {
		x = src.getX();
		y = src.getY();
		z = src.getZ();
		return this;
	}

	/**
	 * @return the length squared of the vector
	 */
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * Translate a vector
	 * @param x The translation in x
	 * @param y the translation in y
	 * @return this
	 */
	public Vector3f translate(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Add a vector to another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the sum of left and right in dest
	 */
	public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
		else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}

	/**
	 * Subtract a vector from another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return left minus right in dest
	 */
	public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
		else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}

	/**
	 * The cross product of two vectors.
	 *
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination result, or null if a new vector is to be created
	 * @return left cross right
	 */
	public static Vector3f cross(
		Vector3f left,
		Vector3f right,
		Vector3f dest)
	{

		if (dest == null)
			dest = new Vector3f();

		dest.set(
			left.y * right.z - left.z * right.y,
			right.x * left.z - right.z * left.x,
			left.x * right.y - left.y * right.x
		);

		return dest;
	}



	/**
	 * Negate a vector
	 * @return this
	 */
	public Vector negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	/**
	 * Negate a vector and place the result in a destination vector.
	 * @param dest The destination vector or null if a new vector is to be created
	 * @return the negated vector
	 */
	public Vector3f negate(Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		return dest;
	}


	/**
	 * Normalise this vector and place the result in another vector.
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the normalised vector
	 */
	public Vector3f normalise(Vector3f dest) {
		float l = length();

		if (dest == null)
			dest = new Vector3f(x / l, y / l, z / l);
		else
			dest.set(x / l, y / l, z / l);

		return dest;
	}

	/**
	 * The dot product of two vectors is calculated as
	 * v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @return left dot right
	 */
	public static float dot(Vector3f left, Vector3f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	/**
	 * Calculate the angle between two vectors, in radians
	 * @param a A vector
	 * @param b The other vector
	 * @return the angle between the two vectors, in radians
	 */
	public static float angle(Vector3f a, Vector3f b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f)
			dls = -1f;
		else if (dls > 1.0f)
			dls = 1.0f;
		return (float)Math.acos(dls);
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Vector#load(FloatBuffer)
	 */
	public Vector load(FloatBuffer buf) {
		x = buf.get();
		y = buf.get();
		z = buf.get();
		return this;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Vector#scale(float)
	 */
	public Vector scale(float scale) {

		x *= scale;
		y *= scale;
		z *= scale;

		return this;

	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Vector#store(FloatBuffer)
	 */
	public Vector store(FloatBuffer buf) {

		buf.put(x);
		buf.put(y);
		buf.put(z);

		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("Vector3f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return x
	 */
	public final float getX() {
		return x;
	}

	/**
	 * @return y
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Set X
	 * @param x
	 */
	public final void setX(float x) {
		this.x = x;
	}

	/**
	 * Set Y
	 * @param y
	 */
	public final void setY(float y) {
		this.y = y;
	}

	/**
	 * Set Z
	 * @param z
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/* (Overrides)
	 * @see org.lwjgl.vector.ReadableVector3f#getZ()
	 */
	public float getZ() {
		return z;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vector3f other = (Vector3f)obj;

		if (x == other.x && y == other.y && z == other.z) return true;

		return false;
	}

	// =================================================================
	// Below this line is additional methods made for DragonFly
	// =================================================================
	public static Vector3f origin = new Vector3f(0.5f, 0.5f, 0.5f);
	public Vector3f rotateAroundX(Vector3f origin, float angle){
		if (angle == 0) return this;
		angle = (float) ((angle) * (Math.PI/180)); // Convert to radians
		float y = getY();
		float z = getZ();
		z -= origin.getZ();
		y -= origin.getY();

		float newZ = (float) (z * Math.cos(angle) - y * Math.sin(angle));
		float newY = (float) (z * Math.sin(angle) + y * Math.cos(angle));

		z = newZ + origin.getZ();
		y = newY + origin.getY();
		return new Vector3f(getX(), y, z);
	}

	public Vector3f rotateAroundY(Vector3f origin, float angle){
		if (angle == 0) return this;
		angle = (float) ((angle) * (Math.PI/180)); // Convert to radians
		float x = getX();
		float z = getZ();
		z -= origin.getZ();
		x -= origin.getX();

		float newZ = (float) (z * Math.cos(angle) - x * Math.sin(angle));
		float newX = (float) (z * Math.sin(angle) + x * Math.cos(angle));

		z = newZ + origin.getZ();
		x = newX + origin.getX();
		return new Vector3f(x, getY(), z);
	}
	public Vector3f rotateAroundZ(Vector3f origin, float angle){
		if (angle == 0) return this;
		angle = (float) ((angle) * (Math.PI/180)); // Convert to radians
		float x = getX();
		float y = getY();
		y -= origin.getY();
		x -= origin.getX();

		float newZ = (float) (y * Math.cos(angle) - x * Math.sin(angle));
		float newY = (float) (y * Math.sin(angle) + x * Math.cos(angle));

		y = newZ + origin.getY();
		x = newY + origin.getX();
		return new Vector3f(x, y, getZ());
	}

	public float getFromIndex(int index){
		switch (index){
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
		}
		throw new ArrayIndexOutOfBoundsException();
	}

}
