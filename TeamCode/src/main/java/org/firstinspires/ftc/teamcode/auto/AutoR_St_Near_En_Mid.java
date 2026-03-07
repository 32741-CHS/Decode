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
import org.firstinspires.ftc.teamcode.field.Red;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.MechController;
import org.firstinspires.ftc.teamcode.robot.MechState;
import org.firstinspires.ftc.teamcode.robot.RobotHardware;
import org.firstinspires.ftc.teamcode.robot.VisionController;
import org.firstinspires.ftc.vision.VisionPortal;
@Autonomous(name = "AutoR_St_Near_En_Mid", group = "Red")
public class AutoR_St_Near_En_Mid extends OpMode {

    RobotHardware robot;
    MechController mechController;
    VisionController visionController;
    private VisionPortal visionPortal;

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    private final Pose startPose = Red.START_POSE_NEAR;
    private final Pose aprilTagPoseReach = Red.APRILTAG_POSE_NEAR_REACH;
    private final Pose aprilTagPose = Red.APRILTAG_POSE_NEAR_READ;
    private final Pose scorePoseNear = Red.SCORE_POSE_NEAR;
    private final Pose readyMidPose = Red.READY_MID_POSE;
    private final Pose alignMidPose = Red.ALIGN_MID_POSE;
    private final Pose pickupMidPose = Red.PICKUP_MID_POSE;
    private final Pose readyNearPose = Red.READY_NEAR_POSE;
    private final Pose alignNearPose = Red.ALIGN_NEAR_POSE;
    private final Pose pickupNearPose = Red.PICKUP_NEAR_POSE;


    private Path aprilTagReach;
    private PathChain aprilTagRead, scorePreload, readyNear, alignNear, grabNear, scoreNear, readyMid, alignMid, grabMid;

    public void buildPaths() {
        aprilTagReach = new Path(new BezierLine(startPose, aprilTagPoseReach));
        aprilTagReach.setLinearHeadingInterpolation(startPose.getHeading(), aprilTagPoseReach.getHeading());

        aprilTagRead = follower.pathBuilder()
                .addPath(new BezierLine(aprilTagPoseReach, aprilTagPose))
                .setLinearHeadingInterpolation(aprilTagPoseReach.getHeading(), aprilTagPose.getHeading())
                .build();

        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(aprilTagPose, scorePoseNear))
                .setLinearHeadingInterpolation(aprilTagPose.getHeading(), scorePoseNear.getHeading())
                .build();

        readyNear = follower.pathBuilder()
                .addPath(new BezierLine(scorePoseNear, readyNearPose))
                .setLinearHeadingInterpolation(scorePoseNear.getHeading(), readyNearPose.getHeading())
                .build();

        alignNear = follower.pathBuilder()
                .addPath(new BezierLine(readyNearPose, alignNearPose))
                .setLinearHeadingInterpolation(readyNearPose.getHeading(), alignNearPose.getHeading())
                .build();

        grabNear = follower.pathBuilder()
                .addPath(new BezierLine(alignNearPose, pickupNearPose))
                .setLinearHeadingInterpolation(alignNearPose.getHeading(), pickupNearPose.getHeading())
                .build();

        scoreNear = follower.pathBuilder()
                .addPath(new BezierLine(pickupNearPose, scorePoseNear))
                .setLinearHeadingInterpolation(pickupNearPose.getHeading(), scorePoseNear.getHeading())
                .build();

        readyMid = follower.pathBuilder()
                .addPath(new BezierLine(scorePoseNear, readyMidPose))
                .setLinearHeadingInterpolation(scorePoseNear.getHeading(), readyMidPose.getHeading())
                .build();

        alignMid = follower.pathBuilder()
                .addPath(new BezierLine(readyMidPose, alignMidPose))
                .setLinearHeadingInterpolation(readyMidPose.getHeading(), alignMidPose.getHeading())
                .build();

        grabMid = follower.pathBuilder()
                .addPath(new BezierLine(alignMidPose, pickupMidPose))
                .setLinearHeadingInterpolation(alignMidPose.getHeading(), pickupMidPose.getHeading())
                .build();
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(aprilTagReach);
                mechController.setState(MechState.APRIL_TAG);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    follower.followPath(aprilTagRead);
                    mechController.setState(MechState.APRIL_TAG);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(scorePreload, true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    mechController.setState(MechState.SHOOT_STATE); // Shoot preload
                    follower.followPath(readyNear,true);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(alignNear,true);
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(grabNear,true);
                    mechController.setState(MechState.INTAKE_STATE); //Intake 1
                    setPathState(6);
                }
                break;
            case 6:
                if(!follower.isBusy()) {
                    follower.followPath(scoreNear,true);
                    setPathState(7);
                }
                break;
            case 7:
                if(!follower.isBusy()) {
                    mechController.setState(MechState.SHOOT_STATE); // Shoot 1
                    follower.followPath(readyMid,true);
                    setPathState(8);
                }
                break;
            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(alignMid,true);
                    setPathState(9);
                }
                break;
            case 9:
                if(!follower.isBusy()) {
                    follower.followPath(grabMid,true);
                    mechController.setState(MechState.INTAKE_STATE); // Intake 2
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
