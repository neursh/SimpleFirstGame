package io.github.gucksus.simplefirstgame.entities.enemies;

import java.util.UUID;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.animation.AnimSpec;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.maths.CubicBezier;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class ArchEnemy extends Enemy {
    private String archId = UUID.randomUUID().toString();

    public ArchEnemy(TextureRegion staticTexture, float iniX, float iniY, MainShip mainShip,
            Wave wave) {
        super(staticTexture, iniX, iniY, 1, 1, mainShip, wave);
        health = 2f;
        hitboxes.add(new BoxWithOffset(iniX, iniY, 20, 16, 6, 6, pixelLength.x, pixelLength.y));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, 26, 15, 3, 7, pixelLength.x, pixelLength.y));

        setPosition(v(0, 0));
        mountAnimation();
    }

    private Vector2 v(float x, float y) {
        return new Vector2(x, y);
    }

    /**
     * A demo of the capability of the animation system, do not call it repeatedly.
     */
    public void mountAnimation() {
        CubicBezier moveUp = new CubicBezier(v(0, 0), v(worldWidth * 0.2f, worldHeight * 0.7f),
                v(worldWidth * 0.92f, worldHeight * 0.7f),
                v(worldWidth * 0.4f, worldHeight * 0.9f));

        CubicBezier moveDown = new CubicBezier(v(worldWidth * 0.4f, worldHeight * 0.9f),
                v(worldWidth * 0.1f, worldHeight), v(0, worldHeight), v(worldWidth, 0));

        AnimSpec<Vector2> moveUpDef = new AnimSpec<>(moveUp, (value, progress) -> {
            setPosition(value);
        }, 0, 2, 2, 10);

        AnimSpec<Vector2> moveDownDef = new AnimSpec<>(moveDown, (value, progress) -> {
            setPosition(value);
        }, 2, 2, 0, 10);

        Constants.cubicAnimScheduler.play(this.archId + "archDown", moveDownDef);
        Constants.cubicAnimScheduler.play(this.archId + "archUp", moveUpDef);
    }

    public void setPosition(Vector2 value) {
        sprite.setCenter(value.x, value.y);
    }

    public void takeDamage(float damage) {
        this.health -= damage;

        if (health == 1) {
            Constants.cubicAnimScheduler.stop(this.archId + "archDown");
            Constants.cubicAnimScheduler.stop(this.archId + "archUp");

            CubicBezier fun = new CubicBezier(this.getCoordinate(), v(0, worldHeight),
                    v(worldWidth, worldHeight), this.mainShip.getCoordinate());

            AnimSpec<Vector2> terror = new AnimSpec<>(fun, (value, progress) -> {
                setPosition(value);
            }, 0, 1, 0, 0);

            Constants.cubicAnimScheduler.play(this.archId + "terrorist", terror);
        }
    }

    @Override
    protected Bullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return null;
    }
}
