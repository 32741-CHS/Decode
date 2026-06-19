package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Feeder;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LazySusan;
import org.firstinspires.ftc.teamcode.subsystems.Vision;
import org.firstinspires.ftc.teamcode.utils.GamepadEx;

/**
 * Main TeleOp
 *
 * Controller 2 (Driver):
 *   Left Stick    → drive + strafe
 *   Right Stick X → rotate
 *   RB (hold)     → slow mode (25%)
 *   LB (hold)     → turbo mode (100%)
 *   A (toggle)    → field-relative / robot-centric
 *
 * Controller 1 (Operator):
 *   A (toggle)    → intake on/off
 *   B (toggle)    → flywheel on/off
 *   D-pad Up      → flywheel speed up (10%)
 *   D-pad Down    → flywheel speed down (10%)
 *   D-pad Left    → spin susan left (hold)
 *   D-pad Right   → spin susan right (hold)
 *   Right Trigger → run feeder for 5 seconds
 *
 * IMU: logo UP, USB FORWARD
 * Webcam: "Webcam 1" on C920, 640x480, mounted on turret
 */
@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();
    private Drivetrain drivetrain;
    private Feeder feeder;
    private Intake intake;
    private LazySusan susan;
    private Flywheel flywheel;
    private Vision vision;

    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();

    private boolean fieldRelative = false;
    private boolean intakeOn = false;
    private boolean feederRunning = false;
    private boolean triggerWasPressed = false;
    private final ElapsedTime feederTimer = new ElapsedTime();

    private static final double TRIGGER_THRESHOLD = 0.5;
    private static final double FEEDER_DURATION = 5.0;

    @Override
    public void init() {
        hw.init(hardwareMap);
        drivetrain = new Drivetrain(hw);
        feeder = new Feeder(hw);
        intake = new Intake(hw);
        susan = new LazySusan(hw);
        flywheel = new Flywheel(hw);
        vision = new Vision(hw);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        gp1.update(gamepad1);
        gp2.update(gamepad2);

        // Controller 2: Drive
        if (gp2.a.wasPressed()) {
            fieldRelative = !fieldRelative;
        }
        drivetrain.setSpeedMode(gp2.rb.isHeld(), gp2.lb.isHeld());
        drivetrain.drive(
            -gamepad2.left_stick_y,
            gamepad2.left_stick_x,
            gamepad2.right_stick_x,
            fieldRelative
        );

        // Controller 1: Operator

        // Intake toggle
        if (gp1.a.wasPressed()) {
            intakeOn = !intakeOn;
            intake.setPower(intakeOn ? -1.0 : 0);
        }

        // Flywheel toggle and speed
        if (gp1.b.wasPressed()) {
            flywheel.setRunning(!flywheel.isRunning());
        }
        if (gp1.dpadUp.wasPressed()) {
            flywheel.speedUp();
        }
        if (gp1.dpadDown.wasPressed()) {
            flywheel.speedDown();
        }

        // Right trigger: run feeder for 5 seconds on press
        boolean triggerPressed = gamepad1.right_trigger > TRIGGER_THRESHOLD;
        if (!feederRunning && triggerPressed && !triggerWasPressed) {
            feeder.start();
            feederTimer.reset();
            feederRunning = true;
        }
        triggerWasPressed = triggerPressed;

        if (feederRunning && feederTimer.seconds() >= FEEDER_DURATION) {
            feeder.stop();
            feederRunning = false;
        }

        // Turret manual
        if (gp1.dpadLeft.isHeld()) {
            susan.setPower(-0.25);
        } else if (gp1.dpadRight.isHeld()) {
            susan.setPower(0.25);
        } else {
            susan.setPower(0);
        }

        // Telemetry
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
        telemetry.addData("Flywheel", flywheel.getSpeedLabel());
        telemetry.addData("Feeder", feederRunning ? "ON" : "OFF");
        telemetry.addData("Feeder Timer", feederRunning ? String.format("%.1fs", feederTimer.seconds()) : "-");
        telemetry.addData("Drive", drivetrain.getSpeedLabel());
        telemetry.addData("Mode", fieldRelative ? "Field Relative" : "Robot Centric");
        telemetry.addData("Susan", String.format("%.1f deg", susan.getAngle()));
        telemetry.update();
    }

    @Override
    public void stop() {
        drivetrain.stop();
        feeder.stop();
        intake.stop();
        susan.stop();
        flywheel.stop();
        vision.stop();
    }
}
