package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LazySusan;
import org.firstinspires.ftc.teamcode.subsystems.Vision;
import org.firstinspires.ftc.teamcode.utils.GamepadEx;
import org.firstinspires.ftc.teamcode.utils.TelemetryManager;

/**
 * Main TeleOp
 *
 * Both controllers:
 *   B (toggle)    → flywheel on/off
 *   D-pad Up      → flywheel speed up
 *   D-pad Down    → flywheel speed down
 *
 * Controller 2 (Driver):
 *   Left Stick    → drive + strafe
 *   Right Stick X → rotate
 *   A (toggle)    → field-relative / robot-centric
 *   RB (hold)     → slow mode
 *   LB (hold)     → turbo mode
 *
 * Controller 1 (Operator):
 *   Left Stick Y  → intake
 *   A (toggle)    → auto-shoot on/off
 *   D-pad Left    → spin turret left (hold)
 *   D-pad Right   → spin turret right (hold)
 *
 * IMU: logo UP, USB FORWARD
 * Webcam: "Webcam 1" on C920, 640x480, mounted on turret
 */
@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();
    private Drivetrain drivetrain;
    private Intake intake;
    private LazySusan susan;
    private Flywheel flywheel;
    private Vision vision;

    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();
    private TelemetryManager tm;

    private boolean fieldRelative = false;
    private boolean autoShoot = false;

    // TODO: set this manually before each match. we need a better way to do this.
    private boolean isRedAlliance = false;

    @Override
    public void init() {
        hw.init(hardwareMap);
        drivetrain = new Drivetrain(hw);
        intake = new Intake(hw);
        susan = new LazySusan(hw);
        flywheel = new Flywheel(hw);
        vision = new Vision();
        vision.init(hardwareMap);

        tm = new TelemetryManager(telemetry);
        tm.addData("Status", "Initialized");
        tm.update();
    }

    @Override
    public void loop() {
        gp1.update(gamepad1);
        gp2.update(gamepad2);

        // === Controller 2: Drive ===
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

        // flywheel on both controllers (B toggle, dpad speed)
        if (gp1.b.wasPressed() || gp2.b.wasPressed()) {
            flywheel.setRunning(!flywheel.isRunning());
        }
        if (gp1.dpadUp.wasPressed() || gp2.dpadUp.wasPressed()) {
            flywheel.speedUp();
        }
        if (gp1.dpadDown.wasPressed() || gp2.dpadDown.wasPressed()) {
            flywheel.speedDown();
        }

        // === Controller 1: Operator ===

        // intake on left stick Y
        intake.setPower(-gamepad1.left_stick_y);

        // auto-shoot toggle (A)
        if (gp1.a.wasPressed()) {
            autoShoot = !autoShoot;
        }

        if (autoShoot) {
            // auto mode: vision tracks goal, susan aims, flywheel runs
            int goalTag = vision.getGoalTagId(isRedAlliance);

            if (vision.isTagVisible(goalTag)) {
                double bearing = vision.getTagBearing(goalTag);
                double targetAngle = susan.bearingToTargetAngle(bearing);
                susan.setAngle(targetAngle);
            }
            // tag not visible = susan holds position

            // flywheel stays on during auto-shoot
            if (!flywheel.isRunning()) {
                flywheel.setRunning(true);
            }
        } else {
            // manual mode: operator controls susan with dpad
            if (gp1.dpadLeft.isHeld()) {
                susan.setPower(-0.25);
            } else if (gp1.dpadRight.isHeld()) {
                susan.setPower(0.25);
            } else {
                susan.setPower(0);
            }
        }

        // === Telemetry ===
        tm.addSection("Drive");
        tm.addData("Speed Mode", drivetrain.getSpeedLabel());
        tm.addData("Drive Mode", fieldRelative ? "Field Relative" : "Robot Centric");
        tm.addSection("Shooter");
        tm.addData("Flywheel", flywheel.getSpeedLabel());
        tm.addData("Auto Shoot", autoShoot ? "ON" : "OFF");
        tm.addSection("Turret");
        tm.addData("Angle", autoShoot ? "AUTO" : String.format("%.1f deg", susan.getAngle()));
        if (autoShoot) {
            int goalTag = vision.getGoalTagId(isRedAlliance);
            tm.addData("Goal Tag", vision.isTagVisible(goalTag) ? "FOUND" : "NOT FOUND");
        }
        tm.update();
    }

    @Override
    public void stop() {
        drivetrain.stop();
        intake.stop();
        susan.stop();
        flywheel.stop();
        vision.stop();
    }
}
