package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

// shooting math and lookup table.
// distance (meters) -> flywheel RPS for the shooter.
// TODO: once pedro pathing is added, also compute distance from
// follower.getPose() as fallback when april tags are not visible
@Configurable
public class Ballistics {

    // lookup table: distance (meters) and flywheel RPS.
    // these can be tuned live with ftc dashboard or panels.
    // fill them in during practice by shooting from measured distances.
    public static double[] shootDistances = {1.0, 2.0, 3.0, 4.0, 5.0};
    public static double[] shootRPS =        {20,   28,   36,   44,   52};

    // get the desired flywheel speed for a given distance.
    // uses linear interpolation between table entries.
    public static double getFlywheelRPS(double distanceMeters) {
        if (distanceMeters < 0) return shootRPS[0];
        if (distanceMeters <= shootDistances[0])
            return shootRPS[0];
        if (distanceMeters >= shootDistances[shootDistances.length - 1])
            return shootRPS[shootRPS.length - 1];

        for (int i = 0; i < shootDistances.length - 1; i++) {
            double d0 = shootDistances[i];
            double d1 = shootDistances[i + 1];
            if (distanceMeters >= d0 && distanceMeters <= d1) {
                double r0 = shootRPS[i];
                double r1 = shootRPS[i + 1];
                double t = (distanceMeters - d0) / (d1 - d0);
                return r0 + t * (r1 - r0);
            }
        }
        return shootRPS[shootRPS.length - 1];
    }

    // calculate turret angle from april tag bearing.
    // bearing is how far off-center the tag is (degrees).
    // we add it to the current angle to get the target.
    // positive bearing = tag is to the right = turret turns right.
    public static double calculateTurretAngle(double tagBearingDeg, double currentAngleDeg) {
        return currentAngleDeg + tagBearingDeg;
    }
}
