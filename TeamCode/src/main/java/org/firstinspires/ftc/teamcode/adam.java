package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="nate opMode")
public class  adam extends LinearOpMode {

    @Override
    public void runOpMode() {
        int shayshay = 9;
        int mochi = 3;
        int wise_tree = shayshay + mochi;

        telemetry.addData("number", wise_tree);
    }

}

