package io.github.gucksus.simplefirstgame.entities.bullets;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.animation.AnimSpec;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.maths.Circular;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

public class AquaShield extends Bullet {
    MainShip mainShip;
    Circular circular;
    AnimSpec<Vector2> circularAnimSpec;
    String id = UUID.randomUUID().toString();

    public AquaShield(TextureRegion[] idleAnimationFrames, float width, float height, float iniX,
            float iniY, SpriteBatch batch, MainShip mainShip) {
        super(idleAnimationFrames, iniX, iniY, 1, 1, batch);
        this.mainShip = mainShip;
        sprite.setOriginCenter();
        rectangleHitbox = new BoxWithOffset(iniX, iniY, 10, 13, 10, 9, pixelLength.x, pixelLength.y);
    }

    @Override
    public void update() {
        circular.setCenter(mainShip.getPastPositions().first());
    }

    @Override
    public void playAnimation() {
        circular = new Circular(getCoordinate(), mainShip.getPastPositions().first(), 99);
        circularAnimSpec = new AnimSpec<>(circular, (value, progess) -> {
            this.setPosition(value.x, value.y);
            this.setRotation(circular.getAngle());
        }, 0, 200, 0, 0);
        Constants.circularAnimScheduler.play(id + "Circular", circularAnimSpec);
    }

}
