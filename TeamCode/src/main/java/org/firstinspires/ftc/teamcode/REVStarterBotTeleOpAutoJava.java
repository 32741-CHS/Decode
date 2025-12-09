package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class REVStarterBotTeleOpAutoJava extends LinearOpMode {

  private DcMotor flywheel;
  private Servo shotControl;
  private DcMotor coreHex;
  private DcMotor leftDrive;
  private DcMotor rightDrive;
  double servoPosition;
  private static final int bankVelocity = 1050;
  private static final int farVelocity = 2200;
  private static final int maxVelocity = 2200;
  private static final String TELEOP = "TELEOP";
  private static final String AUTO_BLUE_GOAL = "AUTO BLUE GOAL";
  private static final String AUTO_BLUE_BACK = "AUTO BLUE BACK";
  private static final String AUTO_RED_BACK = "AUTO RED BACK";
  private static final String AUTO_RED_GOAL = "AUTO RED GOAL";
  private static final String AUTO_FORWARD = "AUTO FORWARD";
  private String operationSelected = TELEOP;
  private double WHEELS_INCHES_TO_TICKS = (28 * 5 * 3) / (3 * Math.PI);
  private ElapsedTime autoLaunchTimer = new ElapsedTime();
  private ElapsedTime autoDriveTimer = new ElapsedTime();

  @Override
  public void runOpMode() {
    flywheel = hardwareMap.get(DcMotor.class, "flywheel");
    coreHex = hardwareMap.get(DcMotor.class, "coreHex");
    leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
    rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
    shotControl = hardwareMap.get(Servo.class, "shotControl");

    // Establishing the direction and mode for the motors
    flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    flywheel.setDirection(DcMotor.Direction.REVERSE);
    coreHex.setDirection(DcMotor.Direction.REVERSE);
    leftDrive.setDirection(DcMotor.Direction.REVERSE);

    //On initilization the Driver Station will prompt for which OpMode should be run - Auto Blue, Auto Red, or TeleOp
    while (opModeInInit()) {
      operationSelected = selectOperation(operationSelected, gamepad1.psWasPressed());
      telemetry.update();
    }
    waitForStart();
    if (operationSelected.equals(AUTO_BLUE_GOAL)) {
      doAutoBlueGoal();
    } else if (operationSelected.equals(AUTO_BLUE_BACK)) {
      doAutoBlueBack();
    } else if (operationSelected.equals(AUTO_RED_GOAL)) {
      doAutoRedGoal();
    } else if (operationSelected.equals(AUTO_RED_BACK)) {
      doAutoRedBack();
    } else if (operationSelected.equals(AUTO_FORWARD)) {
      doAutoForward();
    } else {
      doTeleOp();
      servoPosition = 0;
    }
  }

  /**
   * If the PS/Home button is pressed, the robot will cycle through the OpMode options following the if/else statement here.
   * The telemetry readout to the Driver Station App will update to reflect which is currently selected for when "play" is pressed.
   */
  private String selectOperation(String state, boolean cycleNext) {
    if (cycleNext) {
      if (state.equals(TELEOP)) {
        state = AUTO_BLUE_GOAL;
      } else if (state.equals(AUTO_BLUE_GOAL)) {
        state = AUTO_BLUE_BACK;
      } else if (state.equals(AUTO_BLUE_BACK)) {
        state = AUTO_RED_GOAL;
      } else if (state.equals(AUTO_RED_GOAL)) {
        state = AUTO_RED_BACK;
      } else if (state.equals(AUTO_FORWARD)) {
        state = TELEOP;
      } else if (state.equals(AUTO_RED_BACK)) {
        state = AUTO_FORWARD;
      } else {
        telemetry.addData("WARNING", "Unknown Operation State Reached - Restart Program");
      }
    }
    telemetry.addLine("Press Home Button to cycle options");
    telemetry.addData("CURRENT SELECTION", state);
    if (state.equals(AUTO_BLUE_GOAL) || state.equals(AUTO_RED_GOAL) || state.equals(AUTO_BLUE_BACK) || state.equals(AUTO_RED_BACK) || state.equals(AUTO_FORWARD)) {
      telemetry.addLine("Please remember to enable the AUTO timer!");
    }
    telemetry.addLine("Press START to start your program");
    return state;
  }

  //TeleOp Code

  /**
   * If TeleOp was selected or defaulted to, the following will be active upon pressing "play".
   */
  private void doTeleOp() {
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        // Calling our methods while the OpMode is running
        splitStickArcadeDrive();
        setFlywheelVelocity();
        manualCoreHexControl();
        telemetry.addData("Flywheel Velocity", ((DcMotorEx) flywheel).getVelocity());
        telemetry.addData("Flywheel Power", flywheel.getPower());
        telemetry.update();
        shotControl.setPosition(servoPosition);
      }
    }
  }

  /**
   * Controls for the drivetrain. The robot uses a split stick stlye arcade drive.
   * Forward and back is on the left stick. Turning is on the right stick.
   */
  private void splitStickArcadeDrive() {
    float X;
    float Y;

    X = gamepad1.right_stick_x;
    Y = -gamepad1.left_stick_y;
    leftDrive.setPower(Y - X);
    rightDrive.setPower(Y + X);
  }

  /**
   * Manual control for the Core Hex powered feeder
   */
  private void manualCoreHexControl() {
    // Manual control for the Core Hex intake
    if (gamepad1.cross) {
      coreHex.setPower(0.5);
    } else if (gamepad1.triangle) {
      coreHex.setPower(-0.5);
    }
    if (gamepad1.dpad_left) {
      servoPosition -= 0.01;
    } else if (gamepad1.dpad_right) {
      servoPosition += 0.01;
    }
  }

  /**
   * This if/else statement contains the controls for the flywheel, both manual and auto.
   * Circle and Square will spin up ONLY the flywheel to the target velocity set.
   * The bumpers will activate the flywheel, and Core Hex feeder
   */
  private void setFlywheelVelocity() {
    if (gamepad1.options) {
      flywheel.setPower(-0.5);
    } else if (gamepad1.left_bumper) {
      FAR_POWER_AUTO();
    } else if (gamepad1.right_bumper) {
      BANK_SHOT_AUTO();
    } else if (gamepad1.circle) {
      ((DcMotorEx) flywheel).setVelocity(bankVelocity);
    } else if (gamepad1.square) {
      ((DcMotorEx) flywheel).setVelocity(maxVelocity);
    } else {
      ((DcMotorEx) flywheel).setVelocity(0);
      coreHex.setPower(0);
    }
  }

//Automatic Flywheel controls used in Auto and TeleOp

  /**
   * The bank shot or near velocity is intended for launching balls touching or a few inches from the goal.
   * When running this function, the flywheel will spin up and the Core Hex will wait before balls can be fed.
   */
  private void BANK_SHOT_AUTO() {
    ((DcMotorEx) flywheel).setVelocity(bankVelocity);
    if (((DcMotorEx) flywheel).getVelocity() >= bankVelocity - 100) {
      coreHex.setPower(0.95);
    } else {
      coreHex.setPower(0);
    }
  }

  /**
   * The far power velocity is intended for launching balls a few feet from the goal. It may require adjusting the deflector.
   * When running this function, the flywheel will spin up and the Core Hex will wait before balls can be fed.
   */
  private void FAR_POWER_AUTO() {
    ((DcMotorEx) flywheel).setVelocity(farVelocity);
    if (((DcMotorEx) flywheel).getVelocity() >= farVelocity - 100) {
      coreHex.setPower(1);
    } else {
      coreHex.setPower(0);
    }
  }

//Autonomous Code
//For autonomous, the robot will launch the pre-loaded 3 balls then back away from the goal, turn, and back up off the launch line.

  /**
   * For autonomous, the robot is using a timer and encoders on the drivetrain to move away from the target.
   * This method contains the math to be used with the inputted distance for the encoders, resets the elapsed timer, and
   * provides a check for it to run so long as the motors are busy and the timer has not run out.
   */
  private void autoDrive(double speed, int leftDistanceInch, int rightDistanceInch, int timeout_ms) {
    autoDriveTimer.reset();

    // Because leftDrive is REVERSED, we must flip the sign of its target distance.
    int leftTarget = leftDrive.getCurrentPosition() + (int) (-leftDistanceInch * WHEELS_INCHES_TO_TICKS);
    int rightTarget = rightDrive.getCurrentPosition() + (int) (rightDistanceInch * WHEELS_INCHES_TO_TICKS);

    leftDrive.setTargetPosition(leftTarget);
    rightDrive.setTargetPosition(rightTarget);

    leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    // FIX: Remove Math.abs(). RUN_TO_POSITION handles direction via the target sign.
    // The power should always be a positive speed limit.
    leftDrive.setPower(speed);
    rightDrive.setPower(speed);

    while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy()) && autoDriveTimer.milliseconds() < timeout_ms) {
      idle();
    }

    leftDrive.setPower(0);
    rightDrive.setPower(0);
    leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }
//  private void autoDrive(double speed, int leftDistanceInch, int rightDistanceInch, int timeout_ms) {
//    autoDriveTimer.reset();
//    leftDrive.setTargetPosition((int) (leftDrive.getCurrentPosition() + leftDistanceInch * WHEELS_INCHES_TO_TICKS));
//    rightDrive.setTargetPosition((int) (rightDrive.getCurrentPosition() + rightDistanceInch * WHEELS_INCHES_TO_TICKS));
//    leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    leftDrive.setPower(Math.abs(speed));
//    rightDrive.setPower(Math.abs(speed));
//    while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy()) && autoDriveTimer.milliseconds() < timeout_ms) {
//      idle();
//    }
//    leftDrive.setPower(0);
//    rightDrive.setPower(0);
//    leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//  }

  /**
   * Blue Alliance Autonomous
   * The robot will fire the pre-loaded balls until the 10 second timer ends.
   * Then it will back away from the goal and off the launch line.
   */
  private void doAutoBlueGoal() {
    if (opModeIsActive()) {
      telemetry.addData("RUNNING OPMODE", operationSelected);
      telemetry.update();
      // Fire balls
      autoLaunchTimer.reset();
      while (opModeIsActive() && autoLaunchTimer.milliseconds() < 10000) {
        BANK_SHOT_AUTO();
        telemetry.addData("Launcher Countdown", autoLaunchTimer.seconds());
        telemetry.update();
      }
      ((DcMotorEx) flywheel).setVelocity(0);
      coreHex.setPower(0);
      leftDrive.setPower(-1);
      rightDrive.setPower(-1);
      sleep(500);
      leftDrive.setPower(0.5);
      rightDrive.setPower(-0.5);
      sleep(500);
      leftDrive.setPower(0.75);
      rightDrive.setPower(0.75);
      sleep(500);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
    }
  }

  private void doAutoBlueBack() {
    if (opModeIsActive()) {
      telemetry.addData("RUNNING OPMODE", operationSelected);
      telemetry.update();
      leftDrive.setPower(1);
      rightDrive.setPower(1);
      sleep(1125);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
      sleep(100);
      leftDrive.setPower(0.5);
      rightDrive.setPower(-0.5);
      sleep(500);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
      sleep(100);
      leftDrive.setPower(1);
      rightDrive.setPower(1);
      sleep(750);
      // Fire balls
      autoLaunchTimer.reset();
      while (opModeIsActive() && autoLaunchTimer.milliseconds() < 10000) {
        BANK_SHOT_AUTO();
        telemetry.addData("Launcher Countdown", autoLaunchTimer.seconds());
        telemetry.update();
      }
      ((DcMotorEx) flywheel).setVelocity(0);
    }
  }

  /**
   * Red Alliance Autonomous
   * The robot will fire the pre-loaded balls until the 10 second timer ends.
   * Then it will back away from the goal and off the launch line.
   */
  private void doAutoRedGoal() {
    if (opModeIsActive()) {
      telemetry.addData("RUNNING OPMODE", operationSelected);
      telemetry.update();
      // Fire balls
      autoLaunchTimer.reset();
      while (opModeIsActive() && autoLaunchTimer.milliseconds() < 10000) {
        BANK_SHOT_AUTO();
        telemetry.addData("Launcher Countdown", autoLaunchTimer.seconds());
        telemetry.update();
      }
      ((DcMotorEx) flywheel).setVelocity(0);
      coreHex.setPower(0);
      leftDrive.setPower(-1);
      rightDrive.setPower(-1);
      sleep(500);
      leftDrive.setPower(-0.5);
      rightDrive.setPower(0.5);
      sleep(500);
      leftDrive.setPower(0.75);
      rightDrive.setPower(0.75);
      sleep(500);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
    }
  }

  private void doAutoRedBack() {
    if (opModeIsActive()) {
      telemetry.addData("RUNNING OPMODE", operationSelected);
      telemetry.update();
      leftDrive.setPower(1);
      rightDrive.setPower(1);
      sleep(1125);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
      sleep(100);
      leftDrive.setPower(-0.5);
      rightDrive.setPower(0.5);
      sleep(500);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
      sleep(100);
      leftDrive.setPower(1);
      rightDrive.setPower(1);
      sleep(750);
      // Fire balls
      autoLaunchTimer.reset();
      while (opModeIsActive() && autoLaunchTimer.milliseconds() < 10000) {
        BANK_SHOT_AUTO();
        telemetry.addData("Launcher Countdown", autoLaunchTimer.seconds());
        telemetry.update();
      }
      ((DcMotorEx) flywheel).setVelocity(0);
    }
  }

  private void doAutoForward() {
    if (opModeIsActive()) {
      telemetry.addData("RUNNING OPMODE", operationSelected);
      telemetry.update();
      leftDrive.setPower(1);
      rightDrive.setPower(1);
      sleep(500);
      leftDrive.setPower(0);
      rightDrive.setPower(0);
      sleep(0);
    }
  }
}
