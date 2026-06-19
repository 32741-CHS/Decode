package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Flywheel {
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_MOTOR_RPM = 6000;
    private static final double MAX_VELOCITY = (MAX_MOTOR_RPM / 60.0) * TICKS_PER_REV;
    private static final double[] VELOCITY_LEVELS = {
        0, 800, 1400, 2000, 2500, MAX_VELOCITY
    };
    private static final double READY_THRESHOLD = 0.92;

    public enum State { IDLE, SPOOLING, READY }

    private final DcMotorEx motor;
    private int level;
    private boolean running;

    public Flywheel(RobotHardware hw) {
        motor = (DcMotorEx) hw.flywheel;
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        level = 0;
        running = false;
    }

    public void setRunning(boolean on) {
        if (on == running) return;
        running = on;
        if (on) {
            if (level == 0) level = 1;
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(VELOCITY_LEVELS[level]);
        } else {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(0);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void speedUp() {
        if (level < VELOCITY_LEVELS.length - 1) {
            level++;
            if (running) motor.setVelocity(VELOCITY_LEVELS[level]);
        }
    }

    public void speedDown() {
        if (level > 0) {
            level--;
            if (running) motor.setVelocity(VELOCITY_LEVELS[level]);
        }
    }

    public int getLevel() {
        return level;
    }

    public double getTargetVelocity() {
        return running ? VELOCITY_LEVELS[level] : 0;
    }

    public double getCurrentVelocity() {
        return motor.getVelocity();
    }

    public State getState() {
        if (!running) return State.IDLE;
        double vel = motor.getVelocity();
        if (vel >= VELOCITY_LEVELS[level] * READY_THRESHOLD) return State.READY;
        return State.SPOOLING;
    }

    public String getSpeedLabel() {
        if (!running) return "OFF";
        return String.format("%d%%", (int)(VELOCITY_LEVELS[level] / MAX_VELOCITY * 100));
    }

    public void stop() {
        running = false;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
    }
}
