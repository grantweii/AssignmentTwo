package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

/**
 * COMMENT: Comment HeightMap
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;
    private List<Enemy> myEnemies;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
        myEnemies = new ArrayList<Enemy>();
    }

    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public List<Enemy> enemies() {
        return myEnemies;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction.
     * <p>
     * Note: the sun should be treated as a directional light, without a position
     *
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;
    }

    /**
     * Resize the terrain, copying any old altitudes.
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];

        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     *
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     *
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    // TODO: Change this code around
    /**
     * Get the altitude at an arbitrary point.
     * Non-integer points should be interpolated from neighbouring grid points
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return
     */
    public double altitude(double x, double z) {
        // Check that the point is within the terrain grid
        // - 1 since a terrain of width 5 will have coordinates only in the range 0..4
        if (x > this.size().width - 1  || x < 0) return 0;
        if (z > this.size().height - 1 || z < 0) return 0;

        // If both x and z are integers then we can just use the getGridAltitude method
        // since the point lies directly on a grid point
        // This avoids having denominators that are 0 later on
        if ((x == Math.floor(x)) && !Double.isInfinite(x) && (z == Math.floor(z)) && !Double.isInfinite(z)) {
//            System.out.println(getGridAltitude((int) x, (int) z));
            return getGridAltitude((int) x, (int) z);
        }

        // Find the four points which are the corners of the grid the point is within
        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.ceil(x);
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.ceil(z);

        double diagonal = (x1 + z2) - z;
        double altitude;

        // Check if the point lies on a grid line
        // If so then use linear interpolation
        // This avoids having denominators that are 0 later on
        if ((x == Math.floor(x)) && !Double.isInfinite(x)) {
            altitude = calcBilinInterpZDir(z, z1, z2, (int) x, (int) x);
        } else if ((z == Math.floor(z)) && !Double.isInfinite(z)) {
            altitude = calcBilinInterpXDir(x, x1, x2, (int) z, (int) z);
        } else if (x < diagonal) { //Point exists in left triangle, interpolate using it
            altitude = calcBilinInterp(x, x1, x1, x2, z, z2, z1, z1, diagonal);
        } else { //x > diagonal, point exists in right triangle, interpolate using it
            altitude = calcBilinInterp(x, x2, x2, x1, z, z1, z2, z2, diagonal);
        }

        return altitude;
    }

    // Used when x is given as an integer
    private double calcBilinInterpZDir(double z, int z1, int z2, int x1, int x2) {
        return (z2-z)/(z2-z1)*getGridAltitude(x1,z1)+(z-z1)/(z2-z1)*getGridAltitude(x2,z2);
    }

    // Used when z is given as an integer
    private double calcBilinInterpXDir(double x, int x1, int x2, int z1, int z2) {
        return (x2-x)/(x2-x1)*getGridAltitude(x1,z1)+(x-x1)/(x2-x1)*getGridAltitude(x2,z2);
    }

    private double calcBilinInterp(double x, int x1, int x2, int x3, double z, int z1, int z2, int z3, double diagonal) {
        return ((x-x1)/(diagonal-x1)*calcBilinInterpZDir(z, z1, z3, x1, x3) +
                (diagonal - x)/(diagonal - x1)* calcBilinInterpZDir(z, z1, z2, x1, x2));
    }

    /**
     * Add a tree at the specified (x,z) point.
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     *
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }

    /**
     * Add a road.
     *
     * @param width
     * @param spine
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine, altitude(spine[0],spine[1]));
        myRoads.add(road);
    }

    public void addEnemy(double x, double z) {
        float y = (float) altitude(x, z);
        Enemy enemy = new Enemy((float) x, y, (float) z);
        System.out.println(x);
        myEnemies.add(enemy);
    }

    /**
     * Draws the terrain
     *
     * @param gl
     */
    public void draw(GL2 gl, Texture terrainTexture, Texture roadTexture, Texture trunkTexture, Texture leaveTexture) {

        // Push the matrix and lighting
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);
        // Set the polygon mode to fill
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Enable texture
        terrainTexture.enable(gl);
        terrainTexture.bind(gl);

        gl.glTexParameteri( GL2.GL_TEXTURE_2D,   GL2.GL_TEXTURE_WRAP_S,
                GL2.GL_MIRRORED_REPEAT);
        gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                GL2.GL_MIRRORED_REPEAT);

        // Set constants for the terrain's material
        float[] ambient = {0.4f, 0.4f, 0.4f, 1.0f};
        float[] diffuse = {0.6f, 0.6f, 0.6f, 1.0f};
        float[] specular = {0.0f, 0.0f, 0.0f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        // Turn on back face culling
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);

        // Increment through each square and draw the left and right triangles
        for (int z = 0; z < this.size().height - 1; z++) {
            for (int x = 0; x < this.size().width - 1; x++) {
                // Points of Left Triangle
                double[] v1 = {x, getGridAltitude(x, z), z};
                double[] v2 = {x, getGridAltitude(x, z + 1), z + 1};
                double[] v3 = {x + 1, getGridAltitude(x + 1, z), z};

                // Find the normal for the triangle
                double[] faceNormL = MathUtil.getNormal(v1, v2, v3);

                // Draw Left Triangle
                gl.glNormal3dv(faceNormL, 0);
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glTexCoord2d(v1[0],v1[2]);
                    gl.glVertex3dv(v1, 0);
                    gl.glTexCoord2d(v2[0],v2[2]);
                    gl.glVertex3dv(v2, 0);
                    gl.glTexCoord2d(v3[0],v3[2]);
                    gl.glVertex3dv(v3, 0);
                }
                gl.glEnd();

                // Points of Right Triangle
                double[] v4 = {x + 1, getGridAltitude(x + 1, z), z};
                double[] v5 = {x, getGridAltitude(x, z + 1), z + 1};
                double[] v6 = {x + 1, getGridAltitude(x + 1, z + 1), z + 1};

                // Find the normal for the triangle
                double[] faceNormR = MathUtil.getNormal(v4, v5, v6);

                // Draw Right Triangle
                gl.glNormal3dv(faceNormR, 0);
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glTexCoord2d(v4[0],v4[2]);
                    gl.glVertex3dv(v4, 0);
                    gl.glTexCoord2d(v5[0],v5[2]);
                    gl.glVertex3dv(v5, 0);
                    gl.glTexCoord2d(v6[0],v6[2]);
                    gl.glVertex3dv(v6, 0);

                }
                gl.glEnd();
            }
        }
        // Disable the terrain texture as we will now apply textures to roads and trees
        terrainTexture.disable(gl);

        // Iterate over the list of trees and draw them
        Iterator treeIt = this.trees().iterator();
        while (treeIt.hasNext()) {
            Tree currTree = (Tree) treeIt.next();
            currTree.draw(gl, trunkTexture, leaveTexture);
        }

        // Iterate over the list of roads and draw them
        Iterator roadIt = this.roads().iterator();
        while (roadIt.hasNext()) {
            Road currRoad = (Road) roadIt.next();
            currRoad.draw(gl, roadTexture);
        }

//        // Iterate over the list of trees and draw them
//        Iterator enemyIt = this.enemies().iterator();
//        while (enemyIt.hasNext()) {
//            Enemy currEnemy = (Enemy) enemyIt.next();
//            currEnemy.draw(gl, shaderProgram, nightEnabled, torchCoordinates);
//        }

        // Set the polygon mode back to fill to avoid GPU glitch
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Pop the lighting and matrix
        gl.glPopAttrib();
        gl.glPopMatrix();
    }

}