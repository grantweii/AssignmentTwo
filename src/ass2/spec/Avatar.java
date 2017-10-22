package ass2.spec;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import com.jogamp.opengl.GL2;

public class Avatar implements KeyListener {
	
	private boolean thirdPerson;
	private Terrain terrain;
	private PortalPair portals;
	private float x;
	private float y;
	private float z;
	private double myRotation = 300;
	private double rotationStep = 3;
	private double speed = 0.1;
	private MyTexture myTexture;
	private boolean initialised;
	
	public Avatar(Terrain terrain, PortalPair portals) {
		this.terrain = terrain;
		this.thirdPerson = false;
		initialised = false;
		this.portals = portals;
		spawnCoords();
	}
	
	/**
	 * avatar randomly spawns somewhere on map
	 * 
	 */
	public void spawnCoords() {
		Random rand = new Random();
		x = rand.nextFloat() * (terrain.size().width - 1);
		z = rand.nextFloat() * (terrain.size().height - 1);
		y = (float) (terrain.altitude(x, z) + 0.15);
	}
	
	/**
	 * compares coordinates of portal with coordinates of avatar
	 * 
	 * @return
	 */
	public int enterPortal() {
		float[] firstPortalCoords = portals.getFirstPortalCoords();
		float[] firstPortalBounds = portals.getFirstPortalBounds();
		
		float[] secondPortalCoords = portals.getSecondPortalCoords();
		float[] secondPortalBounds = portals.getSecondPortalBounds();
		
		if ((z < (firstPortalCoords[2] + 0.06) && (z > (firstPortalCoords[2] - 0.06))) && x > firstPortalBounds[0] && x < firstPortalBounds[1])  {
			return 1;
		} else if ((z < (secondPortalCoords[2] + 0.06) && (z > (secondPortalCoords[2] - 0.06))) && x > secondPortalBounds[0] && x < secondPortalBounds[1]) {
			return 2;
		} else {
			return 0;
		}
	}
	
	/**
	 * draws sphere & binds texture
	 * 
	 * @param gl
	 */
	public void draw(GL2 gl) {
		
		//draw avatar only if thirdperson is enabled
		if (!thirdPerson) return;
		
		//initialise (first time only)
		if (!initialised) {
			init(gl);
			initialised = true;
		}
		
		gl.glPushMatrix();

			//bind texture, translate, rotate and finally draw
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
	        gl.glTranslated(x, y, z);
	        gl.glRotated(-myRotation, 0, 1, 0);
	        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    	drawSphere(gl);
	    	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		gl.glPopMatrix();
		
	}
	
	/**
	 * teleports avatar to alternate portal
	 * 
	 */
	public void checkPortal() {
		//enters 1st portal
		if (enterPortal() == 1) {
			float[] secondPortalCoords = portals.getSecondPortalCoords();
			float[] secondPortalBounds = portals.getSecondPortalBounds();

			//get middle of second portal x value
			float midX = ((secondPortalBounds[0] + secondPortalBounds[1]) / 2);
			x = midX;
			//new Z is simply second portal Z
			z = secondPortalCoords[2];
			//Y is interpolated as always;
			y = (float) terrain.altitude(x, z);
			
		//enters 2nd portal
		} else if (enterPortal() == 2) {
			float[] firstPortalCoords = portals.getFirstPortalCoords();
			float[] firstPortalBounds = portals.getFirstPortalBounds();

			//get middle of second portal x value
			float midX = ((firstPortalBounds[0] + firstPortalBounds[1]) / 2);
			x = midX;
			//new Z is simply second portal Z
			z = firstPortalCoords[2];
			//Y is interpolated as always;
			y = (float) terrain.altitude(x, z);
		}
	}

	double r(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }
    
    double getY(double t){
    	
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }
	
    /**
     * LECTURE 7 CODE - RevSphereTex.java
     * @param gl
     */
	public void drawSphere(GL2 gl){
		int stacks = 20;
		int slices = 24;
    	double deltaT;
    
    	deltaT = 0.5/stacks;
    	int ang;  
    	int delang = 360/slices;
    	double x1,x2,z1,z2,y1,y2;
    	double radius = 0.1;
    	for (int i = 0; i < stacks; i++) 
    	{ 
    		double t = -0.25 + i*deltaT;
    		
    		gl.glBegin(GL2.GL_TRIANGLE_STRIP); 
    		for(int j = 0; j <= slices; j++)  
    		{  
    			ang = j*delang;
    			x1=radius * r(t)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			x2=radius * r(t+deltaT)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			y1 = radius * getY(t);

    			z1=radius * r(t)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			z2= radius * r(t+deltaT)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			y2 = radius * getY(t+deltaT);

    			double normal[] = {x1,y1,z1};


    			MathUtil.normalize(normal);    

    			gl.glNormal3dv(normal,0);  
    			double tCoord = 1.0/stacks * i; //Or * 2 to repeat label
    			double sCoord = 1.0/slices * j;
    			gl.glTexCoord2d(sCoord,tCoord);
    			gl.glVertex3d(x1,y1,z1);
    			normal[0] = x2;
    			normal[1] = y2;
    			normal[2] = z2;

    			
    			MathUtil.normalize(normal);    
    			gl.glNormal3dv(normal,0); 
    			tCoord = 1.0/stacks * (i+1); //Or * 2 to repeat label
    			gl.glTexCoord2d(sCoord,tCoord);
    			gl.glVertex3d(x2,y2,z2); 

    		}; 
    		gl.glEnd();  
    	}
    }
	
	/**
	 * initialises avatar texture and lighting coefficients
	 * @param gl
	 */
	public void init(GL2 gl) {
		//set material properties
		float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuse = {0.3f, 0.1f, 0.0f, 1.0f};
        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
        
        //initialise avatar texture
		gl.glEnable(GL2.GL_TEXTURE_2D);
		myTexture = new MyTexture(gl,"resources/textures/rock.jpg","jpg",true);
	}
	
	/**
	 * interpolates new position coordinates forwards
	 * trig used to take into consideration avatar rotation
	 */
	public void moveForward() {
		//new coordinates
		float dx = (float) (Math.cos(Math.toRadians(myRotation)) * speed + x);
        float dz = (float) (Math.sin(Math.toRadians(myRotation)) * speed + z);
        
        //if new coordinates are still on map, update coordinates
        if (dx < (terrain.size().width - 1) && dz < (terrain.size().height - 1) && dx > 0 && dz > 0) {
	        x = dx;
	        z = dz;
	        y = (float) (terrain.altitude(x, z) + 0.15);
        }
	}
	
	/**
	 * interpolates new position coordinates backwards
	 * trig used to take into consideration avatar rotation
	 */
	public void moveBackward() {
		//new coordinates
		float dx = (float) (x - Math.cos(Math.toRadians(myRotation)) * speed);
        float dz = (float) (z - Math.sin(Math.toRadians(myRotation)) * speed);
        
        //if new coordinates are still on map, update coordinates
        if (dx < (terrain.size().width - 1) && (dz < terrain.size().height - 1) && dx > 0 && dz > 0) {
	        x = dx;
	        z = dz;
	        y = (float) (terrain.altitude(x, z) + 0.15);
        }
	}
	
	/**
	 * turning right, add rotation
	 */
	public void turnRight() {
		myRotation = myRotation + rotationStep;
    	if (myRotation > 360) myRotation = 0;
	}
	
	/**
	 * turning left, minus rotation
	 */
	public void turnLeft() {
		myRotation = myRotation - rotationStep;
    	if (myRotation < 0) myRotation = 360;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public double getRotation() {
		return this.myRotation;
	}
	
	public double getSpeed() {
		return this.speed;
	}
	
	public double getRotationStep() {
		return this.rotationStep;
	}
	
	public boolean getThirdPerson() {
		return this.thirdPerson;
	}
	
	public void setX(float newX) {
		x = newX;
	}
	
	public void setY(float newY) {
		y = newY;
	}
	
	public void setZ(float newZ) {
		z = newZ;
	}
	
	public void setRotation(double newRotation) {
		myRotation = newRotation;
	}

	@Override
	public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        	
        	//UP, DOWN is translation
            case KeyEvent.VK_UP: {
                moveForward();
            	//check if avatar is entering portals only if portals are enabled
                if (portals.getPortalState()) checkPortal();
                break;
            }
            case KeyEvent.VK_DOWN: {
            	moveBackward();
            	//check if avatar is entering portals only if portals are enabled
            	if (portals.getPortalState()) checkPortal();
            	break;
            }
            //LEFT RIGHT is rotation
            case KeyEvent.VK_LEFT: {
            	turnLeft();
                break;
            }
            case KeyEvent.VK_RIGHT: {
            	turnRight();
            	break;
            }
            case KeyEvent.VK_T: {
            	if (thirdPerson == false) {
            		thirdPerson = true;
            	} else {
            		thirdPerson = false;
            	}
            	break;
            }
            default:
                break;
        }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
}
