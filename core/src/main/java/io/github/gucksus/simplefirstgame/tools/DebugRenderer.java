package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class DebugRenderer {
    public ShapeRenderer shapeRenderer;
    public DebugRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public void drawHitbox(Rectangle hitbox) { // Draw hitboxes using a shape renderer.
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void drawHurtbox(Rectangle hurtbox) { // Draw hurtbox the same as drawAnimation hitbox.
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hurtbox.x, hurtbox.y, hurtbox.width, hurtbox.height);
    }

    public void drawCircleHitbox(Circle circleHitbox) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(circleHitbox.x, circleHitbox.y, circleHitbox.radius, 12);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
