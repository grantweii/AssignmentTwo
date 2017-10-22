package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camera {
	
	private Avatar avatar;
	
	public Camera(Avatar avatar) {
		this.avatar = avatar;
	}
	
	/**
	 * updates the camera view every frame
	 * this is done by getting the avatars coordinates and rotation and setting the appropriate view
	 * (first person or third person)
	 * @param gl
	 */
	public void setView(GL2 gl) {
		double avatarX = avatar.getX();
		double avatarY = avatar.getY();
		double avatarZ = avatar.getZ();
		double avatarRotation = avatar.getRotation();
		double xOffset = 0;
		double yOffset = 0.5;
		double zOffset = 0;
		double thirdPersonOffset = 0;
		
		//if avatar is in third person, we need to offset the camera backwards from avatar
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
		
		GLU glu = new GLU();
		
		glu.gluLookAt(avatarX - xOffset, avatarY + yOffset + thirdPersonOffset, avatarZ - zOffset, 
			avatarX + Math.cos(Math.toRadians(avatarRotation)), 
			avatarY + yOffset, avatarZ + Math.sin(Math.toRadians(avatarRotation)),
			0, 1, 0);
	}
	
	
}
