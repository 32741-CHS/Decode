package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.FlywheelConfig;
import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Flywheel {
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_MOTOR_RPM = 6000;
    private static final double MAX_VELOCITY = (MAX_MOTOR_RPM / 60.0) * TICKS_PER_REV;

    public enum State {
        IDLE, SPOOLING, READY
    }

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
            motor.setVelocity(levelVelocity(level));
        } else {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(0);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void speedUp() {
        if (level < 5) {
            level++;
            if (running) motor.setVelocity(levelVelocity(level));
        }
    }

    public void speedDown() {
        if (level > 0) {
            level--;
            if (running) motor.setVelocity(levelVelocity(level));
        }
    }

    public int getLevel() {
        return level;
    }

    public double getTargetVelocity() {
        return running ? levelVelocity(level) : 0;
    }

    public double getCurrentVelocity() {
        return motor.getVelocity();
    }

    public State getState() {
        if (!running) return State.IDLE;
        double vel = motor.getVelocity();
        if (vel >= levelVelocity(level) * FlywheelConfig.READY_THRESHOLD) return State.READY;
        return State.SPOOLING;
    }

    public String getSpeedLabel() {
        if (!running) return "OFF";
        return String.format("%d%%", (int)(levelVelocity(level) / MAX_VELOCITY * 100));
    }

    public void stop() {
        running = false;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
    }

    private static double levelVelocity(int lvl) {
        switch (lvl) {
            case 0: return FlywheelConfig.LEVEL_1;
            case 1: return FlywheelConfig.LEVEL_2;
            case 2: return FlywheelConfig.LEVEL_3;
            case 3: return FlywheelConfig.LEVEL_4;
            case 4: return FlywheelConfig.LEVEL_5;
            case 5: return FlywheelConfig.LEVEL_6;
            default: return 0;
        }
    }
}
