package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.utils.Apriltags.BLUE_GOAL;
import static org.firstinspires.ftc.teamcode.utils.Apriltags.RED_GOAL;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Vision;

import java.util.ArrayList;
import java.util.List;

// thx claude (i made a new account lols) :)

// temp opmode to make lookup table.
// 1. place robot at measured distance from goal
// 2. adjust flywheel rps until shots go in
// 3. press a to record the pair
// 4. copy the printed table into ballistics.java
@TeleOp(name = "Distance to RPS", group = "Tuning")
public class DistanceToRPSOpMode extends LinearOpMode {

    private final RobotHardware hw = new RobotHardware();
    private Shooter shooter;
    private Vision vision;

    public static boolean isRed = false;

    // store recorded (distance, rps) pairs
    private List<double[]> recordedPoints = new ArrayList<>();

    @Override
    public void runOpMode() {
        hw.init(hardwareMap);
        shooter = new Shooter(hw);
        vision = new Vision(hw);

        telemetry.addData("Status", "Ready. START+A for Red, START+B for Blue");
        telemetry.update();

        // pick alliance during init
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.start && gamepad1.a) isRed = true;
            if (gamepad1.start && gamepad1.b) isRed = false;
            telemetry.addData("Alliance", isRed ? "RED" : "BLUE");
            telemetry.addData("Controls",
                "D-pad up/down = RPS | RT = feed | A = record | B = clear");
            telemetry.update();
        }

        waitForStart();

        while (opModeIsActive()) {
            // flywheel controls
            if (gamepad1.dpad_up) shooter.speedUpFlywheel();
            if (gamepad1.dpad_down) shooter.slowDownFlywheel();
            if (gamepad1.x) shooter.toggleFlywheel();
            if (gamepad1.right_trigger > 0.5) shooter.feed();

            shooter.update();

            // get distance from april tag
            int goalTag = isRed ? RED_GOAL : BLUE_GOAL;
            double distance = vision.getTagRange(goalTag);

            // record point on A press
            if (gamepad1.a) {
                if (distance > 0) {
                    recordedPoints.add(new double[]{distance, Shooter.desiredFlywheelRPS});
                    telemetry.addData("Recorded", "%.1fm -> %.0f RPS", distance, Shooter.desiredFlywheelRPS);
                } else {
                    telemetry.addData("Recorded", "no tag visible, cant record");
                }
                // small debounce
                sleep(300);
            }

            // clear table on B press
            if (gamepad1.b) {
                recordedPoints.clear();
                telemetry.addData("Cleared", "table reset");
                sleep(300);
            }

            // telemetry
            telemetry.addData("Distance", distance > 0 ? String.format("%.2f m", distance) : "no tag");
            telemetry.addData("Flywheel RPS", "%.1f (target: %.0f)",
                shooter.getFlywheelRPS(), Shooter.desiredFlywheelRPS);
            telemetry.addData("Flywheel error", "%.1f", shooter.getFlywheelErrorRPS());

            telemetry.addLine();
            telemetry.addData("Recorded points", recordedPoints.size());
            for (int i = 0; i < recordedPoints.size(); i++) {
                double[] pt = recordedPoints.get(i);
                telemetry.addData("  " + i, "%.1fm -> %.0f RPS", pt[0], pt[1]);
            }

            telemetry.addLine();
            telemetry.addLine("copy this:");
            telemetry.addLine("{ " + formatTable() + " }");

            telemetry.update();
        }

        shooter.update();
    }

    private String formatTable() {
        if (recordedPoints.isEmpty()) return "nothing, record some shots first";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recordedPoints.size(); i++) {
            double[] pt = recordedPoints.get(i);
            sb.append(String.format("{ %.1f, %.0f }", pt[0], pt[1]));
            if (i < recordedPoints.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
