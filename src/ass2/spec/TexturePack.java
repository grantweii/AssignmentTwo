package ass2.spec;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Created by Glover on 19/10/17.
 */
public class TexturePack {

    private Texture terrain;
    private Texture road;
    private Texture avatar;
    private Texture enemy;
    private Texture portal;

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