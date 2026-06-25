package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.utils.Apriltags.BLUE_GOAL;
import static org.firstinspires.ftc.teamcode.utils.Apriltags.RED_GOAL;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.graph.PanelsGraph;
import com.bylazar.graph.GraphManager;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Ballistics;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.Vision;
import org.firstinspires.ftc.teamcode.utils.GamepadEx;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends OpMode {

    private final RobotHardware hw = new RobotHardware();

    private Drivetrain drivetrain;
    private Intake intake;
    private Shooter shooter;
    private Turret turret;
    private Vision vision;

    private final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
    private final GraphManager panelsGraph = PanelsGraph.INSTANCE.getManager();
    private final GamepadEx gp1 = new GamepadEx();
    private final GamepadEx gp2 = new GamepadEx();

    private final GamepadManager pgp1 = PanelsGamepad.INSTANCE.getFirstManager();
    private final GamepadManager pgp2 = PanelsGamepad.INSTANCE.getSecondManager();

    private boolean isFieldDriving = false;
    public static boolean isRed = false;

    private static final double TRIGGER_THRESHOLD = 0.5;

    @Override
    public void init() {
        hw.init(hardwareMap);

        drivetrain = new Drivetrain(hw);
        intake = new Intake(hw);
        shooter = new Shooter(hw);
        vision = new Vision(hw);
        turret = new Turret(hw);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        turret.resetTimer();
    }
    @Override
    public void loop() {
        gp1.update(pgp1.asCombinedFTCGamepad(gamepad1));
        gp2.update(pgp2.asCombinedFTCGamepad(gamepad2));

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
        if (gp2.b.wasPressed()) {turret.resetTurretEncoder();}

        if (gp2.lt >= TRIGGER_THRESHOLD) {intake.eat();}
        if (gp2.a.isHeld()) { intake.invert();}

        if (gp2.rt >= TRIGGER_THRESHOLD) {shooter.feed();}
        if (gp2.x.wasPressed()) { shooter.toggleFlywheel();}

        AprilTagDetection goalTag = vision.getTagById(isRed ? RED_GOAL : BLUE_GOAL);
        if (goalTag != null) {
            double angle = Ballistics.calculateTurretAngle(goalTag.ftcPose.yaw, turret.getCurrentAngle());
            turret.goTo(angle);
        }

        intake.update();
        shooter.update();
        turret.update();

        // Telemetry
        panelsTelemetry.addData("Intake power",  intake.getPower());
        panelsTelemetry.addData("Feeder power", shooter.getFeederPower());
        panelsTelemetry.addData("Flywheel rps", shooter.getFlywheelRPS());
        panelsTelemetry.addData("Flywheel error", shooter.getFlywheelErrorRPS());
        panelsTelemetry.addData("Drivetrain speed", drivetrain.getSpeedMultiplier());
        panelsTelemetry.addData("Turret angle", turret.getCurrentAngle());
        panelsTelemetry.addData("Turret error", turret.getErrorAngle());

        // panels graph feed
        panelsGraph.addData("flywheelRPS", shooter.getFlywheelRPS());
        panelsGraph.addData("flywheelTarget", Shooter.desiredFlywheelRPS);
        panelsGraph.addData("flywheelError", shooter.getFlywheelErrorRPS());
        panelsGraph.addData("feederPower", shooter.getFeederPower());
        panelsGraph.addData("intakePower", intake.getPower());

        panelsGraph.update();
        panelsTelemetry.update(telemetry);
    }
}
