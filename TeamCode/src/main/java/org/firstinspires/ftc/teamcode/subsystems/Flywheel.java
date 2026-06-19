package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Flywheel {
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_MOTOR_RPM = 6000;
    private static final double MAX_VELOCITY = (MAX_MOTOR_RPM / 60.0) * TICKS_PER_REV;
    private static final double POWER_DEADBAND = 0.02;

    public enum State { IDLE, SPOOLING, READY }

    private final DcMotorEx motor;
    private boolean running;
    private double targetPower;

    public Flywheel(RobotHardware hw) {
        motor = (DcMotorEx) hw.flywheel;
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        running = false;
        targetPower = 0;
    }

    public void setPower(double power) {
        targetPower = power;
        if (power > POWER_DEADBAND) {
            if (!running) {
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                running = true;
            }
            motor.setVelocity(power * MAX_VELOCITY);
        } else if (running) {
            running = false;
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(0);
        }
    }

    public double getPower() {
        return targetPower;
    }

    public boolean isRunning() {
        return running;
    }

    public double getTargetVelocity() {
        return running ? targetPower * MAX_VELOCITY : 0;
    }

    public double getCurrentVelocity() {
        return motor.getVelocity();
    }

    public State getState() {
        if (!running) return State.IDLE;
        double vel = motor.getVelocity();
        if (vel >= targetPower * MAX_VELOCITY * 0.92) return State.READY;
        return State.SPOOLING;
    }

    public String getSpeedLabel() {
        if (!running) return "OFF";
        return String.format("%d%%", (int)(targetPower * 100));
    }

    public void stop() {
        running = false;
        targetPower = 0;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
    }
}
