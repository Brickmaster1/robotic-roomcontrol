package AIRobot.util;

public class PowerSetting {

    private boolean AUTO;
    private boolean STOP;
    private boolean TRAINING;
    private double direction;
    private double autoDirection;

    public double getAutoDirection() {
        return autoDirection;
    }

    public void setAutoDirection(double autoDirection) {
        this.autoDirection = autoDirection;
    }

    public PowerSetting(boolean s, boolean a, boolean t) {
        STOP = s;
        AUTO = a;
        TRAINING = t;
    }

    public boolean isSTOP() {
        return STOP;
    }

    public void setSTOP(boolean STOP) {
        this.STOP = STOP;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public PowerSetting(){
        this.direction = 0;
        this.autoDirection = 0;
        this.STOP = true;
        this.AUTO = false;
        this.TRAINING = false;
    }

    public void setAUTO(boolean b) {
        this.AUTO = b;
    }

    public boolean isAUTO() {
        return AUTO;
    }

    public void setTRAINING(boolean b) { this.TRAINING = b; }
    public boolean isTRAINING() { return TRAINING; }
}
