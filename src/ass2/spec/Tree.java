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
    private static double TRUNK_INTERPOLATION_CORRECTION = 0.1;
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

        // Push the matrix and lighting
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING);

        GLU glu = new GLU();
        {
            // Set trunk material
            float[] ambient = {0.5f, 0.5f, 0.5f, 1.0f};
            float[] diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
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

        // Draw trunk

        gl.glPushMatrix();

        // Set trunk material
        float[] ambient = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);

        // Get texture
        leavesTexture.enable(gl);
        leavesTexture.bind(gl);

        gl.glTexParameteri( GL2.GL_TEXTURE_2D,   GL2.GL_TEXTURE_WRAP_S,
                GL2.GL_MIRRORED_REPEAT);
        gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                GL2.GL_MIRRORED_REPEAT);

        int slices = 6;
        double width = 0.3;
        double y1 = myPos[1]-TRUNK_INTERPOLATION_CORRECTION+0.5;
        double y2 = myPos[1]-TRUNK_INTERPOLATION_CORRECTION+2;

        // Sides of the cylinder
        gl.glBegin(GL2.GL_TRIANGLES);
        {
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){
                double a0 = i * angleStep;
                double a1 = ((i+1) % slices) * angleStep;

                //Calculate vertices for the quad
                double x0 = (Math.cos(a0)*width+myPos[0]);
                double z0 = (Math.sin(a0)*width+myPos[2]);

                double x1 = (Math.cos(a1)*width+myPos[0]);
                double z1 = (Math.sin(a1)*width+myPos[2]);

                gl.glNormal3d(-(y2-y1)*(z1-z0),(x1-x0)*(y2-y1),0);
                gl.glTexCoord2d(0,0);
                gl.glVertex3d(x0, y1, z0);
                gl.glTexCoord2d(0.5,3);
                gl.glVertex3d(myPos[0], y2, myPos[2]);
                gl.glTexCoord2d(1,0);
                gl.glVertex3d(x1, y1, z1);

            }

        }
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLE_FAN);{

            gl.glNormal3d(0,0,-1);
            gl.glTexCoord2d(0,0);
            gl.glVertex3d(myPos[0], y1, myPos[2]);
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){

                double a0 = i * angleStep;

                //Calculate vertices for the quad
                double x0 = (Math.cos(a0)*width+myPos[0]);
                double z0 = (Math.sin(a0)*width+myPos[2]);

                if (i%2 == 0) {
                    gl.glTexCoord2d(1,0);
                } else {
                    gl.glTexCoord2d(0,1);

                }
                gl.glVertex3d(x0,y1,z0);
            }


        } gl.glEnd();

        gl.glPopAttrib();
        gl.glPopMatrix();
    }

}
