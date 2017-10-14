package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camera {
	
	private Avatar avatar;
	private GLU glu;
		
	private double fieldOfView = 180;
	private double near = 0.01;
	private double far = 1000;
	private double aspectRatio = 4/3;
	
	public Camera(Avatar avatar) {
		this.avatar = avatar;
	}
	
	public void initCamera(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		//fieldofview, aspectratio, near, far
		glu = new GLU();
		glu.gluPerspective(fieldOfView, aspectRatio, near, far);
	}
	
	public void setView(GL2 gl) {
		double avatarX = avatar.getX();
		double avatarY = avatar.getY();
		double avatarZ = avatar.getZ();
		double avatarRotation = avatar.getRotation();
		double xOffset = 0;
		double yOffset = 0.5;
		double zOffset = 0;
		double thirdPersonOffset = 0;
		
		if (avatar.getThirdPerson()) {
			xOffset = 3 * Math.cos(Math.toRadians(avatarRotation));
			zOffset = 3 * Math.sin(Math.toRadians(avatarRotation));
			thirdPersonOffset = 1;
		} else {
			xOffset = 1 * Math.cos(Math.toRadians(avatarRotation));
			zOffset = 1 * Math.sin(Math.toRadians(avatarRotation));
		}
		gl.glMatrixMode(GL2.GL_MODELVIEW);  
		gl.glLoadIdentity();
		
		glu.gluLookAt(avatarX - xOffset, avatarY + yOffset + thirdPersonOffset, avatarZ - zOffset, 
			avatarX + Math.cos(Math.toRadians(avatarRotation)), 
			avatarY + yOffset, avatarZ + Math.sin(Math.toRadians(avatarRotation)),
			0, 1, 0);
	}
	
	
}
