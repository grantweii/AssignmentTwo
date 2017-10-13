package ass2.spec;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class InputKeyListener implements KeyListener {
	
	@Override
	public void keyPressed(KeyEvent e) {
				
		switch (e.getKeyCode()) {

    	//UP, DOWN is camera translation
        case KeyEvent.VK_UP: {
            double x = Math.cos(Math.toRadians(Game.cameraRotation)) * Game.speed + Game.cameraX;
            double z = Math.sin(Math.toRadians(Game.cameraRotation)) * Game.speed + Game.cameraZ;
            Game.cameraX = x;
            Game.cameraZ = z;
            break;
        }
        case KeyEvent.VK_DOWN: {
        	double x = Game.cameraX - Math.cos(Math.toRadians(Game.cameraRotation)) * Game.speed;
            double z = Game.cameraZ - Math.sin(Math.toRadians(Game.cameraRotation)) * Game.speed;
            Game.cameraX = x;
            Game.cameraZ = z;
        	break;
        }
        //LEFT RIGHT is camera rotation
        case KeyEvent.VK_LEFT: {
        	Game.cameraRotation = Game.cameraRotation - Game.rotateCamera;
        	if (Game.cameraRotation < 0) Game.cameraRotation = 360;
            break;
        }
        case KeyEvent.VK_RIGHT: {
        	Game.cameraRotation = Game.cameraRotation + Game.rotateCamera;
        	if (Game.cameraRotation > 360) Game.cameraRotation = 0;
        	break;
        }
        default:
            break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
