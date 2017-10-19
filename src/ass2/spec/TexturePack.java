package ass2.spec;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Created by Glover on 19/10/17.
 */
public class TexturePack {

    private Texture terrain;
    private Texture road;

    public Texture getTerrain() {
        return terrain;
    }

    public void setTerrain(Texture terrain) {
        this.terrain = terrain;
    }

    public Texture getRoad() {
        return road;
    }

    public void setRoad(Texture road) {
        this.road = road;
    }
}