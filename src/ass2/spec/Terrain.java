package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

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

    /**
     * Get the altitude at an arbitrary point.
     * Non-integer points should be interpolated from neighbouring grid points
     * <p>
     * TO BE COMPLETED
     *
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;


        return altitude;
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
        Road road = new Road(width, spine);
        myRoads.add(road);
    }

    public void draw(GL2 gl) {

        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        float[] ambient = {0.6f, 0.6f, 0.6f, 1.0f};
        float[] diffuse = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] specular = {0.2f, 0.2f, 0.2f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        // Turn on back face culling
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        // Move the map so that it is not in the same position as the camera

        for (int z = 0; z < this.size().height - 1; z++) {
            for (int x = 0; x < this.size().width - 1; x++) {

                // Points of Left Triangle
                double[] v1 = {x, getGridAltitude(x, z), z};
                double[] v2 = {x, getGridAltitude(x, z + 1), z + 1};
                double[] v3 = {x + 1, getGridAltitude(x + 1, z), z};

                double[] faceNormL = getNormal(v1, v2, v3);

                // Draw Left Triangle
//                gl.glColor3f(1, 1, 1);
//                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
                gl.glNormal3dv(faceNormL, 0);
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glVertex3d(v1[0]-2,v1[1],v1[2]+2);
                    gl.glVertex3d(v2[0]-2,v2[1],v2[2]+2);
                    gl.glVertex3d(v3[0]-2,v3[1],v3[2]+2);
                }
                gl.glEnd();
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

                // Points of Right Triangle
                double[] v4 = {x + 1, getGridAltitude(x + 1, z), z};
                double[] v5 = {x, getGridAltitude(x, z + 1), z + 1};
                double[] v6 = {x + 1, getGridAltitude(x + 1, z + 1), z + 1};

                double[] faceNormR = getNormal(v4, v5, v6);

                // Draw Right Triangle
//                gl.glColor3f(1, 1, 1);
//                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
                gl.glNormal3dv(faceNormR, 0);
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glVertex3d(v4[0]-2,v4[1],v4[2]+2);
                    gl.glVertex3d(v5[0]-2,v5[1],v5[2]+2);
                    gl.glVertex3d(v6[0]-2,v6[1],v6[2]+2);

                }
                gl.glEnd();
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
            }

            gl.glPopAttrib();
            gl.glPopMatrix();
        }
    }

    public static double[] getNormal(double[] p0, double[] p1, double[] p2) {
        double u[] = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
        double v[] = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};

        return crossProduct(u, v);
    }

    public static double[] crossProduct(double u[], double v[]) {
        double crossProduct[] = new double[3];
        crossProduct[0] = u[1] * v[2] - u[2] * v[1];
        crossProduct[1] = u[2] * v[0] - u[0] * v[2];
        crossProduct[2] = u[0] * v[1] - u[1] * v[0];

        return crossProduct;

    }

}
