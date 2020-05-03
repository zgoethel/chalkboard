package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Destroyable<THIS extends Destroyable<THIS>>
		extends AutoCloseable
{
	THIS destroy();
	
	default void close() { destroy(); }
}
