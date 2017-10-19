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
public class Game extends JFrame implements GLEventListener, KeyListener {

    private Game game;
    private Terrain myTerrain;
    private Avatar avatar;
    //private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private Camera camera;
    //private TriangleVBO triangle;
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
        panel.addKeyListener(this);

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
        avatar.update();
        //for (Enemy enemy: enemies) {
        //}

        enemy.draw(gl);
        myTerrain.draw(gl, texturePack.getTerrain(), texturePack.getRoad());        //triangle.display(drawable);
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
 		
 		camera.initCamera(gl);
 		//for (Enemy enemy: enemies) {
 		enemy.init(gl);
 		//}
 		//triangle.init(drawable);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu = new GLU();

        glu.gluPerspective(60, (float)width/(float)height, 1, 20);
    }
    
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

        float[] diffuseComponent = new float[]{0.8f, 0.8f, 0.8f, 0.1f}; //diffuse all light

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuseComponent, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, finalSunlightVector, 0);

        gl.glPopMatrix();
    }
}
