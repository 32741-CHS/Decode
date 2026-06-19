package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Flywheel {
    private static final double[] SPEED_LEVELS = {
        0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
    };

    public enum State { IDLE, SPOOLING, READY }

    private final DcMotor motor;
    private int speedLevel;
    private boolean running;

    public Flywheel(RobotHardware hw) {
        motor = hw.flywheel;
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        speedLevel = 0;
        running = false;
    }

    public void setRunning(boolean on) {
        if (on) {
            if (speedLevel == 0) speedLevel = 1;
            running = true;
            motor.setPower(SPEED_LEVELS[speedLevel]);
        } else {
            running = false;
            motor.setPower(0);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void speedUp() {
        if (speedLevel < SPEED_LEVELS.length - 1) {
            speedLevel++;
            if (running) {
                motor.setPower(SPEED_LEVELS[speedLevel]);
            }
        }
    }

    public void speedDown() {
        if (speedLevel > 0) {
            speedLevel--;
            if (running) {
                motor.setPower(SPEED_LEVELS[speedLevel]);
            }
        }
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public State getState() {
        return running ? State.READY : State.IDLE;
    }

    public String getSpeedLabel() {
        if (!running) return "OFF";
        return String.format("%d%%", (int)(SPEED_LEVELS[speedLevel] * 100));
    }

    public void stop() {
        running = false;
        speedLevel = 0;
        motor.setPower(0);
    }
}
