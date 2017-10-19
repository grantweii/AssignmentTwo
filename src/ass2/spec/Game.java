package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener {

    private Game game;
    private Terrain myTerrain;
    private Avatar avatar;
    //private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private Camera camera;
    //private TriangleVBO triangle;

    private int shaderProgram;
    private static final String VERTEX_SHADER = "src/ass2/spec/PassThroughVertex.glsl";
    private static final String FRAGMENT_SHADER = "src/ass2/spec/PassThroughFragment.glsl";

    private TexturePack texturePack;

    public Game(Terrain terrain) {
        super("Assignment 2");
        myTerrain = terrain;
        game = this;
 		avatar = new Avatar(myTerrain);
 		camera = new Camera(avatar);
    	//this.enemies = new ArrayList<Enemy>();
 		//initEnemies(terrain);
 		enemy = new Enemy(terrain, 2, 2);
 		//triangle = new TriangleVBO();
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
        panel.addKeyListener(avatar);

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

        // Setup camera
        camera.setView(gl);

        // Setup Sunlight
        setupSun(gl);

        if (avatar.getThirdPerson()) avatar.draw(gl);
        //for (Enemy enemy: enemies) {
        //}

        //enemy.draw(gl);
        myTerrain.draw(gl, texturePack.getTerrain(), texturePack.getRoad());
        //triangle.display(drawable);
    }
    
    public void initEnemies(Terrain terrain) {
    	Enemy enemy1 = new Enemy(terrain, 2, 1);
    	//enemies.add(enemy1);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //enemy.dispose(gl);
        //triangle.dispose(drawable);
    }
  
    @Override
    public void init(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);
        // By enabling lighting, color is worked out differently.
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        try {
            texturePack.setTerrain(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/grass.jpg"), true, TextureIO.JPG));
            texturePack.setRoad(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/rainbow.png"), true, TextureIO.PNG));
        } catch (IOException e) {
            System.out.println("here");
            e.printStackTrace();
        }
        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        // When you enable lighting you must still actually
        // turn on a light such as this default light.
        // gl.glEnable(GL2.GL_LIGHT0);
 		
        try {
	   		shaderProgram = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu = new GLU();

        glu.gluPerspective(60, (float)width/(float)height, 0.01, 20);
    }

    private void setupSun(GL2 gl) {
        gl.glPushMatrix();

        gl.glEnable(GL2.GL_LIGHT1);

        //Background colour
        gl.glClearColor(0.529411f, 0.807843f, 0.980392f, 1.0f); //Sky Blue, RGB: 135-206-250

        //Global Ambient light
        float[] globalAmb = {1f, 1f, 1f, 1f}; //full intensity
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmb, 0);

        //Sunlight (LIGHT1)
        float[] sunlightVector = myTerrain.getSunlight();
        float[] finalSunlightVector = new float[4];

        finalSunlightVector[0] = sunlightVector[0];
        finalSunlightVector[1] = sunlightVector[1];
        finalSunlightVector[2] = sunlightVector[2];
        finalSunlightVector[3] = 0; //for directional light

        float[] diffuseComponent = new float[]{0.2f, 0.2f, 0.2f, 0.1f}; //diffuse all light

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuseComponent, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, finalSunlightVector, 0);

        gl.glPopMatrix();
    }

}
