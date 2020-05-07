package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Spawnable<THIS extends Spawnable<THIS>>
{
	THIS spawn();
}
