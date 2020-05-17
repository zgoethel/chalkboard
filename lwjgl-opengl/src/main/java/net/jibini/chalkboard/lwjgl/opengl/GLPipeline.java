package net.jibini.chalkboard.lwjgl.opengl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import net.jibini.chalkboard.object.Pointer;
import net.jibini.chalkboard.resource.BinaryResource;
import net.jibini.chalkboard.shading.Pipeline;
import net.jibini.chalkboard.shading.ShaderType;

public class GLPipeline implements Pipeline<GLPipeline>, Pointer<Integer>
{
	private int program;
	private List<Integer> shaders = new ArrayList<>();
	
	@Override
	public GLPipeline destroy() { GL20.glDeleteProgram(pointer()); return self(); }

	
	@Override
	public GLPipeline generate()
	{
		program = GL20.glCreateProgram();
		
		for (Integer i : shaders)
		{
			GL20.glAttachShader(pointer(), i);
			GL20.glDeleteShader(i);
		}
		
		shaders.clear();
		
		GL20.glLinkProgram(pointer());
		if (GL20.glGetProgrami(pointer(), GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Failed to link shader program.\n" + GL20.glGetProgramInfoLog(pointer()));
		return self();
	}
	
	@Override
	public GLPipeline attachShader(ShaderType type, BinaryResource glsl, BinaryResource spirv)
	{
		int shaderType = -1;
		
		switch (type)
		{
		case FRAGMENT:
			shaderType = GL20.GL_FRAGMENT_SHADER;
			break;
		case GEOMETRY:
			shaderType = GL32.GL_GEOMETRY_SHADER;
			break;
		case VERTEX:
			shaderType = GL20.GL_VERTEX_SHADER;
		}
		
		int shader = GL20.glCreateShader(shaderType);
		GL20.glShaderSource(shader, new String(glsl.load()));
		GL20.glCompileShader(shader);
		if (GL20.glGetShaderi(shaderType, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Failed to compile shader\n" + GL20.glGetShaderInfoLog(shader));
		
		shaders.add(shader);
		return self();
	}

	@Override
	public Integer pointer() { return program; }


	@Override
	public GLPipeline use() { GL20.glUseProgram(pointer()); return self(); }
}
