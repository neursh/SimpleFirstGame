package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.EnemyBullet;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

public class PopcornEnemy extends Enemy {
    public PopcornEnemy(TextureRegion staticTexture, float iniX, float iniY) {
        super(staticTexture , iniX, iniY, 1, 1);
        health = 1f;
        hitboxes.add(new BoxWithOffset(iniX, iniY, width / 32 * 20f, height / 32 * 16, pixelLengthX * 6, pixelLengthY * 6));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, width / 32 * 26f, height / 32 * 15, pixelLengthX * 3, pixelLengthY * 7));
        shootAnimationFrameNum = 0;
        deathAnimationFrameNum = 0;
    }

    @Override
    protected EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return null;
    }

    @Override
    protected boolean shootThisFrame() {return false;}
}
