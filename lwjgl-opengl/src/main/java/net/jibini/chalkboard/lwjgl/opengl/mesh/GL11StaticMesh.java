package net.jibini.chalkboard.lwjgl.opengl.mesh;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class GL11StaticMesh extends GLStaticMesh<GL11StaticMesh>
{
	private final List<Float> vertices = new ArrayList<>();
	
	private int displayList;
	
	@Override
	public GL11StaticMesh destroy()
	{
		vertices.clear();
		return self();
	}

	@Override
	public GL11StaticMesh appendVertex(float x, float y, float z)
	{
		vertices.add(x);
		vertices.add(y);
		vertices.add(z);
		
		return self();
	}

	@Override
	public GL11StaticMesh appendVertices(float[] vertices)
	{
		for (float f : vertices)
			this.vertices.add(f);
		return self();
	}

	@Override
	public GL11StaticMesh assignUniforms(int uniform, float[] uniforms)
	{
		
		return self();
	}

	@Override
	public GL11StaticMesh breakSection()
	{
		
		return self();
	}

	@Override
	public GL11StaticMesh generate()
	{
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
			for (int i = 0; i < vertices.size(); i += 3)
				GL11.glVertex3f(vertices.get(i),
						vertices.get(i + 1),
						vertices.get(i + 2));
		GL11.glEnd();
		GL11.glEndList();
		
		return self();
	}

	@Override
	public GL11StaticMesh renderCall() { GL11.glCallList(displayList); return self(); }
}
