package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.Bulletlv1;
import io.github.gucksus.simplefirstgame.entities.Enemylv1;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    // Declare variables.
    // Different background textures.
    Texture backgroundTextureNo0;
    Texture backgroundTextureNo1;
    Texture backgroundTextureNo2;
    // Entities textures.
    Texture shipTexture;
    Texture bulletlv1Texture;
    // Sprites.
    private SpriteBatch batch;
    Sprite shipSprite;
    Sprite[] backgroundSprites;
    FitViewport viewport;
    Array <Bulletlv1> bulletlv1Array;
    // This timer accounts for how long since last bullet.
    float bulletTimer = 1f;
    // How fast the spawn rate of the bullets.
    float fireRate = .2f;
    // Background scrolling speed.
    float backgroundSpeed = 3f;
    Enemylv1 loneEnemylv1;
    ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        // Adds texture.
        // Background has 3 sprites for scrolling effect.
        backgroundSprites = new Sprite[3];
        backgroundTextureNo0 = new Texture("background1.png");
        backgroundTextureNo1 = new Texture("background2.png");
        backgroundTextureNo2 = new Texture("background3.png");
        shipTexture = new Texture("ShipSprite.png");
        bulletlv1Texture = new Texture("bullet_texture.png");

        // Initialize sprites.
        shipSprite = new Sprite(shipTexture);
        shipSprite.setSize(1, 1);
        shipSprite.setCenterX(4);
        backgroundSprites[0] = new Sprite(backgroundTextureNo0);
        backgroundSprites[1] = new Sprite(backgroundTextureNo1);
        backgroundSprites[2] = new Sprite(backgroundTextureNo2);
        // Height is 22 because the background is supposed to be twice the size to create infinity effect.
        backgroundSprites[0].setSize(8, 11);
        backgroundSprites[1].setSize(8, 11);
        backgroundSprites[2].setSize(8, 11);

        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        viewport = new FitViewport(8,11);
        bulletlv1Array = new Array<>();
        loneEnemylv1 = new Enemylv1(4f, 10f);
        backgroundSprites[0].setY(viewport.getWorldHeight());
        backgroundSprites[2].setY(-viewport.getWorldHeight());
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // In case delta jump too high.
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1/55f);
        input();
        backgroundUpdate(delta);
        clampLogic();
        bulletLogic(delta);
        enemyLv1Logic(delta);
        draw();
    }

    private void input() { // Inputs for the ship.
        float speed = 6f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            shipSprite.translateX(-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            shipSprite.translateX(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            shipSprite.translateY(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            shipSprite.translateY(-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            bulletSpawn();
        }
    }

    private void backgroundUpdate(float delta) {

        for (Sprite background : backgroundSprites) {
            background.translateY(-delta * backgroundSpeed);
        }

        /*How does this work:
        * As a background sprite go entirely below the screen, it looks for the highest sprite and then put the off-screen sprite
        * on top.*/
        float highestY = backgroundSprites[0].getY();
        int highestIdx = 0;
        int outOfScreenIdx = -1;
        for (int i = 0; i < 3; i++) { // The for is for both looking for the off-screen and the highest sprite.
            if (backgroundSprites[i].getY() > highestY) {
                highestY = backgroundSprites[i].getY();
                highestIdx = i;
            }
            if (backgroundSprites[i].getY() + backgroundSprites[i].getHeight() < 0)
                outOfScreenIdx = i;
        }
        if (outOfScreenIdx != -1) // If there is a sprite that off-screen.
            backgroundSprites[outOfScreenIdx].setY(highestY + backgroundSprites[highestIdx].getHeight());
    }

    private void bulletSpawn() {
        if (bulletTimer >= fireRate) { // If the timer exceeds the time interval, it will spawn a bullet and resets back to 0.
            float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
            float iniY = shipSprite.getY() + shipSprite.getHeight();
            bulletlv1Array.add(new Bulletlv1(bulletlv1Texture, iniX, iniY));
            bulletTimer = 0;
        }
    }

    private void bulletLogic(float delta) { // Update position for all the bullets.
        bulletTimer += delta;

        for (Bulletlv1 bullet: bulletlv1Array) {
            bullet.update(delta);
        }
    }

    private void enemyLv1Logic(float delta) {
        loneEnemylv1.moveWeirdly(delta);
    }

    private void clampLogic() { // Clamp logic for the ship.
        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();
        // Here I set so that the ship can go off-screen a quarter of its width.
        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth()/4), worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite.setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));
    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw background and ship.
        for (Sprite background: backgroundSprites) {
            background.draw(batch);
        }

        shipSprite.draw(batch);
        loneEnemylv1.selfSprite.draw(batch);

        for (Bulletlv1 bulletlv1: bulletlv1Array) {
            bulletlv1.bulletSelfSprite.draw(batch);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(loneEnemylv1.hitbox.x, loneEnemylv1.hitbox.y, loneEnemylv1.hitbox.width, loneEnemylv1.hitbox.height);

        shapeRenderer.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
