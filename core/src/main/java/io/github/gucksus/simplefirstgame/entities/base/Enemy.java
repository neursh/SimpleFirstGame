package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:</b> <i>health, hitboxOffsetX and hitboxOffsetY, hurtboxOffsetX and hurtboxOffsetY, shootPointOffsetX and shootPointOffsetY, hitbox, hurtbox, bulletTexture, animationIntervalTime, shootAnimationRepeat.</i> <br>
 * <b><i>SET THESE VARIABLES AS 0 IF THE ENEMY DOES NOT HAVE SHOOT OR/AND DEATH ANIMATION: shootAnimationFrameNum, deathAnimationFrameNum.</i></b>
 */
public abstract class Enemy {
    protected float health;
    public Sprite sprite;
    protected Array<BoxWithOffset> hitboxes;
    protected Array<BoxWithOffset> hurtboxes;
    protected float width;
    protected float height;
    protected Array<Vector2> shootPointsOffsets;
    protected int textureSizeX;
    protected int textureSizeY;
    protected float pixelLengthX;
    protected float pixelLengthY;
    protected float shootFrameInterval = 0.1f;
    protected float deathFrameInterval = 0.1f;
    protected boolean isDead = false;
    public boolean isMoving;
    protected boolean isInvulnerable;
    protected boolean isInvisible;
    protected boolean isHarmless;
    protected boolean shootInThisAnimation;
    protected Texture bulletTexture;
    /**
     * The X difference/distance of each frame compared to the previous frame. So in each frame, this amount is added to make the enemy move. Thus, all enemies move at a constant speed.
     */
    public float nextFrameXDifference;
    /**
     * The Y difference/distance of each frame compared to the previous frame. So in each frame, this amount is added to make the enemy move. Thus, all enemies move at a constant speed.
     */
    public float nextFrameYDifference;
    public float nextFrameAngleDifference;
    public float angle;

    protected Animation<TextureRegion> shootAnimation;
    protected Animation<TextureRegion> deathAnimation;
    protected int shootAnimationFrameNum;
    /**
     * The number of time that the enemy is allowed to shoot.
     */
    protected int shootAnimationRepeat;
    /**
     * Timer for counting the interval between animation.
     */
    float animationIntervalTimer;
    protected float animationInterval;
    protected int deathAnimationFrameNum;
    protected float stateTime;
    enum AnimationType {Static, Shoot, Death}
    AnimationType currentAnimationType = AnimationType.Static;

    public Enemy(TextureRegion staticTexture, float iniX, float iniY, float width, float height) {
        this.width = width;
        this.height = height;
        textureSizeX = staticTexture.getRegionWidth();
        textureSizeY = staticTexture.getRegionHeight();
        pixelLengthX = width / textureSizeX;
        pixelLengthY = height / textureSizeY;
        sprite = new Sprite(staticTexture);
        sprite.setSize(width, height);
        sprite.setPosition(iniX, iniY);
        hitboxes = new Array<>();
        hurtboxes = new Array<>();
        shootPointsOffsets = new Array<>();
    }

    public void initializeShootAnimation(TextureRegion[] shootAnimationFrames) {
        shootAnimation = new Animation<>(shootFrameInterval, shootAnimationFrames);
        this.shootAnimationFrameNum = shootAnimationFrames.length;
        stateTime = 0;
    }

    public void initializeDeathAnimation(TextureRegion[] deathAnimationFrames) {
        deathAnimation = new Animation<>(deathFrameInterval, deathAnimationFrames);
        this.deathAnimationFrameNum = deathAnimationFrames.length;
        stateTime = 0;
    }

    public void moveStraight() {
        if (isMoving) {
            sprite.translate(nextFrameXDifference, nextFrameYDifference);
            updateEnemyHitboxAndHurtboxWhenMoved();
        }
    }

    public void moveCircle(Vector2 centerPoint , float radius) {
        if (isMoving) {
            angle += nextFrameAngleDifference;
            Vector2 nextPoint = new Vector2();
            nextPoint.x = centerPoint.x + radius * MathUtils.cos(angle);
            nextPoint.y = centerPoint.y + radius * MathUtils.sin(angle);
            sprite.setCenter(nextPoint.x, nextPoint.y);
            updateEnemyHitboxAndHurtboxWhenMoved();
        }
    }

    public void updateEnemyHitboxAndHurtboxWhenMoved() {
        for (BoxWithOffset hitbox: hitboxes) {
            hitbox.update(sprite.getX(), sprite.getY());
        }
        for (BoxWithOffset hurtbox: hurtboxes) {
            hurtbox.update(sprite.getX(), sprite.getY());
        }
    }

    /**
     * Method to check if the enemy is in the screen this frame or not.
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     * @return Whether the enemy is in the screen in this frame.
     */
    public boolean isInScreenThisFrame(float worldWidth, float worldHeight) {
        return (sprite.getX() > -width && sprite.getX() < worldWidth && sprite.getY() > -height && sprite.getY() < worldHeight);
    }

    /**
     * <b>THIS METHOD NEEDS TO BE RUN EVERY FRAME.</b><br>
     * This method checks number of things and then update the enemy status accordingly:
     * <ol>
     * <li>If the enemy's health is less than or equal to 0 and the death animation has not started yet.</li>
     * <li>If the enemy is in screen this frame and if the enemy is in screen the last frame.</li>
     * <li>If the number of time the enemy is allowed on screen is equal to 0.</li>
     * </ol>
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     */
    public void updateStatus(float worldWidth, float worldHeight) {
        if (health <= 0 && currentAnimationType != AnimationType.Death) {
            isDead = true;
            isInvulnerable = true;
            isHarmless = true;
            triggerDeathAnimation();
        }
    }

    public void triggerShootAnimation() {
        if (shootAnimationFrameNum != 0) {
            currentAnimationType = AnimationType.Shoot;
            stateTime = 0;
        }
    }

    public void triggerDeathAnimation() {
        if (deathAnimationFrameNum != 0) {
            currentAnimationType = AnimationType.Death;
            stateTime = 0;
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        switch (currentAnimationType) {
            case Static:
                sprite.draw(batch);
                // If the number of times that the shoot animation needs to repeat is not 0 then it needs to keep track of intervals.
                if (shootAnimationRepeat != 0 && animationIntervalTimer < animationInterval) {
                    animationIntervalTimer += delta;
                } else if (shootAnimationRepeat != 0 && animationIntervalTimer >= animationInterval) {
                    shootAnimationRepeat--;
                    animationIntervalTimer = 0;
                    shootInThisAnimation = false;
                    triggerShootAnimation();
                }
                break;
            case Shoot:
                if (shootAnimationFrameNum != 0) {
                    stateTime += delta;
                    animationIntervalTimer = 0;
                    TextureRegion currentFrame = shootAnimation.getKeyFrame(stateTime);
                    if (shootAnimation.isAnimationFinished(stateTime)) {
                        currentAnimationType = AnimationType.Static;
                    }
                    batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);
                }
                break;
            case Death:
                if (deathAnimationFrameNum != 0) {
                    stateTime += delta;
                    TextureRegion currentFrame = deathAnimation.getKeyFrame(stateTime);
                    batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);
                }
        }
    }

    public boolean isDeathAnimationFinished() {
        if (deathAnimationFrameNum == 0)
            return true;
        else
            return deathAnimation.isAnimationFinished(stateTime);
    }

    /**
     * @return Whether the shoot animation reach the frame where the enemy needs to shoot or not.
     */
    protected abstract boolean shootThisFrame();

    /**
     *
     * @param shootPointX The X coordinate of the shoot point.
     * @param shootPointY The Y coordinate of the shoot point.
     * @param dx The X direction of the vector.
     * @param dy The Y direction of the vector.
     * @return The bullet type of this enemy.
     */
    protected abstract EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy);

    /**
     *
     * @param mainShip The ship that needs to be shot.
     * @return A bullet if the conditions are met. Otherwise, it returns null.
     */
    public EnemyBullet shoot(MainShip mainShip) {
        if (shootThisFrame() && !shootInThisAnimation && !isDead && shootAnimationFrameNum != 0) {
            shootInThisAnimation = true;
            for (Vector2 shootPointOffset: shootPointsOffsets) {
                float shootPointX = sprite.getX() + shootPointOffset.x;
                float shootPointY = sprite.getY() + shootPointOffset.y;
                float dx = mainShip.getShipHurtboxCenterX() - shootPointX;
                float dy = mainShip.getShipHurtboxCenterY() - shootPointY;
                return returnBulletType(shootPointX, shootPointY, dx, dy);
            }
        }
        else return null;
        return null;
    }

    public boolean hitboxIntersectWithMainShip(MainShip mainShip) {
        for (BoxWithOffset hitbox: hitboxes) {
            if (Intersector.overlaps(mainShip.shipHurtbox, hitbox.getBox()))
                return true;
        }
        return false;
    }

    public boolean hurtboxIntersectWithThisBullet(Bullet bullet) {
        for (BoxWithOffset hurtbox: hurtboxes) {
            if (bullet.hitbox.overlaps(hurtbox.getBox()))
                return true;
        }
        return false;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getCenter() {
        return new Vector2(sprite.getX() + width / 2, sprite.getY() + height / 2);
    }
}
