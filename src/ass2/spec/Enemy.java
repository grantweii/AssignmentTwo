package ass2.spec;

import java.nio.FloatBuffer;
import java.util.Random;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class Enemy {

	private boolean initialised;

	private int[] bufferIDs;
	private float x;
	private float y;
	private float z;
	private FloatBuffer vertexBuffer;
	private FloatBuffer colourBuffer;
	private FloatBuffer normalBuffer;
	private FloatBuffer textureBuffer;
	private float[] vertexArray;
	private float[] colourArray;
	private float[] normalArray;
	private float[] textureArray;
	private int vertexID;
	private int colourID;
	private int normalID;
	private int textureID;

	private int texUnitPos;
	private MyTexture myTexture;

	public Enemy(float x, float y, float z) {
		this.initialised = false;
		this.x = x;
		this.y = y;
		this.z = z;
	}

//	public void spawnCoords() {
//		Random rand = new Random();
//		x = rand.nextFloat() * (terrain.size().width - 1);
//		z = rand.nextFloat() * (terrain.size().height - 1);
//		y = (float) (terrain.altitude(x, z) + 0.2);
//	}

	public void dispose(GL2 gl) {
		gl.glDeleteBuffers(3, bufferIDs, 0);
	}

	double getX(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }

    double getY(double t){
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }

	public void init(GL2 gl, int shaderProgram){
		gl.glEnable(GL2.GL_TEXTURE_2D);
		myTexture = new MyTexture(gl,"resources/textures/rainbow.png","png",true);
//		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		texUnitPos = gl.glGetUniformLocation(shaderProgram, "texUnit");

		double radius = 0.2;
	    int stacks = 16;
	    int slices = 32;
	    int size = stacks * (slices+1) * 2 * 3;

    	vertexBuffer = Buffers.newDirectFloatBuffer(size);
    	colourBuffer = Buffers.newDirectFloatBuffer(size);
    	normalBuffer = Buffers.newDirectFloatBuffer(size);
    	textureBuffer = Buffers.newDirectFloatBuffer(size);

    	vertexArray = new float[size];
    	colourArray = new float[size];
    	normalArray = new float[size];
    	textureArray = new float[size];

    	//initialise sphere positions to put into buffer array
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

    	        vertexBuffer.put((float) (radius*x2));
    			vertexBuffer.put((float) (radius*y2));
    			vertexBuffer.put((float) (radius*z2));
    			normalBuffer.put( (float)x2 );
    	        normalBuffer.put( (float)y2 );
    	        normalBuffer.put( (float)z2 );
    			textureBuffer.put(((float) j)/slices);
    			textureBuffer.put(((float) (i+1))/stacks);

    			vertexBuffer.put((float) (radius*x1));
    			vertexBuffer.put((float) (radius*y1));
    			vertexBuffer.put((float) (radius*z1));
    			normalBuffer.put( (float)x1 );
    	        normalBuffer.put( (float)y1 );
    	        normalBuffer.put( (float)z1 );
    			textureBuffer.put(((float) j)/slices);
    			textureBuffer.put(((float) i)/stacks);

    	    }
		}

    	Random rand = new Random();

    	//randomise the colours
		for (int i = 0; i < size; i++) {
		      vertexArray[i] = vertexBuffer.get(i);
		      normalArray[i] = normalBuffer.get(i);
		      textureArray[i] = textureBuffer.get(i);

		      colourBuffer.put(rand.nextFloat());
		      colourArray[i] = colourBuffer.get(i);
		}

	    vertexBuffer.rewind();
	    colourBuffer.rewind();
	    normalBuffer.rewind();
	    textureBuffer.rewind();

	    int[] bufferIDs = new int[4];
	    gl.glGenBuffers(4, bufferIDs, 0);
	    vertexID = bufferIDs[0];
	    colourID = bufferIDs[1];
	    normalID = bufferIDs[2];
	    textureID = bufferIDs[3];

	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, vertexBuffer, GL2.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colourID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, colourBuffer, GL2.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normalID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, normalBuffer, GL2.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, textureID);
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, size*4, textureBuffer, GL2.GL_STATIC_DRAW);
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

	public void draw(GL2 gl, int shaderProgram, boolean nightEnabled, float[] torchCoordinates, float[] sunCoordinates, double avatarRotation) {

		if (!initialised) {
			init(gl,shaderProgram);
			initialised = true;
		}

		float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
		float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	    float[] specular = {0.2f, 0.2f, 0.2f, 1.0f};

    	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
    	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
	    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

		gl.glPushMatrix();

			gl.glUseProgram(shaderProgram);
			gl.glUniform1i(texUnitPos, 0);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());

			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexID);
		    int vertexPositionID = gl.glGetAttribLocation(shaderProgram, "coordinates");
		    gl.glEnableVertexAttribArray(vertexPositionID);
		    gl.glVertexAttribPointer(vertexPositionID, 3, GL.GL_FLOAT, false, 0, 0);

			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colourID);
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL.GL_FLOAT, 0, 0);

		    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalID);
		    int vertexNormalID = gl.glGetAttribLocation(shaderProgram, "normals");
		    gl.glEnableVertexAttribArray(vertexNormalID);
		    gl.glVertexAttribPointer(vertexNormalID, 3, GL.GL_FLOAT, false, 0, 0);

		    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, textureID);
		    int vertexTextureID = gl.glGetAttribLocation(shaderProgram, "textures");
		    gl.glEnableVertexAttribArray(vertexTextureID);
		    gl.glVertexAttribPointer(vertexTextureID, 2, GL.GL_FLOAT, false, 0, 0);

		    texUnitPos = gl.glGetUniformLocation(shaderProgram, "texUnit");
		    gl.glActiveTexture(GL.GL_TEXTURE0);
		    gl.glUniform1i(texUnitPos, 0); //0 for GL_TEXTURE0
		    
		    int avatarRotationPos = gl.glGetUniformLocation(shaderProgram, "avatarRotation");
		    gl.glUniform1i(avatarRotationPos, (int) avatarRotation);
		    
		    float[] enemyCoords = new float[]{x,y,z};
		    int enemyPosition = gl.glGetUniformLocation(shaderProgram, "enemyPos");
		    gl.glUniform3fv(enemyPosition, 1, enemyCoords, 0);
		    
		    System.out.println(nightEnabled);
		    
		    int nightMode = gl.glGetUniformLocation(shaderProgram, "nightMode");
		    int lightPos = gl.glGetUniformLocation(shaderProgram, "lightPosition");
		    //If night mode, the sun is the position of the camera (spotlight)
		    if (nightEnabled) {
		    	gl.glUniform3fv(lightPos, 1, torchCoordinates, 0);
			    gl.glUniform1i(nightMode, 1);
		    } else {
		    	//System.out.println(terrain.getSunlight().length);
		    	gl.glUniform3fv(lightPos, 1, sunCoordinates, 0);
			    gl.glUniform1i(nightMode, 0);
		    }

//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexID);
//			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
//			gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
//
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colourID);
//			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
//			gl.glColorPointer(3, GL.GL_FLOAT, 0, 0);
//
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, textureID);
//			gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
//			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);

			gl.glTranslated(x, y, z);

			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			drawSphere(gl);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		gl.glPopMatrix();

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		gl.glUseProgram(0);
	}

}
