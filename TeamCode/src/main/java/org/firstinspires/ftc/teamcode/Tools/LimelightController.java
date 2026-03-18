package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;

import java.util.ArrayList;
import java.util.List;

public class LimelightController {

    public enum Mode {
        TAG_MODE,
        COLOR_MODE
    }

    private final Limelight3A limelight;
    private final Telemetry screenLogger;

    // Tracking Constants - Tuned for stability
    private static final double K_FORWARD = 0.040;
    private static final double K_STRAFE = 0.045;
    private static final double K_TURN = 0.030;
    private static final double TARGET_DISTANCE_INCH = 25.0; 

    public LimelightController(HardwareMap hardwareMap, Telemetry telemetry) {
        screenLogger = telemetry;
        
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); 
        limelight.start();

        switchMode(Mode.TAG_MODE);
    }

    public void switchMode(Mode mode) {
        switch (mode) {
            case TAG_MODE:
                limelight.pipelineSwitch(0); 
                break;
            case COLOR_MODE:
                limelight.pipelineSwitch(1); 
                break;
        }
    }

    /**
     * Returns a string of all currently visible AprilTag IDs.
     */
    public String getVisibleIDs() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials.isEmpty()) return "None";
            
            List<Integer> ids = new ArrayList<>();
            for (LLResultTypes.FiducialResult fr : fiducials) {
                ids.add(fr.getFiducialId());
            }
            return ids.toString();
        }
        return "No Result";
    }

    /**
     * Track a specific AprilTag ID and maintain target distance.
     */
    public boolean track(int targetId, MecanumDrive drive) {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            for (LLResultTypes.FiducialResult fr : fiducials) {
                if (fr.getFiducialId() == targetId) {
                    Pose3D pose = fr.getRobotPoseTargetSpace();
                    Position pos = pose.getPosition().toUnit(DistanceUnit.INCH);
                    YawPitchRollAngles angles = pose.getOrientation();

                    // Error calculation
                    double depthError = pos.z - TARGET_DISTANCE_INCH;
                    double lateralError = pos.x; 
                    
                    // Normalize yaw error (target space yaw is ~180 when facing tag)
                    double yawError = angles.getYaw(AngleUnit.DEGREES);
                    double relativeYaw = yawError - 180;
                    while (relativeYaw > 180) relativeYaw -= 360;
                    while (relativeYaw <= -180) relativeYaw += 360;

                    // P-control calculation
                    // Forward: Positive power drives robot forward
                    double forwardPower = Range.clip(depthError * K_FORWARD, -0.6, 0.6);
                    
                    // Strafe: If robot is to the right (pos.x > 0), we want positive strafe to move left.
                    // (Matching your manual drive logic where positive is left)
                    double strafePower = Range.clip(lateralError * K_STRAFE, -0.5, 0.5);
                    
                    // Turn: If robot is CCW of target (relativeYaw > 0), we want positive turn (CW).
                    // (Matching your manual drive logic where positive is CW)
                    double turnPower = Range.clip(relativeYaw * K_TURN, -0.4, 0.4);

                    // Execute drive commands
                    drive.driveRobotCentric(strafePower, forwardPower, turnPower);
                    
                    updateTrackingTelemetry(targetId, depthError, lateralError, relativeYaw);
                    return true;
                }
            }
        }
        return false;
    }

    private void updateTrackingTelemetry(int id, double distErr, double latErr, double yawErr) {
        screenLogger.addData("LL Tracking", "Target ID: %d", id);
        screenLogger.addData("LL Error", "Dist: %.1f\", Lat: %.1f\", Yaw: %.1f deg", distErr, latErr, yawErr);
    }

    public void stop() {
        limelight.stop();
    }
}
