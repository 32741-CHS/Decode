package org.firstinspires.ftc.teamcode.Util.Enum;

public enum DrumSlots {
    AllSlots(-1,-1,null),
    SLOT_0(0.27,0.1,Balls.unknown),
    SLOT_1(0.6,0.42,Balls.unknown),
    SLOT_2(0.92,0.76,Balls.unknown);

    public final double loadPosition;
    public final double shootPosition;

    private Balls loadedBall;

    public void autoDrumSlotInit(){
        SLOT_0.setLoadedBall(Balls.green);
        SLOT_1.setLoadedBall(Balls.purple);
        SLOT_2.setLoadedBall(Balls.purple);
    }

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
    public void setAllBall(Balls ballColor0,Balls ballColor1,Balls ballColor2) {
        SLOT_0.loadedBall = ballColor0;
        SLOT_1.loadedBall = ballColor1;
        SLOT_2.loadedBall = ballColor2;
    }

    public void setFromSlot(Balls ballcolor,int slot){
        switch(slot) {
            case 0 :
                SLOT_0.loadedBall = ballcolor;
                break;
            case 1 :
                SLOT_1.loadedBall = ballcolor;
                break;
            case 2 :
                SLOT_2.loadedBall = ballcolor;
                break;
        }
    }
    public boolean isEmpty() {
        return loadedBall == Balls.unknown;
    }

    public void clear() {
        loadedBall = Balls.unknown;
    }
}


