package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;

public class BulletHolder {
    public Array<Bullet> enemyBullets = new Array<>();
    public Array<Bullet> shipBullets = new Array<>();
    DebugRenderer debugRenderer;
    float worldHeight;

    public BulletHolder() {
        this.debugRenderer = Constants.debugRenderer;
        this.worldHeight = Constants.worldHeight;
    }

    public void update() {
        bulletUpdate();
        bulletRemoveUpdate();
    }

    public void draw() {
        for (Bullet bullet : enemyBullets) {
            bullet.draw();
        }
        for (Bullet bullet : shipBullets)
            bullet.draw();
    }

    public void drawDebug() {
        for (Bullet bullet : enemyBullets) {
            if (bullet.isCircle()) {
                debugRenderer.drawCircleHitbox(bullet.getCircleHitbox());
            } else {
                debugRenderer.drawHitbox(bullet.getRectangleHitbox());
            }
        }
        for (Bullet bullet : shipBullets) {
            debugRenderer.drawHitbox(bullet.getRectangleHitbox());
        }
    }

    /**
     * This method updates bullet position; checks if a bullet is out of screen and removes any
     * bullet that does.
     */
    private void bulletUpdate() {
        for (int i = shipBullets.size - 1; i >= 0; i--) {
            shipBullets.get(i).update();
        }

        for (Bullet bullet : enemyBullets)
            bullet.update();
    }

    public void bulletRemoveUpdate() {
        for (int i = shipBullets.size - 1; i >= 0; i--) {
            Sprite currentBulletSprite = shipBullets.get(i).getSprite();
            if (currentBulletSprite.getY() > worldHeight) {
                shipBullets.removeIndex(i);
            }
        }
    }
}
