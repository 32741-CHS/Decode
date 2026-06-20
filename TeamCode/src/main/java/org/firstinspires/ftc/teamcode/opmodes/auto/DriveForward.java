package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

@Autonomous(name="Robot: Auto Drive By Time", group="Robot")
public class DriveForward extends LinearOpMode {
    private final RobotHardware hw = new RobotHardware();
    private Drivetrain drivetrain;
    private final ElapsedTime timer = new ElapsedTime();

    private static final double DRIVE_POWER    = 0.4;
    private static final double DRIVE_SECONDS  = 1.0; // tune this

    @Override
    public void runOpMode() {
        hw.init(hardwareMap);
        drivetrain = new Drivetrain(hw);

        waitForStart();
        timer.reset();

        while (opModeIsActive() && timer.seconds() < DRIVE_SECONDS) {
            drivetrain.drive(DRIVE_POWER, 0, 0, false);
        }

        drivetrain.stop();
    }
}
}
