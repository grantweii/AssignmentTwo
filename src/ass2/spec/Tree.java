package ass2.spec;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;

    private static double TREE_HEIGHT = 1;
    private static double WIDTH_MULTIPLIER = 0.1;
    private static double TRUNK_INTERPOLATION_CORRECTION = 0.2;
    private static double NUM_SLICES = 32;
    private static boolean CYLINDER = true;

    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }

    public void draw(GL2 gl) {

        // Push matrix and lighting
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

        // Set the polygonal mode to fill
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Lighting
        float[] ambient = {0.5f, 0.25f, 0.25f, 1.0f};
        float[] diffuse = {0.3f, 0.2f, 0.2f, 1.0f};
        float[] specular = {0f, 0f, 0f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        // Back face culling
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);

        // Set the height of the cylinder
        double y1 = 0;
        double y2 = TREE_HEIGHT;

        // Sides of the tree (cylinder)
        gl.glBegin(GL2.GL_QUADS);
        {
            double angleStep = 2* Math.PI/NUM_SLICES;
            for (int i = 0; i <= NUM_SLICES; i++) {
                double a0 = i * angleStep;
                double a1 = ((i+1) % NUM_SLICES) * angleStep;

                // Calculate vertices for the quad
                double x0 = Math.cos(a0)*WIDTH_MULTIPLIER+getPosition()[0];
                double z0 = Math.sin(a0)*WIDTH_MULTIPLIER+getPosition()[2];

                double x1 = Math.cos(a1)*WIDTH_MULTIPLIER+getPosition()[0];
                double z1 = Math.sin(a1)*WIDTH_MULTIPLIER+getPosition()[2];

                // Calculating normals for each face
                if (CYLINDER) {
                    gl.glNormal3d(x0, 0, z0);
                } else {
                    //Use the face normal for all 4 vertices in the quad.
                    gl.glNormal3d(-(y2-y1)*(z1-z0),(x1-x0)*(y2-y1),0);
                }

                gl.glVertex3d(x0, y1, z0);
                gl.glVertex3d(x0, y2, z0);

                //If we want it to be smooth like a cylinder
                //use different normals for each different x and y
                if (CYLINDER) gl.glNormal3d(x1, 0, z1);

                gl.glVertex3d(x1, y2, z1);
                gl.glVertex3d(x1, y1, z1);
            }
        }
        gl.glEnd();

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);

        gl.glPopAttrib();
        gl.glPopMatrix();

    }

    public void draw2(GL2 gl) {

        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

        GLU glu = new GLU();

        {
            // Set trunk material
            float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
            float[] diffuse = {0.3f, 0.1f, 0.0f, 1.0f};
            float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

            // First make the cylinder (trunk) of the tree
            gl.glTranslated(myPos[0], myPos[1]-TRUNK_INTERPOLATION_CORRECTION, myPos[2]);
            gl.glRotated(-90.0, 1, 0, 0);

            GLUquadric gluQuadratic = glu.gluNewQuadric();
            glu.gluQuadricTexture(gluQuadratic, true);
            glu.gluQuadricNormals(gluQuadratic, GLU.GLU_SMOOTH);
            glu.gluCylinder(gluQuadratic, 0.05f, 0.05f, 0.8f, 60, 60);

        }
        gl.glPopAttrib();
        gl.glPopMatrix();

    }
    

}
