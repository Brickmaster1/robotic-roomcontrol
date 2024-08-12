package AIRobot.util;

import java.util.HashMap;

public class FloatPair {

    private float first;
    private float second;

    public FloatPair(float first, float second) {
        this.first = first;
        this.second = second;
    }

    public float getFirst() {
        return first;
    }

    public void setFirst(float first) {
        this.first = first;
    }

    public float getSecond() {
        return second;
    }

    public void setSecond(float second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "FloatPair{" + "first=" + first + ", second=" + second + '}';
    }
}

