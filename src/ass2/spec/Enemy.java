package ass2.spec;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Enemy {
	
	private Terrain terrain;
	public float[] position;
	public float[] colours;
	private int[] bufferIDs;
	private float[] pos =
		{
				2, 1, 2,
				3, 1, 2,
				3, 2, 2
//				position[0], position[1], position[2],
//				position[0] + 1f, position[1], position[2],
//				position[0] + 1f, position[2] + 1f, position[2]
		};
	private float[] col = 
		{
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
		};
	
	
	public Enemy(Terrain terrain, float x, float z) {
		this.terrain = terrain;
		this.position = new float[3];
		this.position[0] = x;
		this.position[1] = Float.parseFloat(String.valueOf(terrain.altitude(x, z)));
		this.position[2] = z;
	}
	
	public void update() {
		
	}
	
	public void draw() {
		
	}

	//using vbos
	public void init(GL2 gl) {
//		float[] pos =
//			{
//					2, 1, 2,
//					3, 1, 2,
//					3, 2, 2
////					position[0], position[1], position[2],
////					position[0] + 1f, position[1], position[2],
////					position[0] + 1f, position[2] + 1f, position[2]
//			};
//		
//		float[] col = 
//			{
//					1, 0, 0,
//					1, 0, 0,
//					1, 0, 0,
//			};
		
		//parse data as floatbuffer
    	FloatBuffer posData = Buffers.newDirectFloatBuffer(pos);
    	FloatBuffer colData = Buffers.newDirectFloatBuffer(col);
    	
    	//generate buffer IDs
    	bufferIDs = new int[1];
    	gl.glGenBuffers(1, bufferIDs, 0);
    	
    	//bind buffer for use
    	gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIDs[0]);
    	gl.glBufferData(GL2.GL_ARRAY_BUFFER, pos.length * Float.BYTES + col.length * Float.BYTES,
    					null, GL2.GL_STATIC_DRAW);
    	
    	//load data into buffer, enable state, and tell where to find the data
    	gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, pos.length * Float.BYTES, posData);
		
    	gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, pos.length * Float.BYTES, col.length * Float.BYTES, colData);

	}
	
	public void draw(GL2 gl) {
		//bind buffer for use
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[0]);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
    	gl.glColorPointer(3, GL.GL_FLOAT, 0, pos.length * Float.BYTES);
		
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
		
		//disable state, good practice
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		//unbind buffers, good practice
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}
	
	public void dispose(GL2 gl) {
		gl.glDeleteBuffers(1, bufferIDs, 0);
	}
	
	
}
