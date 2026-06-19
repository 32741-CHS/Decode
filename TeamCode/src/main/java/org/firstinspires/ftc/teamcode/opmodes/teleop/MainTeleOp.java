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
    private boolean autoShoot = false;
    private boolean intakeOn = false;
    private boolean shooting = false;
    private final ElapsedTime shootTimer = new ElapsedTime();

    private boolean isRedAlliance = false;

    private static final double TRIGGER_DEADBAND = 0.05;
    private static final double SHOOT_DELAY = 3.0;
    private static final double AUTO_SHOOT_POWER = 0.85;

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

        // Shooting: right trigger = flywheel power, feeder after delay
        float rawTrigger = gamepad1.right_trigger;
        if (rawTrigger > TRIGGER_DEADBAND) {
            flywheel.setPower(rawTrigger);
            if (!shooting) {
                shootTimer.reset();
            }
            shooting = true;
            if (shootTimer.seconds() >= SHOOT_DELAY) {
                feeder.start();
            } else {
                feeder.stop();
            }
        } else {
            if (shooting) {
                feeder.stop();
                flywheel.stop();
            }
            shooting = false;
        }

        // Auto-shoot
        if (gp1.b.wasPressed()) {
            autoShoot = !autoShoot;
        }

        if (autoShoot && !shooting) {
            int goalTag = vision.getGoalTagId(isRedAlliance);
            if (vision.isTagVisible(goalTag)) {
                double bearing = vision.getTagBearing(goalTag);
                double targetAngle = susan.bearingToTargetAngle(bearing);
                susan.setAngle(targetAngle);
            }
            if (!flywheel.isRunning()) {
                flywheel.setPower(AUTO_SHOOT_POWER);
            }
        } else if (!shooting) {
            if (gp1.dpadLeft.isHeld()) {
                susan.setPower(-0.25);
            } else if (gp1.dpadRight.isHeld()) {
                susan.setPower(0.25);
            } else {
                susan.setPower(0);
            }
        }

        // Telemetry
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
        telemetry.addData("Flywheel", flywheel.getSpeedLabel());
        telemetry.addData("Feeder", feeder.isRunning() ? "ON" : "OFF");
        telemetry.addData("Drive", drivetrain.getSpeedLabel());
        telemetry.addData("Mode", fieldRelative ? "Field Relative" : "Robot Centric");
        telemetry.addData("Susan", autoShoot ? "AUTO" : String.format("%.1f deg", susan.getAngle()));
        telemetry.addData("Auto Shoot", autoShoot ? "ON" : "OFF");
        if (autoShoot) {
            int goalTag = vision.getGoalTagId(isRedAlliance);
            telemetry.addData("Goal Tag", vision.isTagVisible(goalTag) ? "FOUND" : "NOT FOUND");
        }
        telemetry.addData("Shoot Timer", shooting ? String.format("%.1fs", shootTimer.seconds()) : "-");
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
