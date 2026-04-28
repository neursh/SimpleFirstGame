package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.waves.Wave;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:</b> <i>health, hitboxOffsetX and
 * hitboxOffsetY, hurtboxOffsetX and hurtboxOffsetY, shootPointOffsetX and shootPointOffsetY,
 * hitbox, hurtbox, bulletTexture, animationIntervalTime, shootAnimationRepeat.</i> <br>
 * <b><i>SET THESE VARIABLES AS 0 IF THE ENEMY DOES NOT HAVE SHOOT OR/AND DEATH ANIMATION:
 * shootAnimationFrameNum, deathAnimationFrameNum.</i></b>
 *
 * **YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:**
 *
 *
 */
public abstract class Enemy {
    protected float health;
    protected Sprite sprite;
    protected Vector2 textureSize;
    protected Vector2 pixelLength;
    protected float width;
    protected float height;

    protected Array<BoxWithOffset> hitboxes = new Array<>();
    protected Array<BoxWithOffset> hurtboxes = new Array<>();
    protected Array<Vector2> shootPointsOffsets = new Array<>();

    protected boolean isDead = false;
    public boolean isMoving;
    protected boolean isInvulnerable;
    protected boolean isInvisible;
    protected boolean isHarmless;
    protected boolean shootInThisAnimation;
    protected TextureRegion[] bulletIdleFrames;

    protected Array<Vector2> path = new Array<>();
    protected CatmullRomSpline<Vector2> catmullRomSpline;
    protected float moveDuration;
    protected float moveTimer;
    float nextFrameAngleDifference;
    float angle;
    protected movingType currentMovingType = movingType.Straight;

    protected enum movingType {
        Straight, Circle, Curve, Still
    }

    public Array<Timer.Task> tasks = new Array<>();

    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> shootAnimation;
    protected Animation<TextureRegion> deathAnimation;
    protected int idleAnimationFrameNum;
    protected int shootAnimationFrameNum;
    protected int deathAnimationFrameNum;
    protected int shootSpriteIndex;
    protected float idleFrameInterval = 0.1f;
    protected float shootFrameInterval = 0.1f;
    protected float deathFrameInterval = 0.1f;
    /**
     * The number of time that the enemy is allowed to shoot.
     */
    protected int shootAnimationRepeat;
    /**
     * Timer for counting the interval between animation.
     */
    protected float animationIntervalTimer;
    protected float animationInterval;
    protected float stateTime;

    protected enum AnimationType {
        Idle, Shoot, Death
    }

    protected AnimationType currentAnimationType = AnimationType.Idle;
    public SpriteBatch batch;
    protected DebugRenderer debugRenderer;
    MainShip mainShip;
    protected float worldWidth;
    protected float worldHeight;
    public Wave wave;

    Vector2 centerPoint;
    float radius;

    protected BulletHolder bulletHolder;

    public Enemy(TextureRegion staticTexture, float iniX, float iniY, float width, float height,
            MainShip mainShip, Wave wave) {
        this.width = width;
        this.height = height;
        textureSize = new Vector2(staticTexture.getRegionWidth(), staticTexture.getRegionHeight());
        pixelLength = new Vector2(width / textureSize.x, height / textureSize.y);
        sprite = new Sprite(staticTexture);
        sprite.setSize(width, height);
        sprite.setPosition(iniX - width / 2, iniY - height / 2);
        this.batch = Constants.batch;
        this.debugRenderer = Constants.debugRenderer;
        this.mainShip = mainShip;
        this.worldWidth = Constants.worldWidth;
        this.worldHeight = Constants.worldHeight;
        this.wave = wave;
        this.bulletHolder = wave.level.bulletHolder;
        path.add(new Vector2(iniX, iniY));
    }

    public void initializeIdleAnimation(TextureRegion[] idleAnimationFrames) {
        idleAnimation = new Animation<>(idleFrameInterval, idleAnimationFrames);
        idleAnimationFrameNum = idleAnimationFrames.length;
        stateTime = 0;
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

    public void update() {
        updateStatus();
        moveUpdate();
        updateEnemyHitboxAndHurtbox();
        hitboxCheck();
        hurtboxCheck();
    }

    void addTask(Timer.Task task) {
        Timer.schedule(task,
                wave.previousDuration + wave.waveEnemyArray.indexOf(this, true) * wave.interval);
        tasks.add(task);
    }

    protected void moveUpdate() {
        switch (currentMovingType) {
            case Straight:
                moveStraightUpdate();
                break;
            case Circle:
                moveCircleUpdate();
                break;
            case Curve:
                moveCurveUpdate();
                break;
            case Still:
                break;
        }
    }

    public void moveCircle(float duration) {
        Vector2 tempCenterPoint = wave.centerPoint;
        float tempRadius = wave.radius;
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                isMoving = true;
                moveTimer = 0;
                currentMovingType = movingType.Circle;
                moveDuration = duration;
                Vector2 thisToCenter = new Vector2(getCoordinate().x - tempCenterPoint.x,
                        getCoordinate().y - tempCenterPoint.y);
                angle = thisToCenter.angleRad();
                centerPoint = tempCenterPoint;
                radius = tempRadius;
            }
        };
        addTask(task);
    }

    public void moveStraight(float duration) {
        Array<Vector2> tempPath = new Array<>();
        for (Vector2 point : wave.path)
            tempPath.add(point.cpy());
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                isMoving = true;
                moveTimer = 0;
                currentMovingType = movingType.Straight;
                path = tempPath;
                moveDuration = duration;
            }
        };
        addTask(task);
    }

    public void moveCurve(float duration) {
        Vector2[] tempPath = new Vector2[wave.path.size];
        for (int i = 0; i < wave.path.size; i++)
            tempPath[i] = wave.path.get(i).cpy();
        Timer.Task task = new Timer.Task() {
            public void run() {
                isMoving = true;
                moveTimer = 0;
                currentMovingType = movingType.Curve;
                catmullRomSpline = new CatmullRomSpline<>(tempPath, true);
                moveDuration = duration;
            }
        };
        addTask(task);
    }

    public void moveCircleUpdate() {
        float delta = Gdx.graphics.getDeltaTime();
        nextFrameAngleDifference = wave.clockwiseMultiplier
                * (wave.revolutionNum * MathUtils.PI2 / moveDuration * delta);
        if (isMoving) {
            angle += nextFrameAngleDifference;
            Vector2 nextPoint = new Vector2();
            nextPoint.x = centerPoint.x + radius * MathUtils.cos(angle);
            nextPoint.y = centerPoint.y + radius * MathUtils.sin(angle);
            sprite.setCenter(nextPoint.x, nextPoint.y);
        }
    }

    public void moveStraightUpdate() {
        float delta = Gdx.graphics.getDeltaTime();
        moveTimer += delta;
        if (moveTimer >= moveDuration) {
            isMoving = false;
            return;
        }
        float nextX = path.first().x + (path.peek().x - path.first().x) / moveDuration * moveTimer;
        float nextY = path.first().y + (path.peek().y - path.first().y) / moveDuration * moveTimer;
        if (isMoving) {
            sprite.setCenter(nextX, nextY);
        }
    }

    void moveCurveUpdate() {
        float delta = Gdx.graphics.getDeltaTime();
        moveTimer += delta;
        if (moveTimer >= moveDuration) {
            isMoving = false;
            return;
        }
        Vector2 nextPoint = new Vector2();
        nextPoint = catmullRomSpline.valueAt(nextPoint, moveTimer / moveDuration);
        if (isMoving) {
            sprite.setCenter(nextPoint.x, nextPoint.y);
        }
    }

    void updateEnemyHitboxAndHurtbox() {
        for (BoxWithOffset hitbox : hitboxes) {
            hitbox.update(sprite.getX(), sprite.getY());
        }
        for (BoxWithOffset hurtbox : hurtboxes) {
            hurtbox.update(sprite.getX(), sprite.getY());
        }
    }

    public void updateStatus() {
        if (health <= 0 && currentAnimationType != AnimationType.Death) {
            isDead = true;
            isInvulnerable = true;
            isHarmless = true;
            cancelAllTasks();
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

    public void draw() {
        drawAnimation();
    }

    public void drawDebug() {
        drawHitbox();
    }

    public void drawAnimation() {
        float delta = Gdx.graphics.getDeltaTime();
        switch (currentAnimationType) {
            case Idle:
                if (idleAnimationFrameNum == 0)
                    sprite.draw(batch);
                else {
                    stateTime += delta;
                    TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);

                    batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);

                    if (idleAnimation.isAnimationFinished(stateTime))
                        stateTime = 0;
                }
                // If the number of times that the shoot animation needs to repeat is not 0 then it
                // needs to keep track of intervals.
                if (shootAnimationFrameNum != 0) {
                    if (shootAnimationRepeat != 0 && animationIntervalTimer < animationInterval) {
                        animationIntervalTimer += delta;
                    } else if (shootAnimationRepeat != 0
                            && animationIntervalTimer >= animationInterval) {
                        shootAnimationRepeat--;
                        animationIntervalTimer = 0;
                        shootInThisAnimation = false;
                        triggerShootAnimation();
                    }
                }
                break;
            case Shoot:
                if (shootAnimationFrameNum != 0) {
                    stateTime += delta;
                    animationIntervalTimer = 0;
                    TextureRegion currentFrame = shootAnimation.getKeyFrame(stateTime);

                    if (shootAnimation.getKeyFrameIndex(stateTime) == shootSpriteIndex)
                        shoot();

                    if (shootAnimation.isAnimationFinished(stateTime)) {
                        currentAnimationType = AnimationType.Idle;
                        stateTime = 0;
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

    void drawHitbox() {
        for (BoxWithOffset hitbox : hitboxes) {
            debugRenderer.drawHitbox(hitbox.getBox());
        }
        for (BoxWithOffset hurtbox : hurtboxes) {
            debugRenderer.drawHurtbox(hurtbox.getBox());
        }
    }

    public boolean isDeathAnimationFinished() {
        if (deathAnimationFrameNum == 0)
            return true;
        else
            return deathAnimation.isAnimationFinished(stateTime);
    }

    /**
     *
     * @param shootPointX The X coordinate of the shoot point.
     * @param shootPointY The Y coordinate of the shoot point.
     * @param dx The X direction of the vector.
     * @param dy The Y direction of the vector.
     * @return The bullet type of this enemy.
     */
    protected abstract Bullet returnBulletType(float shootPointX, float shootPointY, float dx,
            float dy);

    public void shoot() {
        if (!shootInThisAnimation && !isDead && shootAnimationFrameNum != 0) {
            for (Vector2 shootPointOffset : shootPointsOffsets) {
                shootInThisAnimation = true;
                float shootPointX = sprite.getX() + shootPointOffset.x;
                float shootPointY = sprite.getY() + shootPointOffset.y;
                float dx = mainShip.getShipHurtboxCenterX() - shootPointX;
                float dy = mainShip.getShipHurtboxCenterY() - shootPointY;

                bulletHolder.enemyBullets.add(returnBulletType(shootPointX, shootPointY, dx, dy));
            }
        }
    }

    void hitboxCheck() {
        for (BoxWithOffset hitbox : hitboxes) {
            if (Intersector.overlaps(mainShip.shipHurtbox, hitbox.getBox())
                    && mainShip.timerSinceLastDamage > mainShip.invulnerableDuration && !isHarmless)
                mainShip.takeDamage();
        }

        for (Bullet enemyBullet : bulletHolder.enemyBullets) {
            if (enemyBullet.getDamage() != 0) {
                if (enemyBullet.isCircle()) {
                    if (Intersector.overlaps(enemyBullet.getCircleHitbox(), mainShip.shipHurtbox)) {
                        mainShip.takeDamage();
                    }
                } else {
                    if (Intersector.overlaps(mainShip.shipHurtbox,
                            enemyBullet.getRectangleHitbox())) {
                        mainShip.takeDamage();
                    }
                }
            } else if (Intersector.overlaps(mainShip.shipHurtbox,
                    enemyBullet.getRectangleHitbox())) {
                bulletHolder.enemyBullets.removeValue(enemyBullet, true);
            }
        }
    }

    void hurtboxCheck() {
        for (BoxWithOffset hurtbox : hurtboxes) {
            for (int i = bulletHolder.shipBullets.size - 1; i >= 0; i--) {
                Bullet bullet = bulletHolder.shipBullets.get(i);
                if (Intersector.overlaps(bullet.rectangleHitbox.getBox(), hurtbox.getBox())
                        && !isInvulnerable) {
                    takeDamage(bullet.getDamage());
                    bulletHolder.shipBullets.removeIndex(i);
                }
            }
        }
    }

    protected void takeDamage(float damage) {
        health -= damage;
    }


    void cancelAllTasks() {
        for (Timer.Task task : tasks)
            if (task.isScheduled())
                task.cancel();
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

    public Vector2 getCoordinate() {
        return new Vector2(sprite.getX() + width / 2, sprite.getY() + height / 2);
    }
}
