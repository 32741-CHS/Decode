package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Chassis;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;
import org.firstinspires.ftc.teamcode.mechanisms.Vision;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name="Base Teleop", group="OpMode")
public class BaseTeleop extends OpMode {
    Chassis chassis = new Chassis();
    Vision vision = new Vision();
    Turret turret = new Turret();

    boolean isRed = false;

    @Override
    public void init() {
        vision.init(hardwareMap, telemetry);
        chassis.init(hardwareMap);
        turret.init(hardwareMap, telemetry);

        telemetry.addLine("Initialized");
        telemetry.update();
    }

    public void start() {
        turret.resetTimer();
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

        vision.update();
        AprilTagDetection goalTag = vision.getTagById(isRed ? 24 : 20);

        if (goalTag != null) {
            telemetry.addData("Sees goal tag", true);
            vision.displayTagTelemetry(goalTag);
            turret.slewTo(goalTag.ftcPose.bearing);
        }

        turret.update();
    }
}
