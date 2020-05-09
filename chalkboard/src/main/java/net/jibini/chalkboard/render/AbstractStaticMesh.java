package net.jibini.chalkboard.render;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jibini.chalkboard.object.Attachable;

public abstract class AbstractStaticMesh
		<
			ENGINE extends RenderEngine<THIS, ENGINE>,
			THIS extends AbstractStaticMesh<ENGINE, THIS>
		>
		implements StaticMesh<THIS>, Attachable<ENGINE, THIS>
{
	public final List<Float> vertices = new CopyOnWriteArrayList<>();
	
	@Override
	public THIS appendVertex(float x, float y, float z) { appendVertices(x, y, z); return null; }

	@Override
	public THIS appendVertices(float ... vertices)
	{
		for (float f : vertices)
			this.vertices.add(f);
		return self();
	}

	@Override
	public THIS assignUniforms(int uniform, float[] uniforms)
	{
		//TODO
		return null;
	}

	@Override
	public THIS breakSection()
	{
		//TODO: Sub-meshes
		return null;
	}

	@Override
	public THIS queueRender()
	{
		attachment().queue(self());
		return self();
	}
}
