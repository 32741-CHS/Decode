package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.GamepadEx;

@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();

    private Drivetrain drivetrain;
    private Intake intake;
    private Shooter shooter;
    private Turret turret;

    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();

    private boolean isFieldDriving = false;

    private static final double TRIGGER_THRESHOLD = 0.5;

    @Override
    public void init() {
        hw.init(hardwareMap);

        drivetrain = new Drivetrain(hw);
        intake = new Intake(hw);
        shooter = new Shooter(hw);
        turret = new Turret(hw);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        gp1.update(gamepad1);
        gp2.update(gamepad2);

        // gamepad 1
        drivetrain.setSpeedMultiplier(gp1.lb.isHeld(), gp1.rb.isHeld());
        drivetrain.drive(
            -gamepad1.left_stick_y,
            gamepad1.left_stick_x,
            gamepad1.right_stick_x,
                isFieldDriving
        );

        if (gp1.y.wasPressed()) {
            isFieldDriving = !isFieldDriving;
        }

        // gamepad 2
        if (gp2.dpadUp.wasPressed()) {shooter.speedUpFlywheel();}
        if (gp2.dpadDown.wasPressed()) {shooter.slowDownFlywheel();}

        if (gp2.y.wasPressed()) {drivetrain.resetIMU();}


        if (gp2.lt >= TRIGGER_THRESHOLD) {intake.eat();}

        if (gp2.rt >= TRIGGER_THRESHOLD) {shooter.feed();}

        if (gp2.x.wasPressed()) { shooter.toggleFlywheel();}

        //TODO implement manual turret control

        intake.update();
        shooter.update();

        // Telemetry
        telemetry.addData("Intake power:",  intake.getPower());
        telemetry.addData("Feeder power:", shooter.getFeederPower());
        telemetry.addLine(String.format("Flywheel rps: %.2f, error: %.2f", shooter.getFlywheelRPS(), shooter.getFlywheelErrorRPS()));
        //telemetry.addLine(String.format("Driving Mode: %s, Driving Speed: %s", isFieldDriving ? "field" : "robot", drivetrain.getSpeedMultiplier()));
    }
}
