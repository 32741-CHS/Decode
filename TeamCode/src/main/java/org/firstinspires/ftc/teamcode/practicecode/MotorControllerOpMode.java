package org.firstinspires.ftc.teamcode.practicecode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Practice: Motor Controller", group = "Practice")
public class MotorControllerOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        // "practiceMotor" must match the port name in your robot configuration
        MotorController motor = new MotorController(hardwareMap, "practiceMotor");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Right trigger drives forward, left trigger drives reverse
            double power = gamepad1.right_trigger - gamepad1.left_trigger;
            motor.setPower(power);

            telemetry.addData("Status", "Running");
            telemetry.addData("Motor Power", "%.2f", motor.getPower());
            telemetry.update();
        }

        motor.stop();
    }
}
