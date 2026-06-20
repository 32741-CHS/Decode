package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Drivetrain {

    private static final double SPEED_SLOW   = 0.25;
    private static final double SPEED_NORMAL = 0.75;
    private static final double SPEED_TURBO  = 1.00;

    private static final double STICK_DEADBAND = 0.05;

    private final DcMotor flDrive, frDrive, blDrive, brDrive;
    private final IMU imu;

    private double speedMultiplier = SPEED_NORMAL;


    public Drivetrain(RobotHardware hw) {
        (flDrive = hw.flDrive).setDirection(DcMotorSimple.Direction.REVERSE);
        (blDrive = hw.blDrive).setDirection(DcMotorSimple.Direction.REVERSE);
        (frDrive = hw.frDrive).setDirection(DcMotorSimple.Direction.FORWARD);
        (brDrive = hw.brDrive).setDirection(DcMotorSimple.Direction.FORWARD);
        imu = hw.imu;

        for (DcMotor m : new DcMotor[]{flDrive, frDrive, blDrive, brDrive}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        resetIMU();
    }

    public void drive(double y, double x, double rx, boolean fieldRelative) {
        y  = deadband(y);
        x  = deadband(x) * 1.1;
        rx = deadband(rx);

        if (fieldRelative) {
            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double cos = Math.cos(-heading);
            double sin = Math.sin(-heading);
            double rotX = x * cos - y * sin;
            double rotY = x * sin + y * cos;
            x  = rotX;
            y  = rotY;
        }

        double fl = (y + x + rx);
        double fr = (y - x - rx);
        double bl = (y - x + rx);
        double br = (y + x - rx);

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                         Math.max(Math.abs(fr),
                         Math.max(Math.abs(bl), Math.abs(br)))));

        flDrive.setPower((fl / max) * speedMultiplier);
        frDrive.setPower((fr / max) * speedMultiplier);
        blDrive.setPower((bl / max) * speedMultiplier);
        brDrive.setPower((br / max) * speedMultiplier);
    }

    public void setSpeedMultiplier(boolean slow, boolean turbo) {
        if (slow) speedMultiplier = SPEED_SLOW;
        else if (turbo) speedMultiplier = SPEED_TURBO;
        else speedMultiplier = SPEED_NORMAL;
    }

    //public String getSpeedMultiplier() {
    //    if (speedMultiplier <= SPEED_SLOW)  return String.format("Slow (d%)", speedMultiplier);
    //    if (speedMultiplier >= SPEED_TURBO) return String.format("Turbo (d%)", speedMultiplier);
    //    return String.format("Normal (d%)", speedMultiplier);
    //}

    public void resetIMU() {
        imu.initialize(new IMU.Parameters( // TODO update these
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                )
        ));
    }

    private double deadband(double v) {
        return Math.abs(v) < STICK_DEADBAND ? 0 : v;
    }
}
