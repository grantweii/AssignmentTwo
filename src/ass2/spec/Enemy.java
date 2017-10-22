package ass2.spec;

import java.nio.FloatBuffer;
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
	private int vertexID;
	private int colourID;
	private int normalID;
	private int textureID;

	private int texUnitPos;
	private MyTexture myTexture;

	public Enemy(float x, float y, float z) {
		this.initialised = false;
		this.x = x;
		this.y = y + 0.2f;
		this.z = z;
	}

	double getX(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }

    double getY(double t){
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }

    /**
     * creates the VBO buffers by iterating through coordinates of a sphere and puts into buffer array
     * Code Source: "http://math.hws.edu/graphicsbook/source/jogl/ColorCubeOfSpheres.java"
     * 
     * @param gl
     * @param shaderProgram
     */
	public void init(GL2 gl, int shaderProgram){
		gl.glEnable(GL2.GL_TEXTURE_2D);
		myTexture = new MyTexture(gl,"resources/textures/rock.jpg","jpg",true);
		texUnitPos = gl.glGetUniformLocation(shaderProgram, "texUnit");

		double radius = 0.2;
	    int stacks = 16;
	    int slices = 32;
	    int size = stacks * (slices+1) * 2 * 3;

	    //create the buffer objects
    	vertexBuffer = Buffers.newDirectFloatBuffer(size);
    	colourBuffer = Buffers.newDirectFloatBuffer(size);
    	normalBuffer = Buffers.newDirectFloatBuffer(size);
    	textureBuffer = Buffers.newDirectFloatBuffer(size);

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

	    vertexBuffer.rewind();
	    colourBuffer.rewind();
	    normalBuffer.rewind();
	    textureBuffer.rewind();

	    //generate buffer IDs for each array
	    bufferIDs = new int[4];
	    gl.glGenBuffers(4, bufferIDs, 0);
	    vertexID = bufferIDs[0];
	    colourID = bufferIDs[1];
	    normalID = bufferIDs[2];
	    textureID = bufferIDs[3];

	    //bind the buffer arrays to the VBO
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
	 * Code Source: "http://math.hws.edu/graphicsbook/source/jogl/ColorCubeOfSpheres.java"
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

	/**
	 * draws the sphere object from buffers & uses shaderProgram 
	 * material properties are interpolated based on Sun state
	 * 
	 * @param gl
	 * @param shaderProgram
	 * @param nightEnabled
	 * @param torchCoordinates
	 * @param sunCoordinates
	 * @param avatarRotation
	 * @param torchEnabled
	 */
	public void draw(GL2 gl, int shaderProgram, boolean nightEnabled, float[] torchCoordinates, float[] sunCoordinates, double avatarRotation, boolean torchEnabled, float sunT) {

		if (!initialised) {
			init(gl,shaderProgram);
			initialised = true;
		}

		float ambientL = 0.1f;
		float ambientH = 0.3f;

		// Interpolate between ambient low and height

		float ambientInterp = ambientH*((float) Math.sin(sunT*2*Math.PI)) + ambientL*(1-(float) Math.sin(sunT*2*Math.PI)) ;

		//set material properties
		float[] ambient = {ambientInterp, ambientInterp, ambientInterp, 1.0f};
		float[] diffuse = {ambientInterp*3, ambientInterp*3, ambientInterp*3, 1.0f};
	    float[] specular = {0.2f, 0.2f, 0.2f, 1.0f};

    	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
    	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
	    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

		gl.glPushMatrix();

			//bind uniform variables and set pointers to each array
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
		    
		    int torchPosition = gl.glGetUniformLocation(shaderProgram, "isTorchOn");
		    if (torchEnabled) {
		    	gl.glUniform1i(torchPosition, 1);
		    } else {
		    	gl.glUniform1i(torchPosition, 0);
		    }

		    int nightMode = gl.glGetUniformLocation(shaderProgram, "nightMode");
		    int lightPos = gl.glGetUniformLocation(shaderProgram, "lightPosition");
		    //If night mode, the sun is the position of the camera (spotlight)
		    if (nightEnabled) {
		    	gl.glUniform3fv(lightPos, 1, torchCoordinates, 0);
			    gl.glUniform1i(nightMode, 1);
		    } else {
		    	gl.glUniform3fv(lightPos, 1, sunCoordinates, 0);
			    gl.glUniform1i(nightMode, 0);
		    }

		    //translate and then draw the sphere
			gl.glTranslated(x, y, z);

			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			drawSphere(gl);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		gl.glPopMatrix();

		//disable the VBO
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		gl.glUseProgram(0);
	}

}
