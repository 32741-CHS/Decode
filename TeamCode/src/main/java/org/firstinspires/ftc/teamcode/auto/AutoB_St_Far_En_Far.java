package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.field.Blue;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.MechController;
import org.firstinspires.ftc.teamcode.robot.MechState;
import org.firstinspires.ftc.teamcode.robot.RobotHardware;
import org.firstinspires.ftc.teamcode.robot.VisionController;
import org.firstinspires.ftc.vision.VisionPortal;
@Disabled
@Autonomous(name = "AutoB_St_Far_En_Far", group = "Blue")
public class AutoB_St_Far_En_Far extends OpMode {

    RobotHardware robot;
    MechController mechController;
    VisionController visionController;
    private VisionPortal visionPortal;

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    private final Pose startPose = Blue.START_POSE_FAR;
    private final Pose aprilTagPose = Blue.APRILTAG_POSE_FAR;
    private final Pose scorePoseAuto = Blue.SCORE_POSE_AUTO;
    private final Pose readyFarPose = Blue.READY_FAR_POSE;
    private final Pose alignFarPose = Blue.ALIGN_FAR_POSE;
    private final Pose pickupFarPose = Blue.PICKUP_FAR_POSE;
    private final Pose endFarPose = Blue.TELEOP_START_FAR;


    private Path aprilTagRead;
    private PathChain scorePreload, readyFar, alignFar, grabFar, scoreFar, endFar;

    public void buildPaths() {
        aprilTagRead = new Path(new BezierLine(startPose, aprilTagPose));
        aprilTagRead.setLinearHeadingInterpolation(startPose.getHeading(), aprilTagPose.getHeading());

        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(aprilTagPose, scorePoseAuto))
                .setLinearHeadingInterpolation(aprilTagPose.getHeading(), scorePoseAuto.getHeading())
                .build();

        readyFar = follower.pathBuilder()
                .addPath(new BezierLine(scorePoseAuto, readyFarPose))
                .setLinearHeadingInterpolation(scorePoseAuto.getHeading(), readyFarPose.getHeading())
                .build();

        alignFar = follower.pathBuilder()
                .addPath(new BezierLine(readyFarPose, alignFarPose))
                .setLinearHeadingInterpolation(readyFarPose.getHeading(), alignFarPose.getHeading())
                .build();

        grabFar = follower.pathBuilder()
                .addPath(new BezierLine(alignFarPose, pickupFarPose))
                .setLinearHeadingInterpolation(alignFarPose.getHeading(), pickupFarPose.getHeading())
                .build();

        scoreFar = follower.pathBuilder()
                .addPath(new BezierLine(pickupFarPose, scorePoseAuto))
                .setLinearHeadingInterpolation(pickupFarPose.getHeading(), scorePoseAuto.getHeading())
                .build();

        endFar = follower.pathBuilder()
                .addPath(new BezierLine(scorePoseAuto, endFarPose))
                .setLinearHeadingInterpolation(scorePoseAuto.getHeading(), endFarPose.getHeading())
                .build();
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(aprilTagRead);
                mechController.setState(MechState.APRIL_TAG);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    follower.followPath(scorePreload, true);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    mechController.setState(MechState.SHOOT_STATE); // Shoot preload
                    follower.followPath(readyFar,true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(alignFar,true);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(grabFar,true);
                    mechController.setState(MechState.INTAKE_STATE); //Intake 1
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(scoreFar,true);
                    setPathState(6);
                }
                break;
            case 6:
                if(!follower.isBusy()) {
                    mechController.setState(MechState.SHOOT_STATE); // Shoot 1
                    follower.followPath(endFar,true);
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        mechController.update();
        follower.update();
        autonomousPathUpdate();

        MechState state = mechController.getCurrentState();
        if (state == MechState.SHOOT_STATE || state == MechState.APRIL_TAG) {
            follower.setMaxPower(0.0);
        } else if (state == MechState.INTAKE_STATE) {
            follower.setMaxPower(MechController.INTAKE_DRIVE_POWER);
        } else {
            follower.setMaxPower(MechController.FULL_DRIVE_POWER);
        }

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        mechController.allTelemetry();
    }

    @Override
    public void init() {
        robot = new RobotHardware(hardwareMap, telemetry);

        visionController = new VisionController(robot);
        visionController.initAprilTag();
        visionPortal = visionController.getVisionPortal();

        mechController = new MechController(robot, visionController);
        mechController.handleMechState(MechState.START);

        telemetry.addData("Status", "Initialized. Detecting April Tag....");
        telemetry.update();

        pathTimer = new Timer();
        actionTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();


        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }

    @Override
    public void init_loop() {
        mechController.update();
        mechController.allTelemetry();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void stop() {
        visionPortal.stopStreaming();
        mechController.setLifter(0);
        mechController.setIndexer(MechController.INTAKE[0]);
    }
}
