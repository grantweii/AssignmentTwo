package ass2.spec;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

public class Avatar implements KeyListener {
	
	private boolean thirdPerson;
	private Terrain terrain;
	private double x = 1;
	private double y = 0.3;
	private double z = 4;
	private double myRotation = 300;
	private double rotationStep = 2;
	private double speed = 0.1;
	private MyTexture myTexture;
	private boolean initialised;
	
	public Avatar(Terrain terrain) {
		this.terrain = terrain;
		this.thirdPerson = false;
		initialised = false;
	}
	
	public void draw(GL2 gl) {
		
		if (!initialised) {
			init(gl);
		}
				
		gl.glPushMatrix();			
	        	        
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
	        gl.glTranslated(x, y, z);
	        gl.glRotated(-myRotation, 0, 1, 0);
	        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    	drawSphere(gl);
	    	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
		gl.glPopMatrix();
		
				
		System.out.println(x);
		System.out.println(z);
		System.out.println(myRotation);

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
	
	public void init(GL2 gl) {
		//materials
		float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuse = {0.3f, 0.1f, 0.0f, 1.0f};
        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
        
		gl.glEnable(GL2.GL_TEXTURE_2D);
		myTexture = new MyTexture(gl,"resources/textures/world.jpg","jpg",true);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	}
	
	public void moveForward() {
		double dx = Math.cos(Math.toRadians(myRotation)) * speed + x;
        double dz = Math.sin(Math.toRadians(myRotation)) * speed + z;
        x = dx;
        z = dz;
        y = terrain.altitude(x, z) + 0.3;
	}
	
	public void moveBackward() {
		double dx = x - Math.cos(Math.toRadians(myRotation)) * speed;
        double dz = z - Math.sin(Math.toRadians(myRotation)) * speed;
        x = dx;
        z = dz;
        y = terrain.altitude(x, z) + 0.3;
	}
	
	public void turnRight() {
		myRotation = myRotation + rotationStep;
    	if (myRotation > 360) myRotation = 0;
	}
	
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
	
	public void setX(double newX) {
		x = newX;
	}
	
	public void setY(double newY) {
		y = newY;
	}
	
	public void setZ(double newZ) {
		z = newZ;
	}
	
	public void setRotation(double newRotation) {
		myRotation = newRotation;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
        switch (e.getKeyCode()) {
        	
        	//UP, DOWN is translation
            case KeyEvent.VK_UP: {
                moveForward();
                break;
            }
            case KeyEvent.VK_DOWN: {
            	moveBackward();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
