package ass2.spec;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Enemy {
	
	private Terrain terrain;
	
	private String textureString;
	
	private int[] bufferIDs;
	public float[] positions;
	private float[] normals;
	public float[] colours;
	private float[] textures;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalBuffer;
	private FloatBuffer textureBuffer;
	private float[] vertexArray;
	private float[] normalArray;
	private float[] textureArray;
	private int vertexID;
	private int normalID;
	private int textureID;
	
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
		this.positions = new float[3];
		this.positions[0] = x;
		this.positions[1] = Float.parseFloat(String.valueOf(terrain.altitude(x, z)));
		this.positions[2] = z;
		
	}
	
	public void dispose(GL2 gl) {
		gl.glDeleteBuffers(1, bufferIDs, 0);
	}
	
	double getX(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }
    
    double getY(double t){
    	
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }
	
	public void init(GL2 gl){
		System.out.println("init");
		double radius = 0.4;
	    int stacks = 16;
	    int slices = 32;
	    int size = stacks * (slices+1) * 2 * 3;
	    
    	vertexBuffer = Buffers.newDirectFloatBuffer(size);
    	normalBuffer = Buffers.newDirectFloatBuffer(size);
    	textureBuffer = Buffers.newDirectFloatBuffer(size);
    	
    	vertexArray = new float[size];
    	normalArray = new float[size];
    	textureArray = new float[size];
    	
    	for (int i = 0; i < stacks; i++) {
    		double latitude1 = (Math.PI/stacks) * i - Math.PI/2;
    	    double latitude2 = (Math.PI/stacks) * (i+1) - Math.PI/2;
    	    double sinLat1 = Math.sin(latitude1);
    	    double cosLat1 = Math.cos(latitude1);
   	      	double sinLat2 = Math.sin(latitude2);
    	    double cosLat2 = Math.cos(latitude2);
    	    
    	    for (int j = 0; j <= slices; j++) {
    	    	double longitude = (2*Math.PI/slices) * j;
    	        double sinLong = Math.sin(longitude);
    	        double cosLong = Math.cos(longitude);
    	        double x1 = cosLong * cosLat1;
    	        double y1 = sinLong * cosLat1;
    	        double z1 = sinLat1;
    	        double x2 = cosLong * cosLat2;
    	        double y2 = sinLong * cosLat2;
    	        double z2 = sinLat2;
    			
    			//should it be x2 or x1 first?
    			normalBuffer.put((float) x2);
    			normalBuffer.put((float) y2);
    			normalBuffer.put((float) z2);
    			vertexBuffer.put((float) (radius*x2));
    			vertexBuffer.put((float) (radius*y2));
    			vertexBuffer.put((float) (radius*z2));
    			textureBuffer.put((float) (1/slices*j));
    			textureBuffer.put((float) (1/stacks*i+1));
    			
    			normalBuffer.put((float) x1);
    			normalBuffer.put((float) y1);
    			normalBuffer.put((float) z1);
    			vertexBuffer.put((float) (radius*x1));
    			vertexBuffer.put((float) (radius*y1));
    			vertexBuffer.put((float) (radius*z1));
    			textureBuffer.put((float) (1/slices*j));
    			textureBuffer.put((float) (1/stacks*i));
    	    }
		}
		for (int i = 0; i < size; i++) {
		      vertexArray[i] = vertexBuffer.get(i);
		      normalArray[i] = normalBuffer.get(i);
		      textureArray[i] = textureBuffer.get(i);
		}
		    
	    vertexBuffer.rewind();
	    normalBuffer.rewind();
	    textureBuffer.rewind();
	    
	    int[] bufferIDs = new int[3];
	    gl.glGenBuffers(3, bufferIDs, 0);
	    vertexID = bufferIDs[0];
	    normalID = bufferIDs[1];
	    textureID = bufferIDs[2];
	    
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, vertexBuffer, GL2.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normalID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, normalBuffer, GL2.GL_STATIC_DRAW);
//	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, textureID);
//	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, textureBuffer, GL2.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	
    }
	
	/**
	 * Draws a sphere using quad strip
	 * Code sourced from "http://math.hws.edu/graphicsbook/source/jogl/ColorCubeOfSpheres.java"
	 * @param gl
	 */
	public void drawSphere(GL2 gl) {
		int slices = 32;
	    int stacks = 16;
	    int vertices = (slices+1)*2;
	    for (int i = 0; i < stacks; i++) {
	      int pos = i*(slices+1)*2;
	      gl.glDrawArrays(GL2.GL_QUAD_STRIP, pos, vertices);
	    }
	}
	
	public void draw(GL2 gl, int shaderProgram) {
		gl.glPushMatrix();
		
			gl.glUseProgram(shaderProgram);
		
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexID);
		    int vertexPositionID = gl.glGetAttribLocation(shaderProgram, "vertexPosition");
		    gl.glEnableVertexAttribArray(vertexPositionID);
		    gl.glVertexAttribPointer(vertexPositionID, 3, GL.GL_FLOAT, false, 0, 0);
		    
		    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalID);
		    int vertexNormalID = gl.glGetAttribLocation(shaderProgram, "vertexNormals");
		    gl.glEnableVertexAttribArray(vertexNormalID);
		    gl.glVertexAttribPointer(vertexNormalID, 3, GL.GL_FLOAT, false, 0, 0);
		    
		    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, textureID);
		    int vertexTextureID = gl.glGetAttribLocation(shaderProgram, "vertexTextures");
		    gl.glEnableVertexAttribArray(vertexTextureID);
		    gl.glVertexAttribPointer(vertexTextureID, 2, GL.GL_FLOAT, false, 0, 0);   
	}
	
}
