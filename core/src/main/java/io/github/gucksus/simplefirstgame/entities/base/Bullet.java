package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:</b> <i>movingType, isCircle, circleHitBox
 * <b>OR</b> rectangleHitbox, speed.</i>
 */
public class Bullet {
    protected float iniX;
    protected float iniY;
    protected float speed;
    protected float width;
    protected float height;
    float timer;
    protected float damage = 1;
    protected float fireRate;
    protected int maxBulletOnScreen;
    protected Sprite sprite;
    protected BoxWithOffset rectangleHitbox;
    protected Circle circleHitbox;
    protected Vector2 circleHitboxOffset = new Vector2();
    private boolean isCircle;
    protected Vector2 pixelLength;
    Animation<TextureRegion> idleAnimation;
    float idleFrameInterval = .1f;
    int idleAnimationFrameNum;
    float stateTime;

    final float gravity = 9f;
    protected float initialVelocity;
    protected float initialAngle;

    protected enum MovingType {
        Straight, Curve, Roundabout, Oblique
    }

    protected MovingType movingType = MovingType.Straight;
    protected Vector2 direction;
    SpriteBatch batch;

    public Bullet(TextureRegion[] idleAnimationFrames, float iniX, float iniY, float width,
            float height, float dx, float dy, SpriteBatch batch) {
        this.width = width;
        this.height = height;
        this.iniX = iniX;
        this.iniY = iniY;
        if (idleAnimationFrames.length == 1)
            sprite = new Sprite(idleAnimationFrames[0]);
        else
            initializeIdleAnimation(idleAnimationFrames);
        sprite.setSize(width, height);
        sprite.setCenterX(iniX);
        sprite.setCenterY(iniY);
        direction = new Vector2();
        direction.set(dx, dy);
        this.batch = batch;
        pixelLength = new Vector2(width / idleAnimationFrames[0].getRegionWidth(),
                height / idleAnimationFrames[0].getRegionHeight());
    }

    public Bullet(TextureRegion[] idleAnimationFrames, float iniX, float iniY, float width,
            float height, SpriteBatch batch) {
        this.width = width;
        this.height = height;
        this.iniX = iniX;
        this.iniY = iniY;
        sprite = new Sprite(idleAnimationFrames[0]);
        if (idleAnimationFrames.length != 1)
            initializeIdleAnimation(idleAnimationFrames);
        sprite.setSize(width, height);
        sprite.setCenterX(iniX);
        sprite.setCenterY(iniY);
        this.batch = batch;
        pixelLength = new Vector2(width / idleAnimationFrames[0].getRegionWidth(),
                height / idleAnimationFrames[0].getRegionHeight());
    }

    public void initializeIdleAnimation(TextureRegion[] idleAnimationFrames) {
        idleAnimation = new Animation<>(idleFrameInterval, idleAnimationFrames);
        idleAnimationFrameNum = idleAnimationFrames.length;
        stateTime = 0;
    }

    protected void updateHitbox() {
        if (isCircle())
            circleHitbox.setPosition(sprite.getX() + circleHitboxOffset.x,
                    sprite.getY() + circleHitboxOffset.y);
        else
            rectangleHitbox.update(sprite.getX(), sprite.getY());
    }

    /**
     * This works based on 2D vector.
     * 
     * @param delta The frame lastDelta time.
     */
    public void updateStraight(float delta) {
        timer += delta;
        float distanceMultiplier = speed / direction.len();
        sprite.setCenterX(iniX + direction.x * distanceMultiplier * timer);
        sprite.setCenterY(iniY + direction.y * distanceMultiplier * timer);
    }

    public void updateOblique() {
        float delta = Gdx.graphics.getDeltaTime();
        timer += delta;
        float velocityX = initialVelocity * MathUtils.cos(initialAngle);
        float velocityY = initialVelocity * MathUtils.sin(initialAngle) - gravity * timer;

        Vector2 nextPoint = new Vector2(iniX + velocityX * timer, iniY + velocityY * timer);
        sprite.setCenter(nextPoint.x, nextPoint.y);
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        switch (movingType) {
            case Straight:
                updateStraight(delta);
                break;
            case Curve:
                throw new Error("Unimplemented.");
            case Roundabout:
                throw new Error("Unimplemented.");
            case Oblique:
                updateOblique();
                break;
        }
        updateHitbox();
    }

    public void draw() {
        float delta = Gdx.graphics.getDeltaTime();
        if (idleAnimationFrameNum == 0)
            sprite.draw(batch);
        else {
            stateTime += delta;
            TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);

            batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);

            if (idleAnimation.isAnimationFinished(stateTime))
                stateTime = 0;
        }
    }

    public float getDamage() {
        return damage;
    }

    public float getMaxBulletOnScreen() {
        return maxBulletOnScreen;
    }

    public float getFireRate() {
        return fireRate;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getRectangleHitbox() {
        return rectangleHitbox.getBox();
    }

    public Circle getCircleHitbox() {
        return circleHitbox;
    }

    public boolean isCircle() {
        return isCircle;

    }

    public void setCircle(boolean isCircle) {
        this.isCircle = isCircle;

    }

    public void setPosition(float x, float y) {
        sprite.setCenter(x, y);
    }

    public void setRotation(float angle) {
        sprite.setRotation(90 - angle * MathUtils.radiansToDegrees);
    }

    public Vector2 getCoordinate() {
        return new Vector2(sprite.getX() + width / 2, sprite.getY() + height / 2);
    }

    public void playAnimation() {}
}
