package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.animation.AnimSpec;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.bullets.AquaShield;
import io.github.gucksus.simplefirstgame.entities.bullets.BasicBullet;
import io.github.gucksus.simplefirstgame.maths.AnimationTexture;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;

public class MainShip {
    Texture aquaShieldTexture;
    Array<Vector2> pastPositions = new Array<>();
    float powerDelay = .05f;

    Texture basicBulletIdleSheet;
    TextureRegion[] basicBulletIdleFrames;
    public Circle shipHurtbox;
    float hurtboxOffsetY;
    Sprite shipSprite;
    float width;
    float height;
    float shipSpeed = 6f;
    Bullet currentBullet;
    float timerSinceLastShot;
    public short lives = 1;
    public float timerSinceLastDamage;
    public float invulnerableDuration = 1f;
    public boolean isDead = false;
    Vector2 directionVector;
    Array<AnimationTexture> spinAnimations = new Array<>();
    TextureRegion[][] turnAnimations = new TextureRegion[3][2];
    Texture spinAnimationSheet;
    float stateTime;
    float worldWidth;
    float worldHeight;
    SpriteBatch batch;
    DebugRenderer debugRenderer;
    BulletHolder bulletHolder;
    int spinAnimIndex = 0;
    float timerSinceLastSpin = 67;
    float spinDuration;

    enum AnimationState {
        Neutral, TurningLeft, TurningRight, Spinning
    }

    AnimationState currentAnimationState = AnimationState.Neutral;

    float timerSinceKeyDown = 0;
    Vector2 lastDirectionVector = new Vector2();

    public MainShip(float centerX, float centerY, float width, float height,
            BulletHolder bulletHolder) {
        this.width = width;
        this.height = height;
        hurtboxOffsetY = height / 2.5f * 1.01f;
        timerSinceLastDamage = invulnerableDuration;
        basicBulletIdleSheet = new Texture("Bullet/basicBullet.png");
        spinAnimationSheet = new Texture("Mainship/ship_sprite_animation1.png");
        aquaShieldTexture = new Texture("Mainship/PowerUp/AquaShield.png");

        initializeAnimation();
        basicBulletIdleFrames = getBasicBulletIdleFrames();

        shipSprite.setSize(width, height);
        shipSprite.setCenter(centerX, centerY);
        shipHurtbox = new Circle(shipSprite.getX() + width / 2, centerY + hurtboxOffsetY, .1f);
        directionVector = new Vector2();
        worldWidth = Constants.worldWidth;
        worldHeight = Constants.worldHeight;
        batch = Constants.batch;
        debugRenderer = Constants.debugRenderer;
        currentBullet = new BasicBullet(basicBulletIdleFrames, 69, 69, 67, 67, batch);
        this.bulletHolder = bulletHolder;

        for (int i = 0; i < powerDelay / .016f; i++) {
            pastPositions.add(new Vector2(centerX, centerY));
        }

        activateAquaShield();
    }

    void activateAquaShield() {
        for (int i = 0; i < 3; i++) {
            AquaShield aquaShield =
                    new AquaShield(TextureRegion.split(aquaShieldTexture, 32, 32)[0], width, height,
                            67, 67, batch, this);
            bulletHolder.shipPower.add(aquaShield);
            bulletHolder.bulletTerminators.add(aquaShield);
        }

        Vector2 shipPoint = pastPositions.first();
        Vector2 basePoint = new Vector2(shipPoint.x + 1, shipPoint.y + 1);
        float radius = basePoint.sub(shipPoint).len();
        float baseAngle = basePoint.sub(shipPoint).angleRad();
        float angleIncrement = MathUtils.PI2 / bulletHolder.shipPower.size;

        for (int i = 0; i < bulletHolder.shipPower.size; i++) {
            float angle = baseAngle + angleIncrement * i;
            bulletHolder.shipPower.get(i).setPosition(shipPoint.x + radius * MathUtils.sin(angle),
                    shipPoint.y + radius * MathUtils.cos(angle));
            bulletHolder.shipPower.get(i).playAnimation();
        }
    }

    void setSpriteTexture(TextureRegion value) {
        shipSprite.setRegion(value);
    }

    void initializeAnimation() {
        TextureRegion[][] splitSpinAnimSheet = TextureRegion.split(spinAnimationSheet, 64, 64);

        AnimationTexture toBlueRed =
                new AnimationTexture(new Animation<>(0.1f, splitSpinAnimSheet[0]));
        AnimationTexture toRedWhite =
                new AnimationTexture(new Animation<>(.1f, splitSpinAnimSheet[1]));
        AnimationTexture toWhiteBlue =
                new AnimationTexture(new Animation<>(0.1f, splitSpinAnimSheet[2]));
        spinDuration = toBlueRed.getDuration();

        spinAnimations.add(toBlueRed);
        spinAnimations.add(toRedWhite);
        spinAnimations.add(toWhiteBlue);

        turnAnimations[0] = splitSpinAnimSheet[3];
        turnAnimations[1] = splitSpinAnimSheet[4];
        turnAnimations[2] = splitSpinAnimSheet[5];

        shipSprite = new Sprite(splitSpinAnimSheet[0][0]);
    }

    void triggerSpinAnim() {
        if (timerSinceLastSpin < spinDuration)
            return;
        bulletHolder.shipPower.clear();
        bulletHolder.bulletTerminators.clear();
        currentAnimationState = AnimationState.Spinning;
        Constants.textureAnimScheduler.play("Spin",
                new AnimSpec<>(spinAnimations.get(spinAnimIndex), (value, progress) -> {
                    this.setSpriteTexture(value);
                }, 0, spinDuration, 0, 0));
        timerSinceLastSpin = 0;
        spinAnimIndex = (spinAnimIndex + 1) % 3;
    }

    TextureRegion[] getBasicBulletIdleFrames() {
        return TextureRegion.split(basicBulletIdleSheet, 16, 16)[0];
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();

        pastPositions.removeIndex(0);
        pastPositions.add(getCoordinate().cpy());

        timerSinceLastShot += delta;
        timerSinceLastDamage += delta;
        timerSinceLastSpin += delta;
        input();
        animationStateUpdate();
        // Update hurtbox position for the ship.
        shipHurtbox.setPosition(shipSprite.getX() + width / 2, shipSprite.getY() + hurtboxOffsetY);
    }

    void returnAnimationToNeutral() {
        timerSinceKeyDown = 0;
        currentAnimationState = AnimationState.Neutral;
        shipSprite.setRegion(spinAnimations.get(spinAnimIndex).getFirstFrame());
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= 1;
            if (lastDirectionVector.x < 0)
                timerSinceKeyDown += delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += 1;
            if (lastDirectionVector.x > 0)
                timerSinceKeyDown += delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            dy -= 1;

        directionVector.set(dx, dy).nor();
        lastDirectionVector.set(directionVector);

        shipSprite.translateX(directionVector.x * shipSpeed * delta);
        shipSprite.translateY(directionVector.y * shipSpeed * delta);

        // Here I set so that the ship can go off-screen a quarter of its width.
        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth() / 4),
                worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite
                .setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));

        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            // If the amount of bullet on screen is smaller than max amount, then allow shooting.
            if (bulletHolder.shipBullets.size < currentBullet.getMaxBulletOnScreen()
                    && timerSinceLastShot >= currentBullet.getFireRate() && !isDead) {
                float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
                float iniY = shipSprite.getY() + shipSprite.getHeight() / 64 * 48;
                bulletHolder.shipBullets
                        .add(new BasicBullet(basicBulletIdleFrames, iniX, iniY, 0, 1, batch));
                timerSinceLastShot = 0;
            }
        }
    }

    void animationStateUpdate() {
        switch (currentAnimationState) {
            case Spinning:
                if (timerSinceLastSpin >= spinDuration) {
                    currentAnimationState = AnimationState.Neutral;
                    if (spinAnimIndex == 0) {
                        activateAquaShield();
                        break;
                    }
                }
                break;
            case TurningLeft:
                shipSprite.setRegion(turnAnimations[spinAnimIndex][0]);
                if (lastDirectionVector.x >= 0)
                    returnAnimationToNeutral();
                break;
            case TurningRight:
                shipSprite.setRegion(turnAnimations[spinAnimIndex][1]);
                if (lastDirectionVector.x <= 0)
                    returnAnimationToNeutral();
                break;
            case Neutral:
                if (Gdx.input.isKeyPressed(Input.Keys.L)) {
                    triggerSpinAnim();
                    break;
                }

                shipSprite.setRegion(spinAnimations.get(spinAnimIndex).getFirstFrame());
                if (timerSinceKeyDown >= .1f) {
                    if (lastDirectionVector.x < 0)
                        currentAnimationState = AnimationState.TurningLeft;
                    else
                        currentAnimationState = AnimationState.TurningRight;
                }
                break;
        }
    }

    public void takeDamage() {
        lives -= 1;
        if (lives == 0) {
            isDead = true;
        }
        timerSinceLastDamage = 0;
    }

    public void draw() {
        if (!isDead)
            shipSprite.draw(batch);
    }

    public void drawDebug() {
        debugRenderer.drawCircleHitbox(shipHurtbox);
    }

    public float getShipHurtboxCenterY() {
        return shipHurtbox.y + shipHurtbox.radius / 2;
    }

    public float getShipHurtboxCenterX() {
        return shipHurtbox.x + shipHurtbox.radius / 2;
    }

    public Vector2 getCoordinate() {
        return new Vector2(shipSprite.getX() + width / 2, shipSprite.getY() + height / 2);
    }

    public Array<Vector2> getPastPositions() {
        return pastPositions;
    }

    public void dispose() {
        basicBulletIdleSheet.dispose();
        spinAnimationSheet.dispose();
    }
}
