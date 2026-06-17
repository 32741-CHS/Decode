package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Chassis;


@TeleOp(name="Base Teleop", group="OpMode")
public class BaseTeleop extends OpMode {
    private Chassis chassis;

    @Override
    public void init() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if (gamepad1.right_bumper) {
            chassis.drive_field(y, x, rx);
        } else {
            chassis.drive_robot(y, x, rx);
        }
    }
}
