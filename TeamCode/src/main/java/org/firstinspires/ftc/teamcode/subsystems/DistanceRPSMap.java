package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

@Configurable
public class DistanceRPSMap {

    // distance in meters vs flywheel RPS
    public static double[] distances = {0.5, 1.0, 1.5, 2.0, 2.5};
    public static double[] rpsValues = {0, 0, 0, 0, 0};

    public static double getRPS(double distance) {
        if (distances == null || rpsValues == null || distances.length == 0) {
            return 0;
        }

        if (distance <= distances[0]) {
            return rpsValues[0];
        }

        if (distance >= distances[distances.length - 1]) {
            return rpsValues[rpsValues.length - 1];
        }

        for (int i = 0; i < distances.length - 1; i++) {
            if (distance >= distances[i] && distance < distances[i + 1]) {
                double t = (distance - distances[i]) / (distances[i + 1] - distances[i]);
                return rpsValues[i] + t * (rpsValues[i + 1] - rpsValues[i]);
            }
        }

        return rpsValues[rpsValues.length - 1];
    }
}
