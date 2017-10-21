package ass2.spec;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;

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

    public void draw(GL2 gl, Texture trunkTexture, Texture leavesTexture) {

        // TODO: Replace this with a different object
        // TODO: Add textures

        // Push the matrix and lighting
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

            //Get texture
            trunkTexture.enable(gl);
            trunkTexture.bind(gl);

            // First make the cylinder (trunk) of the tree
            gl.glTranslated(myPos[0], myPos[1]-TRUNK_INTERPOLATION_CORRECTION, myPos[2]);
            gl.glRotated(-90.0, 1, 0, 0);

            GLUquadric gluQuadratic = glu.gluNewQuadric();
            glu.gluQuadricTexture(gluQuadratic, true);
            glu.gluQuadricNormals(gluQuadratic, GLU.GLU_SMOOTH);
            glu.gluCylinder(gluQuadratic, 0.05f, 0.05f, 0.8f, 60, 60);
            trunkTexture.disable(gl);
        }

        // Pop the matrix and lighting
        gl.glPopAttrib();
        gl.glPopMatrix();

        gl.glPushMatrix();
        {
            //Set leaves material
            float[] ambient = {0.3f, 0.4f, 0.3f, 1.0f};
            float[] diffuse = {0.0f, 0.5f, 0.0f, 0.5f};
            float[] specular = {0.5f, 0.5f, 0.5f, 0.7f};

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

            //Get texture
            leavesTexture.enable(gl);
            leavesTexture.bind(gl);

            //Now make the spherical top of the trees which will sit on top of the cylinder
            gl.glTranslated(myPos[0], myPos[1] + (0.8f - TRUNK_INTERPOLATION_CORRECTION), myPos[2]);

            GLUquadric gluQuadratic = glu.gluNewQuadric();
            glu.gluQuadricTexture(gluQuadratic, true);
            glu.gluQuadricNormals(gluQuadratic, GLU.GLU_SMOOTH);
            glu.gluSphere(gluQuadratic, 0.25f, 60, 60);

            leavesTexture.disable(gl);
        }
        gl.glPopMatrix();

        gl.glPopAttrib();
        gl.glPopMatrix();
    }

}
