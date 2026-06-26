package org.firstinspires.ftc.teamcode.subsystems;

public class Ballistics {
    public static double calculateTurretAngle(double tagBearingDeg, double currentAngleDeg) {
        return currentAngleDeg + tagBearingDeg;
    }
}
