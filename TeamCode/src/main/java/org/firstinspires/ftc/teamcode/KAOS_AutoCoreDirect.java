
package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

public class KAOS_AutoCoreDirect
{
    private BaseHardware m_robot;
    private CoreConfig m_coreConfig;

    public KAOS_AutoCoreDirect(CoreConfig coreConfig)
    {
        m_coreConfig = coreConfig;
        m_robot = new BaseHardware(m_coreConfig);
    }

    public void runOpMode()
    {
        if (!m_robot.init())
        {
            m_coreConfig.Telemetry.addData("Error", "Hardware Init Failed");
            m_coreConfig.Telemetry.update();
            m_coreConfig.OpMode.sleep(8000);  // sleep for 8 seconds so the error can be read
            return;
        }

        // initialize the devices
        m_robot.InitRelicGripper();
        m_robot.InitRelicArmKnuckle();
        m_robot.InitializeGryo();
        m_robot.RaiseLeftJewelArm();
        m_robot.RaiseRightJewelArm();
        m_robot.CenterRightJewelArmFlicker();
        m_robot.CenterLeftJewelArmFlicker();

        // initialize the BaseVuMark reader
        m_coreConfig.Telemetry.addData(">", "Initializing VuMark...");
        m_coreConfig.Telemetry.update();
        BaseVuMark vuMark = new BaseVuMark(m_coreConfig, true);
        RelicRecoveryVuMark glyphColumn = vuMark.ReadVuMark(2, true);
        m_coreConfig.Telemetry.addData(">", "VuMark read attempt 1: " + glyphColumn.toString());
        m_coreConfig.Telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        m_coreConfig.Telemetry.addData(">", "Waiting for start..." + glyphColumn.toString());
        m_coreConfig.Telemetry.update();
        m_coreConfig.OpMode.waitForStart();

        // raise lift with glyph in it
        //m_coreConfig.Telemetry.addData(">", "Raising glyph...");
        //m_coreConfig.Telemetry.update();
        m_robot.RunLiftToTargetPosition(-0.9, 500, 1.5);

        // read the BaseVuMark for a few seconds
        if (glyphColumn == RelicRecoveryVuMark.UNKNOWN)
        {
            m_coreConfig.Telemetry.addData(">", "VuMark unknown, reading vUmARK again...");
            m_coreConfig.Telemetry.update();
            glyphColumn = vuMark.ReadVuMark(1, true);
            vuMark.Deactivate();
        }

        // if we couldn't read the vumark, default to right
        if (glyphColumn == RelicRecoveryVuMark.UNKNOWN)
        {
            m_coreConfig.Telemetry.addData(">", "Could not determine BaseVuMark position, defaulting to center...");
            m_coreConfig.Telemetry.update();
            glyphColumn = RelicRecoveryVuMark.CENTER;
            //glyphColumn = RelicRecoveryVuMark.RIGHT;
            //glyphColumn = RelicRecoveryVuMark.LEFT;
        }
        else
        {
            m_coreConfig.Telemetry.addData(">", "VuMark read attempt 2: " + glyphColumn.toString());
            m_coreConfig.Telemetry.update();
        }

        // lower the jewel arm
        //m_coreConfig.Telemetry.addData(">", "Lowering jewel arm...");
        //m_coreConfig.Telemetry.update();
        m_robot.LowerJewelArm();
        m_coreConfig.OpMode.sleep(750);

        // read the color
        //m_coreConfig.Telemetry.addData(">", "Reading color...");
        //m_coreConfig.Telemetry.update();
        BaseHardware.COLOR_RESULT colorResult = m_robot.ReadJewelColor(2, true);
        //m_robot.DisableColorSensorLEDs();
        //m_coreConfig.Telemetry.addData(">", "Read color: " + colorResult.toString());
        //m_coreConfig.Telemetry.update();

        // knock off the jewel
        m_coreConfig.OpMode.sleep(250);
        if ((m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1) ||
            (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_FRONT_2))
        {
            if (colorResult == BaseHardware.COLOR_RESULT.RED)
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 1/2, saw blue, pull arm");
                //m_coreConfig.Telemetry.update();
                m_robot.PullJewelArmFlicker();
            }
            else if (colorResult == BaseHardware.COLOR_RESULT.BLUE)
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 1/2, saw red, push arm");
                //m_coreConfig.Telemetry.update();
                m_robot.PushJewelArmFlicker();
            }
            else
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 1/2, saw nothing, do nothing");
                //m_coreConfig.Telemetry.update();
                m_robot.CenterJewelArmFlicker();
            }
        }
        else
        {
            if (colorResult == BaseHardware.COLOR_RESULT.BLUE)
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 3/4, saw blue, pull arm");
                //m_coreConfig.Telemetry.update();
                m_robot.PullJewelArmFlicker();
            }
            else if (colorResult == BaseHardware.COLOR_RESULT.RED)
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 3/4, saw red, push arm");
                //m_coreConfig.Telemetry.update();
                m_robot.PushJewelArmFlicker();
            }
            else
            {
                //m_coreConfig.Telemetry.addData(">", "Pos 3/4, saw nothing, did nothing");
                //m_coreConfig.Telemetry.update();
                m_robot.CenterJewelArmFlicker();
            }
        }

        // after we are done, center and raise the jewel arm before moving
        //m_coreConfig.Telemetry.addData(">", "Centering flicker & raising arm...");
        //m_coreConfig.Telemetry.update();
        m_coreConfig.OpMode.sleep(250);
        m_robot.RaiseJewelArm();
        m_coreConfig.OpMode.sleep(500);
        m_robot.CenterJewelArmFlicker();
        m_coreConfig.OpMode.sleep(250);

        // what is our starting position, run the correct code
//**STEP 1**
        m_coreConfig.Telemetry.update();
        if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1)
        {
            // drive straight off
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.DriveByEncoder(0.7, 50 , 50, 7);
            m_robot.gyroTurn(.5, 0, .5);
            m_robot.gyroHold(.2, 0, .75);

            // drive straight (distance depends on glyph column that was read)
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.D_ALL_SIDE_2_SIDE;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                m_robot.DriveByEncoder(0.8, -39, -39, 5);
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                m_robot.DriveByEncoder(0.8, -25, -25, 5);
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                m_robot.DriveByEncoder(0.8, -12 , -12, 5);
            }
            //m_coreConfig.OpMode.sleep(500);

            // straighten up and drive forward to crypto box
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.gyroTurn(.5, 0, .5);
            m_robot.gyroHold(.2, 0, .75);
            //m_coreConfig.OpMode.sleep(500);
            m_robot.DriveByEncoder(0.7, 20 , 20, 5);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_FRONT_2)
        {
            // drive straight (distance depends on glyph column that was read)
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                // tested 3/5/18 dead on center
                m_robot.DriveByEncoder(0.8, 58, 58, 5);
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                // tested 3/5/18 dead on center
                m_robot.DriveByEncoder(0.8, 72, 72, 5);
            }
            else if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                // tested 3/5/18 dead on center
                m_robot.DriveByEncoder(0.8, 85 , 85, 5);
            }

            // straighten up and drive forward to crypto box
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.gyroTurn(.5, -90, 1.1);
            m_robot.gyroHold(.2, -90, 1.5);
            //m_coreConfig.OpMode.sleep(500);
            m_robot.DriveByEncoder(0.8, 25 , 25, 5);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_FRONT_3)
        {
            // drive straight (distance depends on glyph column that was read)
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                m_robot.DriveByEncoder(0.8, 58, 58, 5);
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                m_robot.DriveByEncoder(0.8, 72, 72, 5);
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                m_robot.DriveByEncoder(0.8, 85, 85, 6);
            }
            //m_coreConfig.OpMode.sleep(500);

            // straighten up and drive forward to crypto box
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.gyroTurn(.5, 90, 1.1);
            m_robot.gyroHold(.2, 90, 1.5);
            //m_coreConfig.OpMode.sleep(500);
            m_robot.DriveByEncoder(0.8, 25 , 25, 5);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_BACK_4)
        {
            // drive straight off
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.DriveByEncoder(0.8, 50 , 50, 7);
            m_robot.gyroTurn(.5, 0, .5);
            m_robot.gyroHold(.2, 0, .75);

            // drive straight (distance depends on glyph column that was read)
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.D_ALL_SIDE_2_SIDE;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                m_robot.DriveByEncoder(0.9, 10, 10, 5);
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                m_robot.DriveByEncoder(0.9, 24, 24, 5);
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                m_robot.DriveByEncoder(0.9, 37 , 37, 5);
            }
            //m_coreConfig.OpMode.sleep(500);

            // straighten up and drive forward to crypto box
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_robot.gyroTurn(.5, 0, .5);
            m_robot.gyroHold(.2, 0, .75);
            //m_coreConfig.OpMode.sleep(500);
            m_robot.DriveByEncoder(0.7, 24 , 24, 5);
        }

        // push block out of intake rollers
        //m_coreConfig.Telemetry.addData(">", "Pushing block out of intake rollers..");
        //m_coreConfig.Telemetry.update();
        m_robot.StartRollOutBlock();
        m_coreConfig.OpMode.sleep(250);

//**STEP 2**
        // turn and go for another glyph
        double backupPower = .3;
        if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1)
        {
            // back away from block
            m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(backupPower, -15 , -15, 2);
            m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int centerAngle = 150;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                centerAngle = 160;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                // tested good 3-7-18
                centerAngle = 155;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                centerAngle = 135;
            }
            m_robot.gyroTurn(.6, centerAngle, .9);
            m_robot.gyroHold(.2, centerAngle, 1);

            //m_coreConfig.Telemetry.addData(">", "Lower the lift...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, -275, 1);

            //m_coreConfig.Telemetry.addData(">", "Starting intake motors...");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollInBlock();

            //m_coreConfig.Telemetry.addData(">", "Driving toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                centerAngle = 150;
                m_robot.DriveByEncoder(.9, 50, 50, 5);
                m_robot.gyroTurn(.3, centerAngle, .4);
                m_robot.gyroHold(.2, centerAngle, .3);
                m_robot.DriveByEncoder(.9, 60, 60, 5);
            }
            else
            {
                m_robot.DriveByEncoder(.9, 110, 110, 9);
            }
            m_coreConfig.OpMode.sleep(500);

            //m_coreConfig.Telemetry.addData(">", "Stopping intake motors...");
            //m_coreConfig.Telemetry.update();
            //m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Raise glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, 1400, 1.5);

            //m_coreConfig.Telemetry.addData(">", "Backup before turning...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, -16 , -16, 10);

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int returnAngle = -15;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                returnAngle = -15;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                returnAngle = -15;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                returnAngle = -10;
            }
            m_robot.gyroTurn(.6, returnAngle, .9);
            m_robot.gyroHold(.2, returnAngle, 1.1);

            //m_coreConfig.Telemetry.addData(">", "Driving toward crypto box...");
            // m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, 125 , 125, 10);

            //m_coreConfig.Telemetry.addData(">", "Pushing block out of intake rollers..");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollOutBlock();
            m_coreConfig.OpMode.sleep(250);

            // back away from block
            //m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.15, -15, -15, 3);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_FRONT_2)
        {
            //m_coreConfig.Telemetry.addData(">", "Backing up before we turn around for another glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(backupPower, -22 , -22, 3);
            m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.gyroTurn(.6, 90, 1.1);
            m_robot.gyroHold(.2, 90, 1.5);
            //m_coreConfig.OpMode.sleep(500);

            //m_coreConfig.Telemetry.addData(">", "Lower the lift...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, -275, 1);

            //m_coreConfig.Telemetry.addData(">", "Starting intake motors...");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollInBlock();

            //m_coreConfig.Telemetry.addData(">", "Driving toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.9, 76 , 76, 10);
            m_coreConfig.OpMode.sleep(250);

            //m_coreConfig.Telemetry.addData(">", "Stopping intake motors...");
            //m_coreConfig.Telemetry.update();
            //m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Raise glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, 1200, 1);

            //m_coreConfig.Telemetry.addData(">", "Backup before turning...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, -16 , -16, 10);

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int returnAngle = -90;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                returnAngle = -98;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                returnAngle = -90;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                returnAngle = -82;
            }
            m_robot.gyroTurn(.6, returnAngle, 1.1);
            m_robot.gyroHold(.2, returnAngle, 1.5);

            //m_coreConfig.Telemetry.addData(">", "Driving toward crypto box...");
            // m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, 88 , 88, 10);

            //m_coreConfig.Telemetry.addData(">", "Pushing block out of intake rollers..");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollOutBlock();
            m_coreConfig.OpMode.sleep(250);

            // back away from block
            //m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.15, -15, -15, 2);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_FRONT_3)
        {
            //m_coreConfig.Telemetry.addData(">", "Backing up before we turn around for another glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(backupPower, -22 , -22, 3);
            m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.gyroTurn(.6, -90, 1.1);
            m_robot.gyroHold(.2, -90, 1.5);
            //m_coreConfig.OpMode.sleep(500);

            //m_coreConfig.Telemetry.addData(">", "Lower the lift...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, -275, 1);

            //m_coreConfig.Telemetry.addData(">", "Starting intake motors...");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollInBlock();

            //m_coreConfig.Telemetry.addData(">", "Driving toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.9, 76 , 76, 10);
            m_coreConfig.OpMode.sleep(250);

            //m_coreConfig.Telemetry.addData(">", "Stopping intake motors...");
            //m_coreConfig.Telemetry.update();
            //m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Raise glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, 1200, 1);

            //m_coreConfig.Telemetry.addData(">", "Backup before turning...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, -16 , -16, 10);

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int returnAngle = -90;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                returnAngle = 92;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                returnAngle = 90;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                returnAngle = 98;
            }
            m_robot.gyroTurn(.6, returnAngle, 1.1);
            m_robot.gyroHold(.2, returnAngle, 1.5);

            //m_coreConfig.Telemetry.addData(">", "Driving toward crypto box...");
            // m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, 88 , 88, 10);

            //m_coreConfig.Telemetry.addData(">", "Pushing block out of intake rollers..");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollOutBlock();
            m_coreConfig.OpMode.sleep(250);

            // back away from block
            //m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.15, -15, -15, 2);
        }
        else if (m_coreConfig.StartingPosition == CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_BACK_4)
        {
            // back away from block
            m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(backupPower, -15 , -15, 2);
            m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int centerAngle = -135;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                centerAngle = -125;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                // tested good 3-7-18
                centerAngle = -150;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                centerAngle = -155;
            }
            m_robot.gyroTurn(.6, centerAngle, .9);
            m_robot.gyroHold(.2, centerAngle, 1);

            //m_coreConfig.Telemetry.addData(">", "Lower the lift...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, -275, 1);

            //m_coreConfig.Telemetry.addData(">", "Starting intake motors...");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollInBlock();

            //m_coreConfig.Telemetry.addData(">", "Driving toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                centerAngle = -150;
                m_robot.DriveByEncoder(.9, 50, 50, 5);
                m_robot.gyroTurn(.3, centerAngle, .4);
                m_robot.gyroHold(.2, centerAngle, .3);
                m_robot.DriveByEncoder(.9, 60, 60, 5);
            }
            else
            {
                m_robot.DriveByEncoder(.9, 110, 110, 9);
            }
            m_coreConfig.OpMode.sleep(500);

            //m_coreConfig.Telemetry.addData(">", "Stopping intake motors...");
            //m_coreConfig.Telemetry.update();
            //m_robot.StopIntakeMotors();

            //m_coreConfig.Telemetry.addData(">", "Raise glyph...");
            //m_coreConfig.Telemetry.update();
            m_robot.RunLiftToTargetPosition(.9, 1200, 1);

            //m_coreConfig.Telemetry.addData(">", "Backup before turning...");
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, -16 , -16, 10);

            //m_coreConfig.Telemetry.addData(">", "Turning toward center field for a glyph...");
            //m_coreConfig.Telemetry.update();
            int returnAngle = 45;
            if (glyphColumn == RelicRecoveryVuMark.LEFT)
            {
                returnAngle = 20;
            }
            else if ((glyphColumn == RelicRecoveryVuMark.CENTER) || (glyphColumn == RelicRecoveryVuMark.UNKNOWN))
            {
                returnAngle = 15;
            }
            else if (glyphColumn == RelicRecoveryVuMark.RIGHT)
            {
                returnAngle = 20;
            }
            m_robot.gyroTurn(.6, returnAngle, .9);
            m_robot.gyroHold(.2, returnAngle, 1.1);

            //m_coreConfig.Telemetry.addData(">", "Driving toward crypto box...");
            // m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(1, 125 , 125, 10);

            //m_coreConfig.Telemetry.addData(">", "Pushing block out of intake rollers..");
            //m_coreConfig.Telemetry.update();
            m_robot.StartRollOutBlock();
            m_coreConfig.OpMode.sleep(250);

            // back away from block
            //m_coreConfig.Telemetry.addData(">", "Backup away from block...");
            m_coreConfig.DriveTrainSet = CoreConfig.ROBOT_DRIVE_TRAIN_SET.C_ALL_FORWARD_BACKWARD;
            //m_coreConfig.Telemetry.update();
            m_robot.DriveByEncoder(.15, -15, -15, 3);
        }

        //m_coreConfig.Telemetry.addData(">", "Stopping intake motors..");
        //m_coreConfig.Telemetry.update();
        m_robot.StopIntakeMotors();
    }
}
