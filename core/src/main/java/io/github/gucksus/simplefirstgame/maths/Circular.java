package io.github.gucksus.simplefirstgame.maths;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.animation.CallableMath;

public class Circular implements CallableMath<Vector2> {
    Vector2 startPoint;
    Vector2 center;
    float startAngle;
    float radius;
    int revolutionNum;

    final Vector2 output = new Vector2();

    public Circular(Vector2 startPoint, Vector2 center, int revolutionNum) {
        this.startPoint = startPoint;
        this.center = center;
        this.revolutionNum = revolutionNum;
        radius = Vector2.len(startPoint.x - center.x, startPoint.y - center.y);
        startAngle = Vector2.angleRad(startPoint.x - center.x, startPoint.y - center.y);
    }

    @Override
    public Vector2 get(float progress) {
        if (progress == 0 || progress == 1)
            return startPoint;
        float angle = (startAngle + progress * MathUtils.PI2 * revolutionNum) % MathUtils.PI2;
        return output.set(center.x + radius * MathUtils.cos(angle),
                center.y + radius * MathUtils.sin(angle));
    }
}
