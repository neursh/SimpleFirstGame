package io.github.gucksus.simplefirstgame.maths;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.animation.CallableMath;

public class PathLerp implements CallableMath<Vector2> {
    private final Array<Vector2> path = new Array<>();
    private final Array<Float> segmentLengths = new Array<>();
    private final Array<Float> cumulativeDistances = new Array<>();
    private final float totalLength;

    private final Vector2 output = new Vector2();

    public PathLerp(Array<Vector2> points) {
        for (Vector2 point : points) {
            this.path.add(point.cpy());
        }

        float total = 0;
        for (int i = 0; i < this.path.size - 1; i++) {
            float len = (float) Math.hypot(this.path.get(i + 1).x - this.path.get(i).x,
                    this.path.get(i + 1).y - this.path.get(i).y);
            segmentLengths.add(len);
            total += len;
            cumulativeDistances.add(total);
        }
        this.totalLength = total;
    }

    @Override
    public Vector2 get(float progress) {
        if (progress <= 0)
            return this.path.first();
        if (progress >= 1)
            return this.path.get(this.path.size - 1);

        float target = progress * totalLength;
        int segmentIndex = findSegment(target);

        if (segmentIndex == -1)
            return this.path.get(this.path.size - 1);

        float cumulativeDistance =
                (segmentIndex == 0) ? 0.0f : cumulativeDistances.get(segmentIndex - 1);

        float localT = (target - cumulativeDistance) / this.segmentLengths.get(segmentIndex);

        Vector2 a = this.path.get(segmentIndex);
        Vector2 b = this.path.get(segmentIndex + 1);

        this.output.set(a.x * (1.0f - localT) + b.x * localT, a.y * (1.0f - localT) + b.y * localT);

        return this.output;
    }

    private int findSegment(float targetDistance) {
        int low = 0;
        int high = cumulativeDistances.size - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            float cumulativeValue = cumulativeDistances.get(mid);

            if (cumulativeValue >= targetDistance) {
                result = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return result;
    }

}
