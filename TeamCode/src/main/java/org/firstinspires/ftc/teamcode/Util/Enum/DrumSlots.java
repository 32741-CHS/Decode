package org.firstinspires.ftc.teamcode.Util.Enum;

public enum DrumSlots {
    SLOT_0(0.27,0.1,Balls.unknown),
    SLOT_1(0.6,0.42,Balls.unknown),
    SLOT_2(0.92,0.76,Balls.unknown);

    public final double loadPosition;
    public final double shootPosition;

    private Balls loadedBall;


    DrumSlots(double loadPosition, double shootPosition, Balls initialBall) {
        this.loadPosition = loadPosition;
        this.shootPosition = shootPosition;
        this.loadedBall = initialBall;
        }
    public Balls getLoadedBall() {
        return loadedBall;
    }

    public void setLoadedBall(Balls ballColor) {
        this.loadedBall = ballColor;
    }
    public boolean isEmpty() {
        return loadedBall == Balls.unknown;
    }

    public void clear() {
        loadedBall = Balls.unknown;
    }
}


