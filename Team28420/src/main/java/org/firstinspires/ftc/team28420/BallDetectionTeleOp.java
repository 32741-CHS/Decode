package org.firstinspires.ftc.team28420;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.config.GamepadConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.processors.BallDetection;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;
import org.firstinspires.ftc.team28420.types.Position;
import org.firstinspires.ftc.team28420.types.WheelsRatio;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Point;

@TeleOp(name = "Auto Motif Intake", group = "New Actions")
public class BallDetectionTeleOp extends LinearOpMode {
    private Actions act;
    private BallDetection ballDetection;
    private VisionPortal visionPortal;
    private boolean dpadPressed = false;

    // Assuming standard 640x480 webcam resolution. Adjust if your camera differs.
    private final double CAMERA_CENTER_X = 320.0;

    private void initialize() throws InterruptedException {
        act = new Actions(hardwareMap, telemetry);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize BallDetection Processor and VisionPortal
        ballDetection = new BallDetection();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1")) // Change name if needed
                .addProcessor(ballDetection)
                .build();

        act.init();
    }

    private void handleTargeting() {
        if (ShooterConf.TARGET_MOTIF == null) {
            act.setMotif();
        }
        telemetry.addData("scanned motif", ShooterConf.TARGET_MOTIF);
    }

    private void handleMovement() {
        // --- NEW: Auto Align & Triplet Intake Sequence ---
        if (gamepad1.circle) {
            act.setDribblerVelocityCoefficient(1.0f); // Turn on intake

            Point ballPos = ballDetection.getBallPosition();
            if (ballPos != null) {
                // Calculate horizontal error from the center of the frame
                double errorX = ballPos.x - CAMERA_CENTER_X;

                // Proportional controller for turn (rx). Clamped to prevent violent jerking.
                double rx = Math.max(-0.4, Math.min(0.4, errorX * 0.002));

                // Move forward slowly (0.25) while rotating to align
                act.move(act.getRatios(0, 0.25, rx));
            } else {
                // Blind Creeping: If we momentarily lose sight of the ball (e.g., it goes under the bumper),
                // keep driving forward slowly to ensure we sweep up all three Artifacts in the sequence.
                act.move(act.getRatios(0, 0.2, 0));
            }
        }
        // --- Existing AprilTag Logic ---
        else if (gamepad1.right_trigger > 0.2) {
            act.move(act.getRatiosForApriltag(AprilTag.BLUE, -2, CameraConf.RANGE_TO_TAG));
        } else if (gamepad1.right_bumper) {
            act.move(act.getRatiosLookApriltag(AprilTag.BLUE, 0, CameraConf.RANGE_TO_TAG));
        } else {
            manualDrive();
        }
    }

    private void manualDrive() {
        double x = act.getCubic(act.withDeathzone(gamepad1.left_stick_x, GamepadConf.LEFT_DEAD_ZONE));
        double y = -act.getCubic(act.withDeathzone(gamepad1.left_stick_y, GamepadConf.LEFT_DEAD_ZONE));
        double rx = act.getCubic(act.withDeathzone(gamepad1.right_stick_x, GamepadConf.RIGHT_DEAD_ZONE));

        WheelsRatio<Double> ratios = act.getRatios(new MovementParams(
                new PolarVector(new Position(x, y)), rx));

        act.move(ratios);
    }

    private void indicateReady() {
        gamepad2.setLedColor(0, 255, 0, -1);
        gamepad2.rumble(0);
    }

    private void handleShooter() {
        if (gamepad2.triangle || gamepad2.circle) {
            if (gamepad2.circle) act.resetRevolverTicks();
            act.toggleShooterManualControl(false);
            indicateReady();
        }

        handleRevolverInput();

        float shooterPower = (float) ((gamepad2.right_trigger > 0.4) ? Math.pow(gamepad2.right_trigger, 2) : 0);
        shooterPower *= gamepad2.left_bumper ? 1.3f : 1;

        act.prepareForShoot(shooterPower);

        if (gamepad2.right_bumper) act.shoot();
    }

    private void rotateRevolver(int degrees) {
        act.revolverRotate(degrees);
        gamepad2.setLedColor(255, 0, 0, -1);
        gamepad2.rumble(-1);
    }

    private void handleRevolverInput() {
        if (!dpadPressed) {
            if (gamepad2.dpad_left) rotateRevolver(-120);
            if (gamepad2.dpad_right) rotateRevolver(120);
            if (gamepad2.dpad_up) rotateRevolver(-2);
            if (gamepad2.dpad_down) rotateRevolver(2);
        }
        dpadPressed = (gamepad2.dpad_left || gamepad2.dpad_right || gamepad2.dpad_up || gamepad2.dpad_down);
    }

    private void handleIntakeAndParking() {
        // Prevent manual intake toggle from fighting the Auto-Intake logic
        if (!gamepad1.a) {
            if (gamepad1.left_bumper) {
                float power = (gamepad1.left_trigger > 0.5) ? -0.5f : 1.0f;
                act.setDribblerVelocityCoefficient(power);
            } else {
                if(gamepad2.right_trigger < 0.4)
                    act.setDribblerVelocityCoefficient(0);
            }
        }

        if (gamepad1.dpad_up) act.park();
    }

    private void handleTurret() {
        if (gamepad2.triangle) {
            act.goTurretToAprilTag(AprilTag.BLUE, gamepad2.right_stick_x * 10);
        } else {
            act.goTurretToGyroAngle(gamepad2.right_stick_x * 10);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        act.afterStart();
        ShooterConf.TARGET_MOTIF = null;

        while (opModeIsActive()) {
            act.updateLastAngles();
            act.updateApriltags();

            handleTargeting();
            handleMovement();       // Now includes Ball Alignment
            handleShooter();
            handleIntakeAndParking();
            handleTurret();

            if (gamepad2.right_bumper) {
                act.shoot();
            }

            act.updateShooter();
            act.log();
            ballDetection.updateTelemetry(telemetry); // Helpful for tuning vision
            telemetry.update();
        }
    }
}