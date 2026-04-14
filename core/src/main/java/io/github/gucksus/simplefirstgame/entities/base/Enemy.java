package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.gucksus.simplefirstgame.entities.MainShip;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:</b> <i>health, hitboxOffsetX and hitboxOffsetY, hurtboxOffsetX and hurtboxOffsetY, shootPointOffsetX and shootPointOffsetY, hitbox, hurtbox, bulletTexture, animationIntervalTime, shootAnimationRepeat.</i>
 */
public abstract class Enemy {
    public float health;
    public Sprite sprite;
    public Rectangle hitbox;
    public Rectangle hurtbox;
    public float width;
    public float height;
    protected float hitboxOffsetX;
    protected float hitboxOffsetY;
    protected float shootPointOffsetX;
    protected float shootPointOffsetY;
    protected float hurtboxOffsetX;
    protected float hurtboxOffsetY;
    public boolean isDead = false;
    public boolean isMoving;
    public boolean isInvulnerable;
    public boolean isInvisible;
    public boolean previouslyInScreen;
    public boolean isHarmless;
    boolean shootInThisAnimation;
    public int numberOfTimeAllowedOnScreenLeft = 1;
    /**
     * The X difference/distance of each frame compared to the previous frame. So in each frame, this amount is added to make the enemy move. Thus, all enemies move at a constant speed.
     */
    public float nextFrameXDifference;
    /**
     * The Y difference/distance of each frame compared to the previous frame. So in each frame, this amount is added to make the enemy move. Thus, all enemies move at a constant speed.
     */
    public float nextFrameYDifference;
    movingType currentMovingType;
    enum movingType {Straight, Curve}
    protected Texture bulletTexture;

    protected Animation<TextureRegion> shootAnimation;
    public Animation<TextureRegion> deathAnimation;
    int shootAnimationFrameNum;
    /**
     * The number of time that the enemy is allowed to shoot.
     */
    protected int shootAnimationRepeat;
    /**
     * Timer for counting the interval between animation.
     */
    float animationIntervalTimer;
    protected float animationInterval;
    int deathAnimationFrameNum;
    public float stateTime;
    enum AnimationType {Static, Shoot, Death}
    AnimationType currentAnimationType = AnimationType.Static;

    public Enemy(TextureRegion staticTexture, float iniX, float iniY, float width, float height) {
        this.width = width;
        this.height = height;
        sprite = new Sprite(staticTexture);
        sprite.setSize(width, height);
        sprite.setPosition(iniX, iniY);
        currentMovingType = movingType.Straight;
    }

    public void initializeShootAnimation(TextureRegion[] shootAnimationFrames) {
        shootAnimation = new Animation<>(0.1f, shootAnimationFrames);
        this.shootAnimationFrameNum = shootAnimationFrames.length;
        stateTime = 0;
    }

    public void initializeDeathAnimation(TextureRegion[] deathAnimationFrames) {
        deathAnimation = new Animation<>(0.1f, deathAnimationFrames);
        this.deathAnimationFrameNum = deathAnimationFrames.length;
        stateTime = 0;
    }

    public void updatePosition(float delta) {
        if (!isMoving)
            return;
        switch (currentMovingType) {
            case Straight:
                moveStraight();
                break;
        }
    }

    public void moveStraight() {
        sprite.translate(nextFrameXDifference, nextFrameYDifference);
        updateEnemyHitboxAndHurtboxWhenMoved();
    }

    public void updateEnemyHitboxAndHurtboxWhenMoved() {
        hitbox.setPosition(sprite.getX() + hitboxOffsetX, sprite.getY() + hitboxOffsetY);
        hurtbox.setPosition(sprite.getX() + hurtboxOffsetX, sprite.getY() + hurtboxOffsetY);
    }

    /**
     * Method to check if the enemy is in the screen this frame or not.
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     * @return Whether the enemy is in the screen in this frame.
     */
    public boolean isInScreenThisFrame(float worldWidth, float worldHeight) {
        return (sprite.getX() > -width && sprite.getX() < worldWidth && sprite.getX() > -height && sprite.getY() < worldHeight);
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
        if (isInScreenThisFrame(worldWidth, worldHeight) && !previouslyInScreen) {
            numberOfTimeAllowedOnScreenLeft--;
            previouslyInScreen = true;
        } else if (!isInScreenThisFrame(worldWidth, worldHeight) && previouslyInScreen) {
            previouslyInScreen = false;
        }
        if (numberOfTimeAllowedOnScreenLeft == 0 && !isInScreenThisFrame(worldWidth, worldHeight) && !isInvulnerable) {
            isInvulnerable = true;
        }
    }

    public void triggerShootAnimation() {
        currentAnimationType = AnimationType.Shoot;
        stateTime = 0;
    }

    public void triggerDeathAnimation() {
        currentAnimationType = AnimationType.Death;
        stateTime = 0;
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
            float shootPointX = sprite.getX() + shootPointOffsetX;
            float shootPointY = sprite.getY() + shootPointOffsetY;
            float dx = mainShip.getShipHurtboxCenterX() - shootPointX;
            float dy = mainShip.getShipHurtboxCenterY() - shootPointY;
            return returnBulletType(shootPointX, shootPointY, dx, dy);
        }
        else return null;
    }
}
