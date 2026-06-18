package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Turret {
    private final RobotHardware hw;

    // 537.7 ticks per revolution at output shaft for 312rpm goBilda
    private static final double TICKS_PER_REV = 537.7;

    private static final double MIN_ANGLE = -135.0;
    private static final double MAX_ANGLE = 135.0;

    private static double kP = 0.01;
    private static double kD = 0.000;
    private double lastError = 0;
    public static final double trackingTolerance = 0.2;
    public static final double MAX_POWER = 0.6;
    public static double targetAngle = 0.0;
    private final ElapsedTime timer = new ElapsedTime();

    private final double TURRET_TO_MOTOR_RATIO = (290 / (26.5 * 2 * Math.PI)); // squashed wheel radius
    //TODO double check that turret wheel circumference is correct

    public Turret(RobotHardware hw) {
        this.hw = hw;
        hw.turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        hw.turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public double getCurrentAngle() {
        int ticks = hw.turret.getCurrentPosition();
        return (ticks / TICKS_PER_REV) * TURRET_TO_MOTOR_RATIO * 360;
    }

    public boolean isAtTarget() {
        return Math.abs(getCurrentAngle() - targetAngle) < trackingTolerance;
    }
    public void resetTimer() {timer.reset();}

    public void slewTo(double desiredAngle) {
        targetAngle = desiredAngle;
    }

    public void update() {
        double deltaTime = timer.seconds();
        timer.reset();

        double clampedTargetHeading = Range.clip(targetAngle, MIN_ANGLE, MAX_ANGLE);

        double error = clampedTargetHeading - getCurrentAngle();
        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        double power;
        if (Math.abs(error) < trackingTolerance) {
            power = 0;
        } else {
            power = Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);
        }
        hw.turret.setPower(power);
        lastError = error;
    }
}
