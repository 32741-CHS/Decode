package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;


// 20 (blue), 24 (red)
public class Vision {

    private AprilTagProcessor processor;
    private VisionPortal portal;

    public static final int BLUE_GOAL_TAG = 20;
    public static final int RED_GOAL_TAG = 24;

    public void init(HardwareMap hardwareMap) {
        processor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.METER, AngleUnit.RADIANS)
                .build();

        // idk change
        processor.setDecimation(2);

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(processor)
                .build();
    }

    // call this every loop to refresh detections
    public List<AprilTagDetection> getDetections() {
        return processor.getDetections();
    }

    // check if a specific goal tag is visible
    public boolean isTagVisible(int tagId) {
        return getTagById(tagId) != null;
    }

    // get bearing (in degrees) to a goal tag
    // returns 0 if tag not visible.
    public double getTagBearing(int tagId) {
        AprilTagDetection tag = getTagById(tagId);
        if (tag == null) return 0;
        return Math.toDegrees(tag.ftcPose.bearing);
    }

    public double getTagRange(int tagId) {
        AprilTagDetection tag = getTagById(tagId);
        if (tag == null) return -1;
        return tag.ftcPose.range;
    }

    public double getTagYaw(int tagId) {
        AprilTagDetection tag = getTagById(tagId);
        if (tag == null) return 0;
        return Math.toDegrees(tag.ftcPose.yaw);
    }

    // find a specific tag from current detections
    public AprilTagDetection getTagById(int tagId) {
        List<AprilTagDetection> detections = getDetections();
        for (AprilTagDetection detection : detections) {
            if (detection.id == tagId) {
                return detection;
            }
        }
        return null;
    }

    // get the goal tag ID based on alliance color
    // TODO: we need to have a way to set alliance (manual for now)
    public int getGoalTagId(boolean isRedAlliance) {
        return isRedAlliance ? RED_GOAL_TAG : BLUE_GOAL_TAG;
    }

    public void stop() {
        if (portal != null) {
            portal.close();
        }
    }
}
