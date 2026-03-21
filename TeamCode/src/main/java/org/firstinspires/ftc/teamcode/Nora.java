package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Nora TeleOp")
public class Nora extends LinearOpMode {
    @Override
    public void runOpMode() {
        int nora = 9;
        int shay = 12;
        int seamus = shay + nora;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the driver to press PLAY
        waitForStart();

        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Number", seamus);
            telemetry.update();
        }
    }
}
