package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;


@TeleOp(name = "Limelight Color Detection", group = "limelight")
//@Disabled
// the disabled will make it not show up under the driver station OPmode list
// useful to prevent cluttering after testing
public class LimeLightColorTesting extends LinearOpMode {

    @Override

    public void runOpMode() throws InterruptedException {
        // Get reference to Limelight3A device from hardware map
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();

        while (opModeIsActive()) {
            // Get the latest detection result
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                telemetry.addLine("Target found!");
                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
                for (LLResultTypes.FiducialResult tag : tags) {
                    int id = tag.getFiducialId();
                    Pose3D tagPose = tag.getRobotPoseTargetSpace();
                    telemetry.addData("Tag ID", id);
                }
            }
            else{
                telemetry.addLine("No target in view.");
            }

            telemetry.update();

            }

        limelight.stop(); //stops the limelight

        }
    }

