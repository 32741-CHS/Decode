package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.utils.GamepadEx;

// main teleop. only drivetrain works right now.
// intake, turret, flywheel, vision all coming in later branches.
//
// controller 2: drive
// controller 1: operator stuff (intake, turret, shooting)
@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();
    private Drivetrain drivetrain;

    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();

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

        // controller 2 drives
        drivetrain.setSpeedMode(gp2.rb.isHeld(), gp2.lb.isHeld());
        drivetrain.drive(
            -gamepad2.left_stick_y,
            gamepad2.left_stick_x,
            gamepad2.right_stick_x,
            false  // robot-centric for now
        );

        // controller 1 stuff coming soon
        // TODO: intake on left stick Y
        // TODO: turret on dpad
        // TODO: flywheel toggle + speed on B and dpad up/down
        // TODO: auto-shoot toggle on A

        telemetry.addData("Drive", drivetrain.getSpeedLabel());
        telemetry.update();
    }

    @Override
    public void stop() {
        drivetrain.stop();
    }
}
