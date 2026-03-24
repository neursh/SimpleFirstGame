package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Bulletlv1 {
    final float speed = 5f;
//    final float damage = 1;
    public Sprite selfSprite;
    Texture bulletlv1Texture;
    public Rectangle hitbox;

    public Bulletlv1(float iniX, float iniY) {
        bulletlv1Texture = new Texture("bullet_texture.png");
        selfSprite = new Sprite(bulletlv1Texture);
        selfSprite.setSize(.5f, .5f);
        selfSprite.setCenterX(iniX);
        selfSprite.setY(iniY);
        hitbox = new Rectangle(iniX, iniY, .5f, .5f);
    }

    public void update(float delta){
        selfSprite.translateY(delta * speed);
        hitbox.setPosition(selfSprite.getX(), selfSprite.getY());
    }
}
