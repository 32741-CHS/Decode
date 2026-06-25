package org.firstinspires.ftc.teamcode.subsystems;

public class Ballistics {
    public static double calculateTurretAngle(double yaw, double currentAngle) {
        return yaw + currentAngle;
    }
}
