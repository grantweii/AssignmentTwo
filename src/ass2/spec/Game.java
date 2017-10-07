package ass2.spec;

import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.glu.GLU;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{

    private Terrain myTerrain;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
   
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
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        int x, z;
        for (z = 0; z < 5; z++) {
            for (x = 0; x < 5; x++) {
                System.out.print(terrain.getGridAltitude(x,z) + " ");
            }
            System.out.println();
        }

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
        // Back face culling on
        int z, x;
        for (z = 0; z < myTerrain.size().width-1; z++) {
            for (x = 0; x < myTerrain.size().height-1; x++) {
                gl.glPolygonMode(GL2.GL_FRONT_AND_BACK,GL2.GL_LINE);
                gl.glBegin(GL2.GL_TRIANGLE_FAN);{
                    gl.glColor3f(1,1,1);
                    // Top left triangle of square
                    gl.glVertex3d(x, myTerrain.getGridAltitude(x, z), -z-5);
                    gl.glVertex3d(x, myTerrain.getGridAltitude(x, z+1), -z-5+1);
                    gl.glVertex3d(x+1, myTerrain.getGridAltitude(x+1, z), -z-5);
                    gl.glVertex3d(x+1, myTerrain.getGridAltitude(x+1, z+1), -z-5+1);
                }
                gl.glEnd();
                gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            }
        }

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU glu = new GLU();

        glu.gluPerspective(120,1,1,20);
    }
}
