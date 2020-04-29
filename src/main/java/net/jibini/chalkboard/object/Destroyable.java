package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Destroyable<THIS extends Destroyable<?>>
{
	THIS destroy();
}
