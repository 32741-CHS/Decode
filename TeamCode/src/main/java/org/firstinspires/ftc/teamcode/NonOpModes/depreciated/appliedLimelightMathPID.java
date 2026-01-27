package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import org.firstinspires.ftc.teamcode.Util.AllianceDetector;


@Autonomous(name="LimeLightTesting", group="limelight")
//@Disabled
// the disabled will make it not show up under the driver station OPmode list
// useful to prevent cluttering after testing
public class appliedLimelightMathPID extends LinearOpMode {

    @Override
    public void runOpMode() {

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();


    }
}