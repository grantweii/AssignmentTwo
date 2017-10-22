package ass2.spec;

/**
 * Created by Glover on 13/10/17.
 */
public class MathUtil {

    /**
     * Returns the cross product of the vectors u and v
     *
     * @param u a vector with x, y, z in ascending indexes
     * @param v a vector with x, y, z in ascending indexes
     * @return  cross product of u and v
     */
    public static double[] crossProduct(double u[], double v[]) {
        double crossProduct[] = {u[1] * v[2] - u[2] * v[1],
                                 u[2] * v[0] - u[0] * v[2],
                                 u[0] * v[1] - u[1] * v[0]};
        return crossProduct;
    }

    /**
     * Return the normal to the plane which contains the three points
     * p0, p1, and p2
     *
     * @param p0 a point
     * @param p1 a point
     * @param p2 a point
     * @return   the normal to the plane containing the points
     */
    public static double[] getNormal(double [] p0, double[] p1, double[] p2) {
        double u[] = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
        double v[] = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};
        return crossProduct(u, v);
    }

    /**
     * Convert the vector v into a unit vector
     *
     * @param v a vector
     * @return  a unit vector in the same direction as v
     */
    public static double[] getUnitVector(double[] v) {
        double mod = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]); // Modulus of the vector v
        double[] unitVector = {v[0]/mod,v[1]/mod,v[2]/mod};
        return unitVector;
    }

    public static boolean isZero (double[] v) {
        if (v[0] == 0 && v[1] == 0 && v[2] == 0) return true;
        return false;
    }

    // TODO: Does this function have any use
    public static void normalize(double[] v) {
    	double d = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);  
        if (d != 0.0) {  
           v[0]/=d; 
           v[1]/=d;  
           v[2]/=d;  
        }
    }

}
