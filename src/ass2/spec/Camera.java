package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camera {
	
	//ALSO NOT YET INTEGRATED
	
	private GLU glu;
	private double dx;
	private double dz;
		
	private double fieldOfView;
	private double near;
	private double far;
	private double aspectRatio;
	
	private double cameraRotation;
	
	public Camera(GL2 gl) {
		fieldOfView = 120;
		near = 0.1d;
		far = 1000d;
		aspectRatio = 4/3;
		cameraRotation = 45d;
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		//fieldofview, aspectratio, near, far
		glu = new GLU();
		glu.gluPerspective(fieldOfView, aspectRatio, near, far);
	}
	
	public void setView(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
//		glu.gluLookAt(dx, 1, dz, 
//				dx+Math.cos(Math.toRadians(CAMERA_ROTATION)), 
//				1, dz+Math.sin(Math.toRadians(CAMERA_ROTATION)), 
//				0, 1, 0);
	}
	
	
}
