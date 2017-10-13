package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.Iterator;

/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

    private Terrain myTerrain;
    private double dx;
    private double dz;
    private GLU glu;
    private GLUT glut;
    
    public Avatar avatar;
    public Game game;
    
    private Camera camera;
    private double fieldOfView = 120;
	private double near = 0.1;
	private double far = 1000;
	private double aspectRatio = 4/3;
	public static double cameraRotation = 60;
	public static double rotateCamera = 4;
	public static double speed = 0.2;
	public static double cameraX = -2;
	public static double cameraZ = 6;
	    
    public Game(Terrain terrain) {
        super("Assignment 2");
        myTerrain = terrain;
        game = this;
 		avatar = new Avatar();
 		camera = new Camera(avatar);
    }

    /**
     * Run the game.
     *
     */
    public void run() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLJPanel panel = new GLJPanel();
        panel.addGLEventListener(this);

        // Add an animator to call 'display' at 60fps
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();

        getContentPane().add(panel);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        panel.addKeyListener(this);
        
        //this doesnt work!
//        InputKeyListener keyListener = new InputKeyListener();
//        panel.addKeyListener(keyListener);
    }

    /**
     * Load a level file and display it.
     *
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        camera.setView(gl);
 		avatar.draw(gl);
        //setView(gl);
        myTerrain.draw(gl);
    }
    
    public void setView(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);  
		gl.glLoadIdentity();
		gl.glPushMatrix();
			glu.gluLookAt(cameraX, 1, cameraZ, 
				cameraX + Math.cos(Math.toRadians(cameraRotation)), 
				1, cameraZ + Math.sin(Math.toRadians(cameraRotation)),
				0, 1, 0);
		gl.glPopMatrix();
	}

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }
  
    @Override
    public void init(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);

        //By enabling lighting, color is worked out differently.
        gl.glEnable(GL2.GL_LIGHTING);

        //When you enable lighting you must still actually
        //turn on a light such as this default light.
        gl.glEnable(GL2.GL_LIGHT0);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
 		gl.glLoadIdentity();
 		
 		camera.initCamera(gl);
// 		//initialise camera perspective
// 		glu = new GLU();
// 		glut = new GLUT();
// 		glu.gluPerspective(120, aspectRatio, near, far);
 		
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
//        // TODO Auto-generated method stub

        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu = new GLU();

        glu.gluPerspective(60.0, (float)width/(float)height, 1.0, 20.0);
    }

//    @Override
//    public void keyPressed(KeyEvent e) {
//        // TODO Auto-generated method stub
//        switch (e.getKeyCode()) {
//
//        	//UP, DOWN is camera translation
//            case KeyEvent.VK_UP: {
//                double x = Math.cos(Math.toRadians(cameraRotation)) * speed + cameraX;
//                double z = Math.sin(Math.toRadians(cameraRotation)) * speed + cameraZ;
//                cameraX = x;
//                cameraZ = z;
//                break;
//            }
//            case KeyEvent.VK_DOWN: {
//            	double x = cameraX - Math.cos(Math.toRadians(cameraRotation)) * speed;
//                double z = cameraZ - Math.sin(Math.toRadians(cameraRotation)) * speed;
//                cameraX = x;
//                cameraZ = z;
//            	break;
//            }
//            //LEFT RIGHT is camera rotation
//            case KeyEvent.VK_LEFT: {
//            	cameraRotation = cameraRotation - rotateCamera;
//            	if (cameraRotation < 0) cameraRotation = 360;
//                break;
//            }
//            case KeyEvent.VK_RIGHT: {
//            	cameraRotation = cameraRotation + rotateCamera;
//            	if (cameraRotation > 360) cameraRotation = 0;
//            	break;
//            }
//            default:
//                break;
//        }
//    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
    	double avatarX = avatar.getX();
    	double avatarZ = avatar.getZ();
    	double avatarRotation = avatar.getRotation();
    	double avatarSpeed = avatar.getSpeed();
    	double rotationStep = avatar.getRotationStep();
    	
        switch (e.getKeyCode()) {

        	//UP, DOWN is camera translation
            case KeyEvent.VK_UP: {
                double x = Math.cos(Math.toRadians(avatarRotation)) * avatarSpeed + avatarX;
                double z = Math.sin(Math.toRadians(avatarRotation)) * avatarSpeed + avatarZ;
                avatar.setX(x);
                avatar.setZ(z);
                break;
            }
            case KeyEvent.VK_DOWN: {
            	double x = avatarX - Math.cos(Math.toRadians(avatarRotation)) * avatarSpeed;
                double z = avatarZ - Math.sin(Math.toRadians(avatarRotation)) * avatarSpeed;
                avatar.setX(x);
                avatar.setZ(z);
            	break;
            }
            //LEFT RIGHT is camera rotation
            case KeyEvent.VK_LEFT: {
            	double newRotation = avatarRotation - rotationStep;
            	avatar.setRotation(newRotation);
            	if (newRotation < 0) avatar.setRotation(360);
                break;
            }
            case KeyEvent.VK_RIGHT: {
            	double newRotation = avatarRotation + rotationStep;
            	avatar.setRotation(newRotation);
            	if (newRotation > 360) avatar.setRotation(0);
            	break;
            }
            case KeyEvent.VK_T: {
            	avatar.setThirdPerson();
            }
            default:
                break;
        }
    }
    

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
}
