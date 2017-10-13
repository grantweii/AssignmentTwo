package ass2.spec;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class Avatar {
	
	private boolean thirdPerson;
	private double x = -2;
	private double z = 5;
	private double myRotation = 300;
	private double rotationStep = 2;
	private double speed = 0.2;
	
	public Avatar() {
		this.thirdPerson = false;
	}
	
	public void moveForward() {
		double dx = Math.cos(Math.toRadians(myRotation)) * speed + x;
        double dz = Math.sin(Math.toRadians(myRotation)) * speed + z;
        x = dx;
        z = dz;
        System.out.println(x);
	}
	
	public void moveBackward() {
		double dx = x - Math.cos(Math.toRadians(myRotation)) * speed;
        double dz = z - Math.sin(Math.toRadians(myRotation)) * speed;
        x = dx;
        z = dz;
	}
	
	public void turnRight() {
		myRotation = myRotation + rotationStep;
    	if (myRotation > 360) myRotation = 0;
	}
	
	public void turnLeft() {
		myRotation = myRotation - rotationStep;
    	if (myRotation < 0) myRotation = 360;
	}
	
	public void draw(GL2 gl) {
		GLUT glut = new GLUT();
				
		gl.glPushMatrix();			
		gl.glPushAttrib(GL2.GL_LIGHTING);
	
			//materials
			float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
	        float[] diffuse = {0.3f, 0.1f, 0.0f, 1.0f};
	        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
	        
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
	        
			gl.glTranslated(x, 1, z);
			gl.glRotated(-myRotation, 0, 1, 0);
			glut.glutSolidSphere(0.1, 64, 64);
//			gl.glFrontFace(GL2.GL_CW);
//		    glut.glutSolidTeapot(0.1);
//		    gl.glFrontFace(GL2.GL_CCW);			
		    
		gl.glPopAttrib();
		gl.glPopMatrix();
		
		System.out.println(x);
		System.out.println(z);
		System.out.println(myRotation);

	}
	
	public double getX() {
		return this.x;
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
	
	public void setZ(double newZ) {
		z = newZ;
	}
	
	public void setRotation(double newRotation) {
		myRotation = newRotation;
	}
	
	public void setThirdPerson() {
		if (!thirdPerson) {
			thirdPerson = true;
		} else {
			thirdPerson = false;
		}
	}
	

//	@Override
//	public void keyPressed(KeyEvent e) {
//		// TODO Auto-generated method stub
//        switch (e.getKeyCode()) {
//        	
//        	//UP, DOWN is translation
//            case KeyEvent.VK_UP: {
//                moveForward();
//                break;
//            }
//            case KeyEvent.VK_DOWN: {
//            	moveBackward();
//            	break;
//            }
//            //LEFT RIGHT is rotation
//            case KeyEvent.VK_LEFT: {
//            	turnLeft();
//                break;
//            }
//            case KeyEvent.VK_RIGHT: {
//            	turnRight();
//            	break;
//            }
//            default:
//                break;
//        }
//		
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e) {
//		// TODO Auto-generated method stub
//		
//	}

	
}
