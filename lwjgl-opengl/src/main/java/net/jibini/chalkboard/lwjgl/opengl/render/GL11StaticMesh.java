package net.jibini.chalkboard.lwjgl.opengl.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.jibini.chalkboard.render.AbstractStaticMesh;
import net.jibini.chalkboard.render.DefaultAttrib;

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
		
		for (int i = 0; i < mesh.vertexData.size() / 3; i++)
		{
			for (long attribMeta : mesh.interleavedMeta)
			{
				int attrib = AbstractStaticMesh.decodeAttrib(attribMeta);
				int length = AbstractStaticMesh.decodeLength(attribMeta);
				
				List<Float> attribData = mesh.interleavedData.get(attrib);
				
				switch (attrib)
				{
				case DefaultAttrib.DEFAULT_COLOR_ATTRIB:
					switch (length)
					{
					case 4:
						GL11.glColor4f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()),
								attribData.get((i * length + 3) % attribData.size()));
						break;
					case 3:
						GL11.glColor3f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()));
						break;
					default:
						throw new IllegalStateException("Invalid color size, should be 4 or 3.");
					}
					break;
					
				case DefaultAttrib.DEFAULT_TEX_COORD_ATTRIB:
					switch (length)
					{
					case 4:
						GL11.glTexCoord4f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()),
								attribData.get((i * length + 3) % attribData.size()));
						break;
					case 3:
						GL11.glTexCoord3f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()));
						break;
					case 2:
						GL11.glTexCoord2f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()));
						break;
					default:
						throw new IllegalStateException("Invalid texture coordinate size, should be 4, 3, 2, or 1.");
					}
					break;
					
				case DefaultAttrib.DEFAULT_NORMAL_ATTRIB:
					switch (length)
					{
					case 3:
						GL11.glNormal3f(attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()));
						break;
					default:
						throw new IllegalStateException("Invalid normal vector size, should be 3.");
					}
					break;
				default:
					switch (length)
					{
					case 4:
						GL20.glVertexAttrib4f(attrib, attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()),
								attribData.get((i * length + 3) % attribData.size()));
						break;
					case 3:
						GL20.glVertexAttrib3f(attrib, attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()),
								attribData.get((i * length + 2) % attribData.size()));
						break;
					case 2:
						GL20.glVertexAttrib2f(attrib, attribData.get((i * length) % attribData.size()),
								attribData.get((i * length + 1) % attribData.size()));
						break;
					case 1:
						GL20.glVertexAttrib1f(attrib, attribData.get((i * length) % attribData.size()));
						break;
					default:
						throw new IllegalStateException("Invalid attrib data size, should be 4, 3, 2, or 1.");
					}
				}
			}
			
			GL11.glVertex3f(mesh.vertexData.get(i * 3),
					mesh.vertexData.get(i * 3 + 1),
					mesh.vertexData.get(i * 3 + 2));
		}
		
		GL11.glEnd();
		GL11.glEndList();
		
		return self();
	}

	@Override
	public GL11StaticMesh render() { GL11.glCallList(displayList); return self(); }
}
