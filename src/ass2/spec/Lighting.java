package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.jogamp.opengl.GL2;

/**
 * Created by Glover on 21/10/17.
 */
public class Lighting implements KeyListener {

    private static int VECTOR_SIZE = 4;

    // Directions and Positions
    private float[] sunDir = new float[4];
    Avatar avatar; // Torch position is the same as avatar holding it

    // Flags
    private boolean isDay = true;
    private boolean isSunMoving = false;
    private boolean isTorchOn = true;

    // Colors
    private static float[] earlySunColor = {0.623f, 0.594f, 0.580f, 1f};
    private static float[] lateSunColor  = {1.000f, 0.376f, 0.061f, 1f};
    private static float[] earlySkyColor = {0.529f, 0.808f, 0.980f, 1f};
    private static float[] lateSkyColor  = {0.576f, 0.420f, 0.122f, 1f};
    private static float[] nightSkyColor = {0.025f, 0.093f, 0.187f, 1f};

    // Sun movement
    private static float sunT = 0f; // Interpolation value (t)
    private static float SUN_MOVEMENT_SPEED = 0.001f;

    public void draw(GL2 gl) {
        if (isSunMoving) {
            drawMoving(gl);
            return;
        }
        if (isDay) {
            drawDay(gl);
        } else {
            drawNight(gl);
        }
    }

    public Lighting(float[] sunDir, Avatar avatar) {
        this.sunDir[0] = sunDir[0];
        this.sunDir[1] = sunDir[1];
        this.sunDir[2] = sunDir[2];
        this.sunDir[0] = 0;
        this.avatar = avatar;
    }

    private void drawMoving(GL2 gl) {
        // Increment interpolation value
        sunT += SUN_MOVEMENT_SPEED;

        // If sun is below horizon reset sun movement or switch to night
        // base on isSwitchingToNight
        if (Math.sin(sunT*2*Math.PI) < 0) sunT = 0;

        gl.glPushMatrix();
        gl.glEnable(GL2.GL_LIGHT1);  // Enable sunlight
        gl.glDisable(GL2.GL_LIGHT2); // Disable torch

        // Interpolate between the early and late sunlight colours using sunT
        float[] sunColor = {earlySunColor[0]*(1- sunT) + lateSunColor[0]* sunT,
                earlySunColor[1]*(1- sunT) + lateSunColor[1]* sunT,
                earlySunColor[2]*(1- sunT) + lateSunColor[2]* sunT};

        // Interpolate between the early and late sky colours
        float[] skyColor = {earlySkyColor[0]*(1- sunT) + lateSkyColor[0]* sunT,
                earlySkyColor[1]*(1- sunT) + lateSkyColor[1]* sunT,
                earlySkyColor[2]*(1- sunT) + lateSkyColor[2]* sunT};

        // Background colour
        gl.glClearColor(skyColor[0], skyColor[1], skyColor[2], 1.0f);

        // Global ambient light
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, sunColor, 0);

        // Sunlight
        float[] movingSunVec = new float[4];
        movingSunVec[0] = (float) Math.cos(sunT *2*Math.PI);
        movingSunVec[1] = (float) Math.sin(sunT *2*Math.PI);
        movingSunVec[2] = 0;
        movingSunVec[3] = 0; // Sunlight is directional light

        // Global diffuse light
        // Only ambient light when sun is below the horizon
        float[] globDif;
        if (Math.sin(sunT *2*Math.PI) < 0) {
            globDif = new float[]{0, 0, 0, 0};
        } else {
            globDif = new float[]{sunColor[0], sunColor[1], sunColor[2], 1f};
        }
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, globDif, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, movingSunVec, 0);

        gl.glPopMatrix();
    }

    private void drawDay(GL2 gl) {
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_LIGHT1);  // Enable sunlight
        gl.glDisable(GL2.GL_LIGHT2); // Disable torch

        // Sky color
        gl.glClearColor(earlySkyColor[0], earlySkyColor[1], earlySkyColor[2], 1f);

        // Global ambient light
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, earlySunColor, 0);

        // Global diffuse light
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, earlySunColor, 0);

        // Sunlight direction
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, sunDir, 0);

        gl.glPopMatrix();
    }

    private void drawNight(GL2 gl) {
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_LIGHT1); // Enable moonlight

        // Night sky colour
        gl.glClearColor(nightSkyColor[0], nightSkyColor[1], nightSkyColor[2], 1f);

        // Low global ambient light
        float[] globAmb = {0.1f, 0.1f, 0.1f, 1f};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb, 0);

        // Low global diffuse light
        float[] globDif = {0.1f, 0.1f, 0.1f, 1f};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, globDif, 0);

        // Moonlight direction
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, sunDir, 0);

        // Enable or disable torch
        if (isTorchOn) {
            gl.glEnable(GL2.GL_LIGHT2);
        } else {
            gl.glDisable(GL2.GL_LIGHT2);
        }

        // Torch diffuse light
        float lightDif[] = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, lightDif, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, lightDif, 0);

        // Torch position
        float[] torchPosition = {(float)avatar.getX(), (float)avatar.getY(), (float)avatar.getZ(), 1.0f};
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, torchPosition, 0);

        // Torch direction
        float[] torchDirection = {(float)Math.cos(Math.toRadians(avatar.getRotation())), 0.0f, (float)Math.sin(Math.toRadians(avatar.getRotation()))};
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPOT_DIRECTION, torchDirection, 0); //direction vector

        // Torch cut off and attenuation
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_CUTOFF, 45.0f); //cutoff angle
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_EXPONENT, 0.0f); //attenuation

        gl.glPopMatrix();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q: {
                isSunMoving = !isSunMoving;
                break;
            }
            case KeyEvent.VK_W: {
                isDay = !isDay;
                break;
            }
            case KeyEvent.VK_E: {
                isTorchOn = !isTorchOn;
                break;
            }
            default: break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
