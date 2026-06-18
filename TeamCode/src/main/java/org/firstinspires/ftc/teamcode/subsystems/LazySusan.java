package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

// lazy susan turret. friction wheel drive, 312rpm goBilda motor.
// 270 degree max rotation (±135 from center). NEVER go past this.
// encoder resets on init so center = 0 degrees.
public class LazySusan {

    private final RobotHardware hw;

    // 537.7 ticks per revolution at output shaft for 312rpm goBilda
    private static final double TICKS_PER_REV = 537.7;

    // max rotation in degrees from center (±135 = 270 total)
    private static final double MAX_DEGREES = 135.0;

    // P constant for auto-aim (tune at practice, start conservative)
    private static final double Kp = 0.01;

    // deadband in degrees — stop correcting if we're this close
    private static final double TARGET_TOLERANCE = 2.0;

    // max power for auto-aim so it doesn't go crazy
    private static final double MAX_AUTO_POWER = 0.5;

    private int encoderOffset = 0;

    public LazySusan(RobotHardware hw) {
        this.hw = hw;
        // reset encoder so center (facing forward) = 0
        hw.lazySusan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.lazySusan.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        encoderOffset = 0;
    }

    // manual control. power > 0 = right, power < 0 = left.
    // stops at 270 limit.
    public void setPower(double power) {
        double angle = getAngle();

        // slow down as we approach the limit so we don't slam into it
        if (power > 0 && angle >= MAX_DEGREES) {
            hw.lazySusan.setPower(0);
            return;
        }
        if (power < 0 && angle <= -MAX_DEGREES) {
            hw.lazySusan.setPower(0);
            return;
        }

        hw.lazySusan.setPower(power);
    }

    // auto-aim: P controller to turn to a target angle (in degrees).
    // pass in the absolute field angle you want the turret to face.
    public void setAngle(double targetAngle) {
        // clamp target to 270 limit
        targetAngle = Math.max(-MAX_DEGREES, Math.min(MAX_DEGREES, targetAngle));

        double currentAngle = getAngle();
        double error = targetAngle - currentAngle;

        // close enough, just stop
        if (Math.abs(error) < TARGET_TOLERANCE) {
            hw.lazySusan.setPower(0);
            return;
        }

        double power = error * Kp;

        // clamp power so it doesn't go too fast
        power = Math.max(-MAX_AUTO_POWER, Math.min(MAX_AUTO_POWER, power));

        hw.lazySusan.setPower(power);
    }

    // convert camera bearing to absolute turret angle.
    // camera is on the turret, so bearing = how far off-center the tag is.
    // current turret angle + bearing = where we need to point.
    public double bearingToTargetAngle(double bearingDegrees) {
        return getAngle() + bearingDegrees;
    }

    // get current angle in degrees from center
    public double getAngle() {
        int ticks = hw.lazySusan.getCurrentPosition() - encoderOffset;
        return (ticks / TICKS_PER_REV) * 360.0;
    }

    // check if we're within tolerance of target angle
    public boolean isAtTarget(double targetAngle, double toleranceDeg) {
        return Math.abs(getAngle() - targetAngle) < toleranceDeg;
    }

    public boolean isWithinLimit() {
        double angle = getAngle();
        return angle >= -MAX_DEGREES && angle <= MAX_DEGREES;
    }

    public double getMaxDegrees() {
        return MAX_DEGREES;
    }

    public void stop() {
        hw.lazySusan.setPower(0);
    }
}
