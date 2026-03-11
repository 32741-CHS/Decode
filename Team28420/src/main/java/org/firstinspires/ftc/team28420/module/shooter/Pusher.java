package org.firstinspires.ftc.team28420.module.shooter;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Pusher {
    public enum PusherState {
        INIT,
        PUSH,
        NEUTRAL
    }
    private final double INITPOS = 0.3;
    private final double PUSHPOS = 0.05;
    private final double NEUTRALPOS = 0.3;
    private final Servo pusher;
    private PusherState state = PusherState.INIT;

    public Pusher(HardwareMap hMap) {
        this.pusher = hMap.get(Servo.class, "pusher");
    }

    public void setState(PusherState state) {
        this.state = state;
        updatePosition();
    }

    private void updatePosition() {
        if(state == PusherState.PUSH) pusher.setPosition(PUSHPOS);
        else if(state == PusherState.NEUTRAL) pusher.setPosition(NEUTRALPOS);
        else pusher.setPosition(INITPOS);
    }
}
