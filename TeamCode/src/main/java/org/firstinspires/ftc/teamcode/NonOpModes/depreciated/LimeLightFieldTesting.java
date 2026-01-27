package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import java.util.List;

@Autonomous(name="LimeLightFieldTesting", group="limelight")


public class LimeLightFieldTesting extends LinearOpMode {

    @Override

    public void runOpMode() {

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();

        while (opModeIsActive()) { // keeps the code running so it doesn't only run once

            LLResult result = limelight.getLatestResult(); 

            if (result != null && result.isValid()){ // checks if there is a target and if the target is an actual target

                
                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults(); //get fiducial results basically just tells how many april tags it sees
                //List<LLResultTypes.FiducialResult>: so it makes a list at the size of the # of tags detected and has info on the id and position of the tag

                for (LLResultTypes.FiducialResult tag : tags) {
                    int id = tag.getFiducialId();
                    if (id == 20 || id == 24){
                        Pose3D robotpose = tag.getRobotPoseFieldSpace();
                        if (robotpose != null) {
                            double x = robotpose.getPosition().x;
                            double y = robotpose.getPosition().y;
                            telemetry.addData("bot Location", "(" + x + ", " + y + ")");
                        }
                    }
                }
            }
            else {
                telemetry.addLine("no robotlocation update");
            }

            telemetry.update();
        }

        limelight.stop(); //stops the limelight

    } 
}

