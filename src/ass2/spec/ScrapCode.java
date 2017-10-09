//package ass2.spec;
//
//import com.jogamp.opengl.GL;
//import com.jogamp.opengl.GL2;
//import com.jogamp.opengl.GLAutoDrawable;
//import com.jogamp.opengl.glu.GLU;
//
//public class ScrapCode {
//	
//	private GLU glu;
//	
//	private static final double NEAR = 0.01;
//	private static final double FAR = 100;
//	private static final double ASPECT_RATIO = 4/3;
//	private static final double FIELD_OF_VIEW = 60;
//	
//	public ScrapCode() {
//		
//	}
//	
//	public void initCamera(GL2 gl) {
//		gl.glMatrixMode(GL2.GL_PROJECTION);
//		gl.glLoadIdentity();
//		
//		//fieldofview, aspectratio, near, far
//		glu = new GLU();
//		glu.gluPerspective(FIELD_OF_VIEW, ASPECT_RATIO, NEAR, FAR);
//	}
//	
//	public void setView(GL2 gl) {
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glLoadIdentity();
//		gl.glRotatef(10,1,0,0); //pitch
//		gl.glTranslatef(0, 0, -5); //move camera backwards to see more of the world
//		
//		glu.gluLookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
//	}
//	
//}
