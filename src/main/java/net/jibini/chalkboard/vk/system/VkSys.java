package net.jibini.chalkboard.vk.system;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class VkSys
{
	public PointerBuffer pointerParam = MemoryUtil.memAllocPointer(1);
	public IntBuffer intParam = MemoryUtil.memAllocInt(1);
	public LongBuffer longParam = MemoryUtil.memAllocLong(1);
	
	public VkSys(MemoryStack stack)
	{
		pointerParam = stack.mallocPointer(1);
		intParam = stack.mallocInt(1);
		longParam = stack.mallocLong(1);
	}
	
	public VkSys()
	{
		pointerParam = MemoryUtil.memAllocPointer(1);
		intParam = MemoryUtil.memAllocInt(1);
		longParam = MemoryUtil.memAllocLong(1);
	}
	
	public void check(int errcode)
	{
		if (errcode != 0)
			throw new IllegalStateException(String.format("Vulkan error [0x%X]", errcode));
	}
}
