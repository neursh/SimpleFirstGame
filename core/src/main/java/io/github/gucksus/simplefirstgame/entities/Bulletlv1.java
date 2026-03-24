package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bulletlv1 {
    final float speed = 5f;
//    final float damage = 1;
    public Sprite bulletSelfSprite;
    Texture bulletlv1Texture;

    public Bulletlv1(float iniX, float iniY) {
        bulletlv1Texture = new Texture("bullet_texture.png");
        bulletSelfSprite = new Sprite(bulletlv1Texture);
        bulletSelfSprite.setSize(.5f, .5f);
        bulletSelfSprite.setCenterX(iniX);
        bulletSelfSprite.setY(iniY);
    }

    public void update(float delta){
        bulletSelfSprite.translateY(delta * speed);
    }
}
