package ass2.spec;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL2;

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

    /**
     * Draws a road
     *
     * @param gl
     */
    public void draw(GL2 gl) {
        // TODO: Add textures to the road
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

        // This lifts the road slightly above the ground to avoid z-fighting
        double height = getAltitude() + ALTITUDE_OFFSET;
        // The amount we will increment each time along the curve
        double step = (myPoints.size() / 6.0) / NUM_ROAD_SEGMENTS;
        // The distance of the road, our upper bound for iterating along
        // the curve step by step
        double roadDistance = (myPoints.size() / 6.0) - (1.0/3.0) - (2 * step);

        // Constants for the material of the road
        float[] ambient = {1f, 1f, 1f, 1.0f};
        float[] diffuse = {1f, 1f, 1f, 1.0f};
        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        // Increment over the curve starting at 0 and incrementing by the
        // value of step until we have incremented over the entire distance
        // of the road (roadDistance)
        for (double t = 0.0; t + step < roadDistance; t+=step) {

            // Use the point method to find the coordinates of the curve at the
            // value of t and the subsequent two points
            double[] currP = point(t);
            double[] nextP = point(t + step);
            double[] lastP = point(t+step*2);

            // Convert these to vectors by inserting the height value as the
            // y-coordinate and the x and z coordinates from the point method
            double[] currV = {currP[0], height, currP[1]};
            double[] nextV = {nextP[0], height, nextP[1]};
            double[] lastV = {lastP[0], height, lastP[1]};

            // TODO: Check to see if any of the vectors are 0 as this will be invalid input to some Mathutil methods

            // Calculate the vector from the current point to the next point
            // This will be the segment of the curve that we draw this repetition
            double[] currNextVector = {currP[0]-nextP[0], height, currP[1]-nextP[1]};

            // The up vector is the direction of up in our world (positive y direction)
            double[] upVector = {0,1,0};
            // Calculate the vector perpendicular to the current point and the next point
            // This is the cross product of the vector of the segment and the up vector
            double[] perpVectorCN = MathUtil.getUnitVector(MathUtil.crossProduct(currNextVector, upVector));
            // Multiply this by half the width
            // This will be used for getting the points at the extremes of the width of the curve
            // at the start of the segmentperpVectorCN[0] = perpVectorCN[0]*(width()/2);
            perpVectorCN[1] = perpVectorCN[1]*(width()/2);
            perpVectorCN[2] = perpVectorCN[2]*(width()/2);

            // TODO: See if using the perpVectorNL caused the segment to taper

            // Calculate the vector from the next point the the last point
            // This is the next segment, although we are not drawing it, we will need the
            // vector perpendicular to it to find the last two points of our segment
            double[] nextLastVector = {nextP[0]-lastP[0], height, nextP[1]-lastP[1]};
            // Calculate the vector perpendicular to this vector similar to before
            double[] perpVectorNL = MathUtil.getUnitVector(MathUtil.crossProduct(nextLastVector, upVector));
            // Multiply it by half the width
            perpVectorNL[0] = perpVectorNL[0]*(width()/2);
            perpVectorNL[1] = perpVectorNL[1]*(width()/2);
            perpVectorNL[2] = perpVectorNL[2]*(width()/2);

            // Find the four points of the quad for the segment of the road
            double[] currL = {currV[0]-perpVectorCN[0],currV[1]-perpVectorCN[1],currV[2]-perpVectorCN[2]};
            double[] currR = {currV[0]+perpVectorCN[0],currV[1]+perpVectorCN[1],currV[2]+perpVectorCN[2]};
            // The reason we use the perpVectorNL is that it caused the segment to overlap with the segment
            // That will proceed it, meaning that there are no gaps in the curve between segments due to
            // the segments being at different angles and meeting only at the closest corner
            double[] nextL = {nextV[0]-perpVectorNL[0],nextV[1]-perpVectorNL[1],nextV[2]-perpVectorNL[2]};
            double[] nextR = {nextV[0]+perpVectorNL[0],nextV[1]+perpVectorNL[1],nextV[2]+perpVectorNL[2]};

            // We draw the segment as two triangles in anticlockwise order
            // The road is always flat so the normal is just the up vector
            gl.glBegin(GL2.GL_TRIANGLES);
            {
                // Draw the left triangle
                gl.glNormal3dv(upVector,0);
                gl.glVertex3dv(currL,0);
                gl.glVertex3dv(nextL,0);
                gl.glVertex3dv(nextR,0);

                // Draw the right triangle
                gl.glNormal3dv(upVector,0);
                gl.glVertex3dv(currR,0);
                gl.glVertex3dv(currL,0);
                gl.glVertex3dv(nextR,0);
            }
            gl.glEnd();
            // Set the polygonal mode to fill ot avoid GPU glitch
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        }

        // Pop the lighting and matrix
        gl.glPopAttrib();
        gl.glPopMatrix();
    }

}
