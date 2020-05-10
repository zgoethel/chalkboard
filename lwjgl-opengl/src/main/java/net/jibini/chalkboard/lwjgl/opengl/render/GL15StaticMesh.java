package net.jibini.chalkboard.lwjgl.opengl.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.jibini.chalkboard.render.AbstractStaticMesh;
import net.jibini.chalkboard.render.DefaultAttrib;

public class GL15StaticMesh implements GLMeshExtension<GL15StaticMesh>
{
	private int vertexBuffer;
	
	private final List<Integer> enabledClientStates = new ArrayList<>();
	private final List<Integer> enabledAttribs = new ArrayList<>();
	
	public GL15StaticMesh(GLStaticMesh mesh) { attach(mesh); }
	
	
	@Override
	public GL15StaticMesh destroy()
	{
		GL15.glDeleteBuffers(vertexBuffer);
		return self();
	}

	@Override
	public GL15StaticMesh generate()
	{
		GLStaticMesh mesh = attachment();
		
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(mesh.sumInterleavedTotalSize());
		
		for (int i = 0; i < mesh.vertexData.size() / 3; i++)
		{
			vertexData.put(mesh.vertexData.get(i * 3));
			vertexData.put(mesh.vertexData.get(i * 3 + 1));
			vertexData.put(mesh.vertexData.get(i * 3 + 2));
			
			for (long attribMeta : mesh.interleavedMeta)
			{
				int attrib = AbstractStaticMesh.decodeAttrib(attribMeta);
				int length = AbstractStaticMesh.decodeLength(attribMeta);
				
				List<Float> attribData = mesh.interleavedData.get(attrib);
				for (int j = i * length; j < i * length + length; j++)
					vertexData.put(attribData.get(j % attribData.size()));
			}
		}
		
		vertexData.flip();
				
		vertexBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);

		return self();
	}

	@Override
	public GL15StaticMesh render()
	{
		GLStaticMesh mesh = attachment();
		int stride = mesh.sumInterleavedStride() * 4;
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
		
		for (long attribMeta : mesh.interleavedMeta)
		{
			int attrib = AbstractStaticMesh.decodeAttrib(attribMeta);
			int length = AbstractStaticMesh.decodeLength(attribMeta);
			
			int startPoint = mesh.sumStartingPoint(attrib) * 4;
			
			switch (attrib)
			{
			case DefaultAttrib.DEFAULT_COLOR_ATTRIB:
				enabledClientStates.add(GL11.GL_COLOR_ARRAY);
				GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				GL11.glColorPointer(length, GL11.GL_FLOAT, stride, startPoint);
				break;
			case DefaultAttrib.DEFAULT_TEX_COORD_ATTRIB:
				enabledClientStates.add(GL11.GL_TEXTURE_COORD_ARRAY);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				GL11.glTexCoordPointer(length, GL11.GL_FLOAT, stride, startPoint);
				break;
			case DefaultAttrib.DEFAULT_NORMAL_ATTRIB:
				enabledClientStates.add(GL11.GL_NORMAL_ARRAY);
				GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				GL11.glNormalPointer(GL11.GL_FLOAT, stride, startPoint);
				break;
			default:
				enabledAttribs.add(attrib);
				GL20.glEnableVertexAttribArray(attrib);
				GL20.glVertexAttribPointer(attrib, length, GL11.GL_FLOAT, false, stride, startPoint);
			}
		}
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, attachment().vertexData.size() / 3);
		
		for (int i : enabledClientStates)
			GL11.glDisableClientState(i);
		enabledClientStates.clear();
		
		for (int i : enabledAttribs)
			GL20.glDisableVertexAttribArray(i);
		enabledAttribs.clear();
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		return self();
	}
}
