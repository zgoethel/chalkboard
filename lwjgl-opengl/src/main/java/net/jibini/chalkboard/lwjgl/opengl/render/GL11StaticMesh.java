package net.jibini.chalkboard.lwjgl.opengl.render;

import org.lwjgl.opengl.GL11;

public class GL11StaticMesh implements GLMeshExtension<GL11StaticMesh>
{
	private int displayList;
	
	public GL11StaticMesh(GLStaticMesh mesh) { attach(mesh); }
	
	
	@Override
	public GL11StaticMesh destroy()
	{
		GL11.glDeleteLists(displayList, 1);
		return self();
	}

	@Override
	public GL11StaticMesh generate()
	{
		GLStaticMesh mesh = attachment();
		
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
			for (int i = 0; i < mesh.vertices.size(); i += 3)
				GL11.glVertex3f(mesh.vertices.get(i),
						mesh.vertices.get(i + 1),
						mesh.vertices.get(i + 2));
		GL11.glEnd();
		GL11.glEndList();
		
		return self();
	}

	@Override
	public GL11StaticMesh render() { GL11.glCallList(displayList); return self(); }
}
