package ass2.spec;

import javax.swing.JFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{

    // Main objects
    private Game game;
    private Terrain myTerrain;
    private Lighting lighting;
    private Avatar avatar;
    private ArrayList<Enemy> enemies;
    private Camera camera;
    private TexturePack texturePack;
    private PortalPair portal;

    // TODO: Name this
    Random rand;

    // Shader variables
    private int PassthroughShader;
    private int TextureShader;
    private static final String PASSTHROUGH_VERTEX_SHADER = "src/ass2/spec/PassThroughVertex.glsl";
    private static final String PASSTHROUGH_FRAGMENT_SHADER = "src/ass2/spec/PassThroughFragment.glsl";
    private static final String VERTEX_TEX_SHADER = "src/ass2/spec/VertexTex.glsl";
    private static final String FRAGMENT_TEX_SHADER = "src/ass2/spec/FragmentTex.glsl";

    /**
     * Constructor
     *
     * @param terrain terrain of the game
     */
    public Game(Terrain terrain) {
        super("Assignment 2");
        game = this;
        myTerrain = terrain;
 		portal = new PortalPair(terrain);
 		avatar = new Avatar(myTerrain, portal);
        lighting = new Lighting(myTerrain.getSunlight(), avatar);
 		camera = new Camera(avatar);
        rand = new Random();
        texturePack = new TexturePack();
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

        // Add avatar and game as key listeners
        // Avatar listens for movement keys and first/third person
        panel.addKeyListener(avatar);
        // Lighting listens for day/night mode and torch keys
        panel.addKeyListener(lighting);
        // Portal pair listens for if it is enabled
        panel.addKeyListener(portal);

        // Add an animator to call 'display' at 60fps
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();

        // JFrame set up
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
        GL2 gl = drawable.getGL().getGL2();

        // Setup the matrix mode and buffers
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Setup camera
        camera.setView(gl);

        // Setup Sunlight
        lighting.draw(gl);

        // TODO: Move this check to avatar
        // If we are in third person than draw the avatar
        if (avatar.getThirdPerson()) avatar.draw(gl);

        // TODO: Implement an avatar method which returns its coordinates
        // Draw enemies
        float[] torchCoordinates = { (float) avatar.getX(), (float) avatar.getY(), (float) avatar.getZ() };
        for (Enemy enemy: myTerrain.enemies()) {
        	enemy.draw(gl,TextureShader,!lighting.getIsDay(),torchCoordinates,myTerrain.getSunlight(),avatar.getRotation(),lighting.isTorchOn());
        }

        // Draw terrain
        myTerrain.draw(gl, texturePack.getTerrain(), texturePack.getRoad(), texturePack.getTrunk(), texturePack.getLeaves());

        // Draw portals
        portal.draw(gl, texturePack.getPortal());
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
    }
  
    @Override
    public void init(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();

        // Enable depth testing and normalizing
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);

        // By enabling lighting, color is worked out differently.
        gl.glEnable(GL2.GL_LIGHTING);

        // Enable 2D textures
        // Try to load textures from the resources folder
        gl.glEnable(GL2.GL_TEXTURE_2D);
        texturePack.load();

        // Generate mip maps for each texture
        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        // Try to load shaders
        try {
	   		PassthroughShader = Shader.initShaders(gl,PASSTHROUGH_VERTEX_SHADER,PASSTHROUGH_FRAGMENT_SHADER);
	   		TextureShader = Shader.initShaders(gl, VERTEX_TEX_SHADER, FRAGMENT_TEX_SHADER);
        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {

        GL2 gl = drawable.getGL().getGL2();

        // Set the matrix mode and load identity
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu = new GLU();

        // Set the perspective
        glu.gluPerspective(60, (float)width/(float)height, 0.1, 20);
    }

}
