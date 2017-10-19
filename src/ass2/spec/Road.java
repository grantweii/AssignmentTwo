package ass2.spec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

/**
 * COMMENT: Comment Road
 *
 * @author malcolmr
 */
public class Road {

    private List<Double> myPoints;
    private double myWidth;
    private double altitude;

    private static int NUM_ROAD_SEGMENTS = 100;
    private static double ALTITUDE_OFFSET = 0.01;

    /**
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine, double altitude) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
        this.altitude = altitude;
    }

    /**
     * The width of the road.
     *
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);
    }

    /**
     * Get the number of segments in the curve
     *
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     *
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }

    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     *
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;

        i *= 6;

        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);

        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;

        return p;
    }

    public double getAltitude() {
        return altitude;
    }

    /**
     * Calculate the Bezier coefficients
     *
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {

        switch(i) {

            case 0:
                return (1-t) * (1-t) * (1-t);

            case 1:
                return 3 * (1-t) * (1-t) * t;

            case 2:
                return 3 * (1-t) * t * t;

            case 3:
                return t * t * t;
        }

        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    public void draw(GL2 gl, Texture texture) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

        texture.enable(gl);
        texture.bind(gl);

        double height = getAltitude() + ALTITUDE_OFFSET;
        double step = (myPoints.size() / 6.0) / NUM_ROAD_SEGMENTS;
        double roadDistance = (myPoints.size() / 6.0) - (1.0/3.0) - (2 * step);

        float[] ambient = {1f, 1f, 1f, 1.0f};
        float[] diffuse = {1f, 1f, 1f, 1.0f};
        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        for (double t = 0.0; t < roadDistance; t+=step) {

            double[] currP = point(t);
            double[] currV = {currP[0], height, currP[1]};
            double[] nextP = point(t + step);
            double[] nextV = {nextP[0], height, nextP[1]};
            double[] lastP = point(t+step*2);
            double[] lastV = {lastP[0], height, lastP[1]};

            double[] currNextVector = {currP[0]-nextP[0], height, currP[1]-nextP[1]};
            double[] upVector = {0,1,0};
            double[] perpVectorCN = MathUtil.getUnitVector(MathUtil.crossProduct(currNextVector, upVector));
            perpVectorCN[0] = perpVectorCN[0]*(width()/2);
            perpVectorCN[1] = perpVectorCN[1]*(width()/2);
            perpVectorCN[2] = perpVectorCN[2]*(width()/2);

            double[] nextLastVector = {nextP[0]-lastP[0], height, nextP[1]-lastP[1]};
            double[] perpVectorNL = MathUtil.getUnitVector(MathUtil.crossProduct(nextLastVector, upVector));
            perpVectorNL[0] = perpVectorNL[0]*(width()/2);
            perpVectorNL[1] = perpVectorNL[1]*(width()/2);
            perpVectorNL[2] = perpVectorNL[2]*(width()/2);

            // Find the four points of the quad for the segment of the road
            double[] currL = {currV[0]-perpVectorCN[0],currV[1]-perpVectorCN[1],currV[2]-perpVectorCN[2]};
            double[] currR = {currV[0]+perpVectorCN[0],currV[1]+perpVectorCN[1],currV[2]+perpVectorCN[2]};
            double[] nextL = {nextV[0]-perpVectorNL[0],nextV[1]-perpVectorNL[1],nextV[2]-perpVectorNL[2]};
            double[] nextR = {nextV[0]+perpVectorNL[0],nextV[1]+perpVectorNL[1],nextV[2]+perpVectorNL[2]};

            gl.glBegin(GL2.GL_TRIANGLES);
            {
                gl.glNormal3d(0,1,0);
                gl.glTexCoord2d(1,0);
                gl.glVertex3dv(currL,0);
                gl.glTexCoord2d(1,1);
                gl.glVertex3dv(nextL,0);
                gl.glTexCoord2d(0,1);
                gl.glVertex3dv(nextR,0);
                gl.glNormal3d(0,1,0);
                gl.glTexCoord2d(0,0);
                gl.glVertex3dv(currR,0);
                gl.glTexCoord2d(1,0);
                gl.glVertex3dv(currL,0);
                gl.glTexCoord2d(0,1);
                gl.glVertex3dv(nextR,0);
            }
            gl.glEnd();
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        }
        texture.disable(gl);

        gl.glPopAttrib();
        gl.glPopMatrix();
    }

    private double[] unitVector(double x, double y, double z) {
        double amplitudeReciprocal = 1/(Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+ Math.pow(z,2)));
        double unitX = x*amplitudeReciprocal;
        double unitY = y*amplitudeReciprocal;
        double unitZ = z*amplitudeReciprocal;

        double [] unitVector = {unitX, unitY, unitZ};
        return unitVector;
    }


}