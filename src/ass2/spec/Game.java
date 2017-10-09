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
import com.jogamp.opengl.util.gl2.GLUT;

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
    
    public Game game;
            
    //private Camera camera;
    private double fieldOfView = 120;
	private double near = 0.1;
	private double far = 1000;
	private double aspectRatio = 4/3;
	public double cameraRotation = 60;
	public double rotateCamera = 2;
	public double speed = 0.2;
	public double cameraX = -2;
	public double cameraZ = -8;
	    
    public Game(Terrain terrain) {
        super("Assignment 2");
        myTerrain = terrain;
        game = this;
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

        panel.addKeyListener(this);

        getContentPane().add(panel);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        setView(gl);
        myTerrain.draw(gl);

    }
    
    public void setView(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		glu.gluLookAt(cameraX, 1, cameraZ, 
			cameraX + Math.cos(Math.toRadians(cameraRotation)), 
			1, cameraZ + Math.sin(Math.toRadians(cameraRotation)), 
			0, 1, 0);
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
 		
 		//initialise camera perspective
 		glu = new GLU();
 		glu.gluPerspective(120, aspectRatio, near, far);
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

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        switch (e.getKeyCode()) {

        	//UP, DOWN is camera translation
            case KeyEvent.VK_UP: {
                double x = Math.cos(Math.toRadians(cameraRotation)) * speed + cameraX;
                double z = Math.sin(Math.toRadians(cameraRotation)) * speed + cameraZ;
                cameraX = x;
                cameraZ = z;
                break;
            }
            case KeyEvent.VK_DOWN: {
            	double x = cameraX - Math.cos(Math.toRadians(cameraRotation)) * speed;
                double z = cameraZ - Math.sin(Math.toRadians(cameraRotation)) * speed;
                cameraX = x;
                cameraZ = z;
            	break;
            }
            //LEFT RIGHT is camera rotation
            case KeyEvent.VK_LEFT: {
            	cameraRotation = cameraRotation - rotateCamera;
            	if (cameraRotation < 0) cameraRotation = 360;
                break;
            }
            case KeyEvent.VK_RIGHT: {
            	cameraRotation = cameraRotation + rotateCamera;
            	if (cameraRotation > 360) cameraRotation = 0;
            	break;
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
