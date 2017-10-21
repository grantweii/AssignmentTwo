package ass2.spec;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.IOException;

/**
 * Created by Glover on 19/10/17.
 */
public class TexturePack {

    private Texture terrain;
    private Texture road;
    private Texture avatar;
    private Texture enemy;
    private Texture portal;

    public void load() {
        try {
            setTerrain(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/grass.jpg"), true, TextureIO.JPG));
            setRoad(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/rainbow.png"), true, TextureIO.PNG));
            setAvatar(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/world.jpg"), true, TextureIO.JPG));
            setPortal(TextureIO.newTexture(this.getClass().getResourceAsStream("/textures/portal.png"), true, TextureIO.JPG));
        } catch (IOException e) {
            // Texture file does not exist
            e.printStackTrace();
        }
    }

    public Texture getTerrain() {
        return terrain;
    }

    public void setTerrain(Texture terrain) {
        this.terrain = terrain;
    }

    public Texture getRoad() {
        return road;
    }
    
    public Texture getAvatar() {
    	return avatar;
    }
    
    public Texture getPortal() {
    	return portal;
    }

    public void setRoad(Texture road) {
        this.road = road;
    }
    
    public void setAvatar(Texture avatar) {
    	this.avatar = avatar;
    }
    
    public void setEnemy(Texture enemy) {
    	this.enemy = enemy;
    }
    
    public void setPortal(Texture portal) {
    	this.portal = portal;
    }

}