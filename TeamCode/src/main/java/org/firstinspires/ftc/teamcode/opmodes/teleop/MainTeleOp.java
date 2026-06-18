package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
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
 *   Left Stick Y  → intake (forward=OUT, backward=IN)
 *   A (toggle)    → auto-shoot on/off
 *   D-pad Left    → spin susan left (hold)
 *   D-pad Right   → spin susan right (hold)
 *   D-pad Up      → flywheel speed up
 *   D-pad Down    → flywheel speed down
 *   B (toggle)    → flywheel on/off
 *
 * IMU: logo UP, USB FORWARD
 * Config names: frontLeft, frontRight, backLeft, backRight, lazySusan, flywheel, intake, imu
 */
@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();
    private Drivetrain drivetrain;

    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();

    private boolean fieldRelative = false;

    @Override
    public void init() {
        hw.init(hardwareMap);
        drivetrain = new Drivetrain(hw);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        gp1.update(gamepad1);
        gp2.update(gamepad2);

        // toggle field relative on A press
        if (gp2.a.wasPressed()) {
            fieldRelative = !fieldRelative;
        }

        // controller 2 drives
        drivetrain.setSpeedMode(gp2.rb.isHeld(), gp2.lb.isHeld());
        drivetrain.drive(
            -gamepad2.left_stick_y,
            gamepad2.left_stick_x,
            gamepad2.right_stick_x,
            fieldRelative
        );

        // controller 1 stuff coming soon
        // TODO: intake on left stick Y
        // TODO: turret on dpad
        // TODO: flywheel toggle + speed on B and dpad up/down
        // TODO: auto-shoot toggle on A

        telemetry.addData("Drive", drivetrain.getSpeedLabel());
        telemetry.addData("Mode", fieldRelative ? "Field Relative" : "Robot Centric");
        telemetry.update();
    }

    @Override
    public void stop() {
        drivetrain.stop();
    }
}
