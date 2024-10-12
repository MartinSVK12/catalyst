package sunsetsatellite.catalyst.core.util;

/**
 * Represents a function that accepts 2 arguments + vararg argument and produces a result.
 *
 * <p>This is a functional interface
 * whose functional method is {@link #apply(T,T2,V...)}.
 */
@FunctionalInterface
public interface VarargsFunction3<T,T2,V,R> {

	/**
	 * Runs the procedure.
	 */
	R apply(T t, T2 t2, V... args);
}
