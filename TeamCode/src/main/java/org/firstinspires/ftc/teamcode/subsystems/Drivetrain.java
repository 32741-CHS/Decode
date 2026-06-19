package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

// mecanum drive, controller 2 only.
// LB = turbo, RB = slow, neither = normal
public class Drivetrain {

    private static final double SPEED_SLOW   = 0.25;
    private static final double SPEED_NORMAL = 0.75;
    private static final double SPEED_TURBO  = 1.00;
    private static final double STICK_DEADBAND = 0.05;

    private final RobotHardware hw;
    private double speedMultiplier = SPEED_NORMAL;

    public Drivetrain(RobotHardware hw) {

        this.hw = hw;
    }

    // mecanum drive. pass in stick values and whether to use field relative mode
    public void drive(double y, double x, double rx, boolean fieldRelative) {
        y  = deadband(y);
        x  = deadband(x) * 1.1; // idk everyone does this
        rx = deadband(rx);

        if (fieldRelative) {
            // rotate inputs by heading so forward is always "away from driver"
            double heading = hw.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double cos = Math.cos(-heading);
            double sin = Math.sin(-heading);
            double rotX = x * cos - y * sin;
            double rotY = x * sin + y * cos;
            x  = rotX;
            y  = rotY;
        }

        // mecanum wheel power equations
        double fl = (y + x + rx);
        double fr = (y - x - rx);
        double bl = (y - x + rx);
        double br = (y + x - rx);

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                         Math.max(Math.abs(fr),
                         Math.max(Math.abs(bl), Math.abs(br)))));

        hw.frontLeft.setPower((fl / max) * speedMultiplier);
        hw.frontRight.setPower((fr / max) * speedMultiplier);
        hw.backLeft.setPower((bl / max) * speedMultiplier);
        hw.backRight.setPower((br / max) * speedMultiplier);
    }

    // call this every loop before drive() to set speed
    public void setSpeedMode(boolean slow, boolean turbo) {
        if (slow) {
            speedMultiplier = SPEED_SLOW;
        } else if (turbo) {
            speedMultiplier = SPEED_TURBO;
        } else {
            speedMultiplier = SPEED_NORMAL;
        }
    }

    public double getSpeedMultiplier() {

        return speedMultiplier;
    }

    public String getSpeedLabel() {
        if (speedMultiplier <= SPEED_SLOW)  return "Slow (25%)";
        if (speedMultiplier >= SPEED_TURBO) return "Turbo (100%)";
        return "Normal (75%)";
    }

    public void stop() {
        hw.frontLeft.setPower(0);
        hw.frontRight.setPower(0);
        hw.backLeft.setPower(0);
        hw.backRight.setPower(0);
    }

    private double deadband(double v) {

        return Math.abs(v) < STICK_DEADBAND ? 0 : v;
    }
}
