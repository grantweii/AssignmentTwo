package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;

public class PortalPair {
	
	private Terrain terrain;
	
	//portalCoords { x, y, z }
	private float firstPortalCoords[];
	private float secondPortalCoords[];
	
	//portal bounds is a pair of x values, it is used to check if avatar is going into portal
	//smaller value is ALWAYS first
	//firstPortal { x1, x2 }
	//secondPortal { x1, x2 }
	private float firstPortalBounds[];
	private float secondPortalBounds[];
	
	public PortalPair(Terrain terrain) {
		this.terrain = terrain;
		spawnPortals();
		firstPortalBounds = new float[2];
		secondPortalBounds = new float[2];
        firstPortalBounds[0] = firstPortalCoords[0];
        firstPortalBounds[1] = firstPortalCoords[0]+0.5f;
        secondPortalBounds[0] = secondPortalCoords[0]-0.5f;
        secondPortalBounds[1] = secondPortalCoords[0];
	}
	
	public void spawnPortals() {
		//hardcoded here
		float x1 = 3f;
		float z1 = 0.1f;
		float y1 = (float) (terrain.altitude(x1, z1));
		firstPortalCoords = new float[]{ x1, y1, z1 };
		
		float x2 = 2f;
		float z2 = 4.9f;
		float y2 = (float) (terrain.altitude(x2, z2));
		secondPortalCoords = new float[]{ x2, y2, z2 };
//		Random rand = new Random();
//		x = rand.nextFloat() * (terrain.size().width - 1);
//		z = rand.nextFloat() * (terrain.size().height - 1);
//		y = (float) (terrain.altitude(x, z));
	}
	
	public void teleportToPortal() {
		
	}
	
	public float[] getFirstPortalCoords() {
		return firstPortalCoords;
	}
	
	public float[] getSecondPortalCoords() {
		return secondPortalCoords;
	}
	
	public float[] getFirstPortalBounds() {
		return firstPortalBounds;
	}
	
	public float[] getSecondPortalBounds() {
		return secondPortalBounds;
	}
	
	public void draw(GL2 gl, Texture portalTexture ) {
				
		float[] ambient = {0.1f, 0.18725f, 0.1745f, 1.0f};
        float[] diffuse = {0.396f, 0.74151f, 0.69102f, 1.0f};
        float[] specular = {0.297254f, 0.30829f, 0.306678f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
        
//        portalTexture.enable(gl);
//        portalTexture.bind(gl);
		
        float[] v1 = {firstPortalCoords[0], firstPortalCoords[1], firstPortalCoords[2]};
        float[] v2 = {firstPortalCoords[0]+0.5f, firstPortalCoords[1], firstPortalCoords[2]};
        float[] v3 = {firstPortalCoords[0]+0.5f, firstPortalCoords[1]+1, firstPortalCoords[2]};
        float[] v4 = {firstPortalCoords[0], firstPortalCoords[1]+1, firstPortalCoords[2]};
        
        
        //first portal
		gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);
        
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            
            //gl.glTranslated(x1, y1, z1);
            gl.glBegin(GL2.GL_POLYGON); 
            {
            	gl.glColor3f(1, 0, 0);
//            	gl.glTexCoord2d(0,0);
                gl.glVertex3fv(v1,0);
//                gl.glTexCoord2d(0,1);
                gl.glVertex3fv(v2,0);
//                gl.glTexCoord2d(1,1);
                gl.glVertex3fv(v3,0);
//                gl.glTexCoord2d(1,0);
                gl.glVertex3fv(v4,0);
            }
            gl.glEnd();
        
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl.glPopAttrib();
        gl.glPopMatrix();
        
        float[] v5 = {secondPortalCoords[0], secondPortalCoords[1], secondPortalCoords[2]};
        float[] v6 = {secondPortalCoords[0]-0.5f, secondPortalCoords[1], secondPortalCoords[2]};
        float[] v7 = {secondPortalCoords[0]-0.5f, secondPortalCoords[1]+1, secondPortalCoords[2]};
        float[] v8 = {secondPortalCoords[0], secondPortalCoords[1]+1, secondPortalCoords[2]};
        
        //2nd portal
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);
        
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            
            //gl.glTranslated(x2, y2, z2);
            gl.glBegin(GL2.GL_POLYGON); 
            {
            	gl.glColor3f(1, 0, 0);
//            	gl.glTexCoord2d(0,0);
                gl.glVertex3fv(v5,0);
//                gl.glTexCoord2d(0,1);
                gl.glVertex3fv(v6,0);
//                gl.glTexCoord2d(1,1);
                gl.glVertex3fv(v7,0);
//                gl.glTexCoord2d(1,0);
                gl.glVertex3fv(v8,0);
            }
            gl.glEnd();
            
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl.glPopAttrib();
        gl.glPopMatrix();
        
//        portalTexture.disable(gl);

	}
}
