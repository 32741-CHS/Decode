package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants();

    public static DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants()
            .leftFrontMotorName("flDrive")
            .leftRearMotorName("blDrive")
            .rightFrontMotorName("frDrive")
            .rightRearMotorName("brDrive");

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .driveEncoderLocalizer(driveEncoderConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
