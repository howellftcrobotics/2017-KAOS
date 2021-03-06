package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="KAOS-Auto-Blue-Right-Front-3", group="KAOS - Auto")
//@Disabled
public class KAOS_Auto_Blue_Right_Front_3 extends LinearOpMode
{
    @Override
    public void runOpMode()
    {
        // setup the properties of this OpMode
        CoreConfig coreConfig = new CoreConfig(
                CoreConfig.ROBOT_TEAM.KAOS,
                CoreConfig.ROBOT_OPERATING_MODE.AUTONOMOUS,
                CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_FRONT_3,
                CoreConfig.ROBOT_DRIVING_VIEW_MODE.STANDARD,
                telemetry, hardwareMap, this);
        coreConfig.runOpMode();
    }
}
