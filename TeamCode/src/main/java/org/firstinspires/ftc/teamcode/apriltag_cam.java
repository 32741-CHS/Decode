package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "April Tag Detection")
public class apriltag_cam extends OpMode {
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    @Override
    public void init() {
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void updateDetections() {
        detectedTags = aprilTagProcessor.getDetections();
    }

    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }

    public AprilTagDetection getTagBySpecificId(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                return detection;
            }
        }
        return null;
    }

    @Override
    public void loop() {
        updateDetections();

        telemetry.addData("April Tags Detected", detectedTags.size());

        if (detectedTags.size() > 0) {
            for (AprilTagDetection detection : detectedTags) {
                telemetry.addLine("---");
                telemetry.addData("Tag ID", detection.id);
                telemetry.addData("Range", "%.1f cm", detection.ftcPose.range);
                telemetry.addData("Bearing", "%.1f°", detection.ftcPose.bearing);
                telemetry.addData("Yaw", "%.1f°", detection.ftcPose.yaw);

                // Example usage of getTagBySpecificId
                if (detection.id == 1) {
                    AprilTagDetection specificTag = getTagBySpecificId(1);
                    if (specificTag != null) {
                        telemetry.addData("Specific Tag 1 Range", "%.1f cm", specificTag.ftcPose.range);
                    }
                }
            }
        } else {
            telemetry.addLine("No April Tags detected");
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}