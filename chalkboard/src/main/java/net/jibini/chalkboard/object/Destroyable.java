package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Destroyable<THIS extends Destroyable<THIS>>
		extends AutoCloseable
{
	THIS destroy();
	
	@Override
	default void close() { destroy(); }
}
