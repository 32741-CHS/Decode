package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Flywheel {
    public enum State { IDLE, SPOOLING, READY }

    private static final double STEP_AMOUNT = 0.1;
    private static double targetPower;

    private final DcMotor flywheel;
    private boolean canSpin;

    public Flywheel(RobotHardware hw) {
        flywheel = hw.flywheel;
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        canSpin = false;
    }

    public void toggleCanSpin() {
        canSpin = !canSpin;
    }

    public double getFlywheelPower() {
        return flywheel.getPower();
    }

    public void speedUp() {
        targetPower = Math.min(flywheel.getPower() + STEP_AMOUNT, 1.0);
    }

    public void slowDown() {
        targetPower = Math.max(flywheel.getPower() - STEP_AMOUNT, 0.0);
    }

    public void stop() {
        canSpin = false;
    }

    public void update() {
        flywheel.setPower(targetPower);
    }
}
