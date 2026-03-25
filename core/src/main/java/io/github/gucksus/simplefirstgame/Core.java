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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.BasicBullet;
import io.github.gucksus.simplefirstgame.entities.Bullet;
import io.github.gucksus.simplefirstgame.entities.Enemy;
import io.github.gucksus.simplefirstgame.entities.PopcornEnemy;
import io.github.gucksus.simplefirstgame.levels.Level;
import io.github.gucksus.simplefirstgame.levels.Level1;
import io.github.gucksus.simplefirstgame.tools.ScrollingBackground;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    // Declare textures.
    Texture shipTexture;
    Texture basicBulletTexture;
    Rectangle shipHurtbox;
    // Sprites.
    private SpriteBatch batch;
    Sprite shipSprite;
    FitViewport viewport;
    Array <Bullet> bulletArray;
    // This timer accounts for how long since last bullet.
    float bulletTimer = .2f;
    // How fast the spawn rate of the bullets.
    float fireRate = .2f;
    ScrollingBackground scrollingBackground;
    // Levels.
    Level currentLevel;
    Level1 level1;
    // This is for drawing hitbox and hurtbox.
    ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        // Adds texture.
        shipTexture = new Texture("ShipSprite.png");
        basicBulletTexture = new Texture("bullet_texture.png");

        // Initialize sprites.
        shipSprite = new Sprite(shipTexture);
        shipHurtbox = new Rectangle(4, 0, 1, 1);
        shipSprite.setSize(1, 1);
        shipSprite.setCenterX(4);

        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(8,11);
        bulletArray = new Array<>();
        scrollingBackground = new ScrollingBackground(viewport.getWorldHeight());
        // Level can be changed by changing currentLevel to desired level.
        level1 = new Level1();
        currentLevel = level1;
        currentLevel.enemySpawn();
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
        clampLogic();
        scrollingBackground.backgroundUpdate(delta);
        bulletUpdate(delta);
        // Update hurtbox position for the ship.
        shipHurtbox.setPosition(shipSprite.getX(), shipSprite.getY());
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
            if (bulletTimer >= fireRate) { // If the timer exceeds the time interval, it will spawn a bullet and resets back to 0.
                float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
                float iniY = shipSprite.getY() + shipSprite.getHeight();
                bulletArray.add(new BasicBullet(basicBulletTexture , iniX, iniY));
                bulletTimer = 0;
            }
        }
    }

    private void bulletUpdate(float delta) { // Update position for bullets.
        bulletTimer += delta;
        for (Bullet bullet: bulletArray) {
            bullet.update(delta);
        }
    }

    private void clampLogic() { // Clamp logic for the ship.
        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();
        // Here I set so that the ship can go off-screen a quarter of its width.
        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth()/4), worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite.setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));
    }

    private void drawHitbox(Rectangle hitbox) { // Draw hitboxes using a shape renderer.
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    private void drawHurtbox(Rectangle hurtbox) { // Draw hurtbox the same as draw hitbox.
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hurtbox.x, hurtbox.y, hurtbox.width, hurtbox.height);
    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        scrollingBackground.draw(batch);
        shipSprite.draw(batch);
        currentLevel.draw(batch);
        for (Bullet basicBullet : bulletArray) {
            basicBullet.sprite.draw(batch);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Bullet bullet: bulletArray){
            drawHitbox(bullet.hitbox);
        }
        drawHurtbox(shipHurtbox);
        for (Enemy enemy: level1.enemyArray){
            drawHitbox(enemy.hitbox);
            drawHurtbox(enemy.hurtbox);
        }

        shapeRenderer.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
