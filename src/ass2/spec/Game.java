package ass2.spec;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.jogamp.opengl.util.texture.TextureIO;

// TODO: Change around sun mode and night mode code
/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

    // Main objects
    private Game game;
    private Terrain myTerrain;
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

    // Day mode variables
    private float sunColFactor = 0;
    private float sunPosFactor = -0.4999f;
    private boolean sunForward = true;
    private float[] earlySunColor = {0.623f,0.594f,0.58035f};
    private float[] lateSunColor = {1f,0.576f,0.161f};
    private float[] earlySkyColor = {0.529411f, 0.807843f, 0.980392f};
    private float[] lateSkyColor = {0.976f, 0.820f, 0.522f};

    // Night mode variables
    private boolean isNight = false;
    private boolean isTorchOn = true;

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
 		camera = new Camera(avatar);
 		// TODO: Once enemies is part of the terrain file we should not need to initialise it here
    	enemies = new ArrayList<Enemy>();
        initEnemies(terrain);
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
        // Game listens for day/night mode and torch keys
        panel.addKeyListener(game);

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
        if (!isNight) {
            setupDay(gl);
        } else {
            setupNight(gl);
        }

        // TODO: Move this check to avatar
        // If we are in third person than draw the avatar
        if (avatar.getThirdPerson()) avatar.draw(gl);

        // TODO: Implement an avatar method which returns its coordinates
        // Draw enemies
        float[] torchCoordinates = { (float) avatar.getX(), (float) avatar.getY(), (float) avatar.getZ() };
        for (Enemy enemy: enemies) {
        	enemy.draw(gl,TextureShader,isNight,torchCoordinates);
        }

        // Draw terrain
        myTerrain.draw(gl, texturePack.getTerrain(), texturePack.getRoad());

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
        try {
            texturePack.setTerrain(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/grass.jpg"), true, TextureIO.JPG));
            texturePack.setRoad(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/rainbow.png"), true, TextureIO.PNG));
            texturePack.setAvatar(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/world.jpg"), true, TextureIO.JPG));
            texturePack.setPortal(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/portal.png"), true, TextureIO.JPG));
        } catch (IOException e) {
            // Texture file does not exist
            e.printStackTrace();
        }

        // Generate mip maps for each texture
        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        // Try to load textures
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

    private void initEnemies(Terrain terrain) {
        for (int i = 0; i < 3; i++) {
            Enemy e = new Enemy(terrain);
            enemies.add(e);
        }
    }

    // TODO: Possibly move all the sunlight code to its own class
    // TODO: Get the sun's position to move in an arch, starting from below the horizon
    private void setupDay(GL2 gl) {
        gl.glPushMatrix();

        // Change sunlight factors so that the position of the sun and its colour shift
        if (sunForward) {
            sunColFactor += 0.002;
            sunPosFactor += 0.002;
        } else {
            sunColFactor -= 0.002;
            sunPosFactor -= 0.002;
        }
        if (sunColFactor > 1) {
            sunForward = false;
            sunColFactor = 0.9999f;
            sunPosFactor = 0.4999f;
        } else if (sunColFactor < 0) {
            sunForward = true;
            sunColFactor = 0.0001f;
            sunPosFactor = -0.4999f;
        }

        // Enable sun light source
        gl.glEnable(GL2.GL_LIGHT1);

        // Turn off torch
        gl.glDisable(GL2.GL_LIGHT2);

        // Interpolate between the early and late sunlight colours using the
        // the sunColFactor
        float[] sunColor = {earlySunColor[0]*sunColFactor + lateSunColor[0]*(1-sunColFactor),
                            earlySunColor[1]*sunColFactor + lateSunColor[1]*(1-sunColFactor),
                            earlySunColor[2]*sunColFactor + lateSunColor[2]*(1-sunColFactor)};

        // Interpolate between the early and late sky colours
        float[] skyColor = {earlySkyColor[0]*sunColFactor + lateSkyColor[0]*(1-sunColFactor),
                            earlySkyColor[1]*sunColFactor + lateSkyColor[1]*(1-sunColFactor),
                            earlySkyColor[2]*sunColFactor + lateSkyColor[2]*(1-sunColFactor)};

        // Background colour
        gl.glClearColor(skyColor[0], skyColor[1], skyColor[2], 1.0f);

        // Global Ambient light
        float[] globalAmb = {sunColor[0], sunColor[1], sunColor[2], 1f}; //full intensity
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmb, 0);

        // Sunlight
        float[] sunlightVector = myTerrain.getSunlight();
        float[] finalSunlightVector = new float[4];

        // Shift the sun's position using
        finalSunlightVector[0] = sunlightVector[0]+sunPosFactor;
        finalSunlightVector[1] = sunlightVector[1];
        finalSunlightVector[2] = sunlightVector[2]-sunPosFactor;
        finalSunlightVector[3] = 0; // Sunlight is directional light

        // Diffuse component of sunlight
        float[] diffuseComponent = new float[]{sunColor[0], sunColor[1], sunColor[2], 0.1f};

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuseComponent, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, finalSunlightVector, 0);

        gl.glPopMatrix();
    }

    private void setupNight(GL2 gl) {

        gl.glPushMatrix();

        // Enable moonlight, provides ambient light
        gl.glEnable(GL2.GL_LIGHT1);

        // Night sky colour
        gl.glClearColor(0.0249f, 0.093f, 0.187f, 1.0f);

        // Moonlight
        float[] globalAmb = {0.2f, 0.2f, 0.2f, 1f}; //full intensity
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmb, 0);

        float[] moonlightVector = myTerrain.getSunlight();
        float[] finalMoonlightVector = new float[4];

        finalMoonlightVector[0] = moonlightVector[0]+sunPosFactor;
        finalMoonlightVector[1] = moonlightVector[1];
        finalMoonlightVector[2] = moonlightVector[2]-sunPosFactor;
        finalMoonlightVector[3] = 0; // Moonlight is directional

        // TODO: See if we need a diffuse component for the moonlight
        float[] diffuseComponent = new float[]{0.1f,0.1f,0.1f, 0.1f};

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuseComponent, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, finalMoonlightVector, 0);

        // Torch light
        if (isTorchOn) {
            gl.glEnable(GL2.GL_LIGHT2);
        } else {
            gl.glDisable(GL2.GL_LIGHT2);
        }

        float lightDifAndSpec[] = {1.0f, 1.0f, 1.0f, 1.0f};

        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, lightDifAndSpec, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, lightDifAndSpec, 0);

        // Set torch position to camera position
        float[] torchPosition = {(float)avatar.getX(), (float)avatar.getY(), (float)avatar.getZ(), 1.0f};
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, torchPosition, 0);

        // Set torch direction (facing outwards from avatar)
        float[] torchDirection = {(float)Math.cos(Math.toRadians(avatar.getRotation())), 0.0f, (float)Math.sin(Math.toRadians(avatar.getRotation()))};
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPOT_DIRECTION, torchDirection, 0);

        // Set cut off and attenuation
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_CUTOFF, 15.0f);
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_EXPONENT, 0.0f);

        gl.glPopMatrix();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            //UP, DOWN is translation
            case KeyEvent.VK_N: {
                isNight = !isNight;
                break;
            }
            case KeyEvent.VK_M: {
                isTorchOn = !isTorchOn;
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
