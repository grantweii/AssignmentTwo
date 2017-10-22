package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PortalPair implements KeyListener{
	
	private Terrain terrain;
	
	//portalCoords { x, y, z }
	private float firstPortalCoords[];
	private float secondPortalCoords[];
	private boolean isPortalOn = false;
	
	//portal bounds is a pair of x values, it is used to check if avatar is going into portal
	//smaller value is ALWAYS first
	//firstPortal { x1, x2 }
	//secondPortal { x1, x2 }
	private float firstPortalBounds[];
	private float secondPortalBounds[];
	
	public PortalPair(Terrain terrain) {
		this.terrain = terrain;
		spawnPortals();
	}
	
	/**
	 * initialises coordinates and x coordinate bounds of both portals
	 */
	public void spawnPortals() {
		float x1 = 8f;
		float z1 = 2.5f;
		float y1 = (float) (terrain.altitude(x1, z1));
		firstPortalCoords = new float[]{ x1, y1, z1 };
		
		float x2 = 2f;
		float z2 = 4.9f;
		float y2 = (float) (terrain.altitude(x2, z2));
		secondPortalCoords = new float[]{ x2, y2, z2 };
		
		firstPortalBounds = new float[2];
        firstPortalBounds[0] = firstPortalCoords[0];
        firstPortalBounds[1] = firstPortalCoords[0]+0.5f;
        
		secondPortalBounds = new float[2];
        secondPortalBounds[0] = secondPortalCoords[0]-0.5f;
        secondPortalBounds[1] = secondPortalCoords[0];
	}
	
	public boolean getPortalState() {
		return isPortalOn;
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
	
	/**
	 * draws both sides of the portal using both winding orders
	 * @param gl
	 * @param portalTexture
	 */
	public void draw(GL2 gl, Texture portalTexture ) {

		if (!isPortalOn) return;
		
		//coordinates of first portal
        float[] v1 = {firstPortalCoords[0], firstPortalCoords[1], firstPortalCoords[2]};
        float[] v2 = {firstPortalCoords[0]+0.5f, firstPortalCoords[1], firstPortalCoords[2]};
        float[] v3 = {firstPortalCoords[0]+0.5f, firstPortalCoords[1]+1, firstPortalCoords[2]};
        float[] v4 = {firstPortalCoords[0], firstPortalCoords[1]+1, firstPortalCoords[2]};
        
        //first portal
		gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

		//bind the texture
		portalTexture.enable(gl);
		portalTexture.bind(gl);

    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            
            gl.glBegin(GL2.GL_POLYGON); 
            {
            	gl.glColor3f(1, 0, 0);
            	gl.glTexCoord2d(0,0.2);
                gl.glVertex3fv(v1,0);
                gl.glTexCoord2d(0,0.8);
                gl.glVertex3fv(v2,0);
                gl.glTexCoord2d(1,0.8);
                gl.glVertex3fv(v3,0);
                gl.glTexCoord2d(1,0.2);
                gl.glVertex3fv(v4,0);
            }
            gl.glEnd();
        
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    		
    		gl.glBegin(GL2.GL_POLYGON); 
            {
             	gl.glColor3f(1, 0, 0);
             	gl.glTexCoord2d(0,0.2);
	            gl.glVertex3fv(v1,0);
	            gl.glTexCoord2d(0,0.8);
	            gl.glVertex3fv(v4,0);
	            gl.glTexCoord2d(1,0.8);
	            gl.glVertex3fv(v3,0);
	            gl.glTexCoord2d(1,0.2);
	            gl.glVertex3fv(v2,0);
            }
            gl.glEnd();
             
     		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        
	        //coordinates of second portal
	        float[] v5 = {secondPortalCoords[0], secondPortalCoords[1], secondPortalCoords[2]};
	        float[] v6 = {secondPortalCoords[0]-0.5f, secondPortalCoords[1], secondPortalCoords[2]};
	        float[] v7 = {secondPortalCoords[0]-0.5f, secondPortalCoords[1]+1, secondPortalCoords[2]};
	        float[] v8 = {secondPortalCoords[0], secondPortalCoords[1]+1, secondPortalCoords[2]};
        
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            
            //2nd portal
            gl.glBegin(GL2.GL_POLYGON); 
            {
            	gl.glColor3f(1, 0, 0);
            	gl.glTexCoord2d(0,0.2);
                gl.glVertex3fv(v5,0);
                gl.glTexCoord2d(0,0.8);
                gl.glVertex3fv(v6,0);
                gl.glTexCoord2d(1,0.8);
                gl.glVertex3fv(v7,0);
                gl.glTexCoord2d(1,0.2);
                gl.glVertex3fv(v8,0);
            }
            gl.glEnd();
            
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    		
    		gl.glBegin(GL2.GL_POLYGON); 
            {
            	gl.glColor3f(1, 0, 0);
            	gl.glTexCoord2d(0,0.2);
                gl.glVertex3fv(v5,0);
                gl.glTexCoord2d(0,0.8);
                gl.glVertex3fv(v8,0);
                gl.glTexCoord2d(1,0.8);
                gl.glVertex3fv(v7,0);
                gl.glTexCoord2d(1,0.2);
                gl.glVertex3fv(v6,0);
            }
            gl.glEnd();
            
    		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);


        gl.glPopAttrib();
        gl.glPopMatrix();
        
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {

			//UP, DOWN is translation
			case KeyEvent.VK_Y: {
				isPortalOn = !isPortalOn;
				break;
			}
			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
