package net.jibini.chalkboard.lwjgl.opengl.render;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class GL30StaticMesh implements GLMeshExtension<GL30StaticMesh>
{
	private int vertexBuffer;
	
	public GL30StaticMesh(GLStaticMesh mesh) { attach(mesh); }
	
	
	@Override
	public GL30StaticMesh destroy()
	{
		GL15.glDeleteBuffers(vertexBuffer);
		return self();
	}

	@Override
	public GL30StaticMesh generate()
	{
		GLStaticMesh mesh = attachment();
		
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(mesh.vertices.size());
		for (Float f : mesh.vertices)
			vertexData.put(f.floatValue());
		vertexData.flip();
				
		vertexBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);

		return self();
	}

	@Override
	public GL30StaticMesh render()
	{
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, attachment().vertices.size() / 3);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
		return self();
	}
}
