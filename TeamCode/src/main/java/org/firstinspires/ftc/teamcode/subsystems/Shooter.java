package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Shooter {
    private final DcMotor flywheel, feeder;

    private static final double STEP_AMOUNT = 0.1;
    private static double targetFlywheelPower, targetFeederPower;
    private static double desiredFlywheelPower = 0.0;
    private static double desiredFeederPower;

    private static double FLYWHEEL_TO_FEEDER_DELAY = 1; // seconds
    private static ElapsedTime flywheelSpinTime = new ElapsedTime();

    private boolean isFlywheelSpinning;

    public Shooter(RobotHardware hw) {
        flywheel = hw.flywheel;
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        feeder = hw.feeder;
        feeder.setDirection(DcMotorSimple.Direction.FORWARD);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public double getFlywheelPower() {
        return flywheel.getPower();
    }

    public double getFeederPower() {
        return feeder.getPower();
    }

    public double getTimeTillFeeder() {
        return Math.min(FLYWHEEL_TO_FEEDER_DELAY - flywheelSpinTime.seconds(), 0.0);
    }

    public void speedUpFlywheel() {
        desiredFlywheelPower = Math.min(flywheel.getPower() + STEP_AMOUNT, 1.0);
    }
    public void slowDownFlywheel() {
        desiredFlywheelPower = Math.max(flywheel.getPower() - STEP_AMOUNT, 0.0);
    }

    public void shoot() {
        targetFlywheelPower = desiredFlywheelPower;
        targetFeederPower = desiredFeederPower;

        if (!isFlywheelSpinning) { flywheelSpinTime.reset(); }
    }

    public void update() {
        flywheel.setPower(targetFlywheelPower);
        isFlywheelSpinning = (targetFlywheelPower != 0);

        if (flywheelSpinTime.seconds() >= FLYWHEEL_TO_FEEDER_DELAY) {feeder.setPower(targetFeederPower);}

        targetFlywheelPower = 0; targetFeederPower = 0;
    }
}
