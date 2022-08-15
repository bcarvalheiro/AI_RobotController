package simulator;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class RobotController {

	private FIS fis;
	private FunctionBlock fb;
	private Simulator s;
	static Weka_Decision actionDecider;

	public RobotController() {

		String filename = "robot_set.fcl";
		fis = FIS.load(filename, true);
		if (fis == null) {
			System.err.println("Can't load file : '" + filename + "'");
			System.exit(1);
		}
		s = new Simulator();
		fb = fis.getFunctionBlock("robot_sensores");
		if (fb == null) {
			System.err.println("Can't get the Function Block");
		}
	}

	private void run() {
		s.setRobotSpeed(10);
		s.step();
		do {
			// Gets the distance of a shroom from the sensor
			double leftSensor = s.getDistanceL();
			double rightSensor = s.getDistanceR();
			double centerSensor = s.getDistanceC();
			// will se its values for fuzzy to defuzzify wheels angles
			fb.setVariable("center_sensor", centerSensor);
			fb.setVariable("left_sensor", leftSensor);
			fb.setVariable("right_sensor", rightSensor);
			// will evaluate the angle we need the robot to turn
			fb.evaluate();
			Double angle = fb.getVariable("wheels_angle").defuzzify();
			System.out.println("i'm assigning " + angle + " to my wheels");
			s.setRobotAngle(angle);
			// if it gets close to the object, it will slow down!
			if (s.getDistanceC() > 5 && s.getDistanceL() > 5 && s.getDistanceR() > 5)
				s.setRobotSpeed(10);
			else
				s.setRobotSpeed(5);

			s.step();

		} while (s.getDistanceC() > 1 && s.getDistanceL() > 1 && s.getDistanceR() > 1);

		// as soon as it gets near the shroom, will get its attributes
		String[] values = s.getMushroomAttributes();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				System.out.println(values[i]);
			}
			double shroomCategory = actionDecider.getAction(values);
			System.out.println("This is the value of the weka decision for the shroom category " + shroomCategory);
			fb.setVariable("shroom", shroomCategory);
			fb.evaluate();
			// System.out.println(shroomCategory);
			double action = fb.getVariable("robot_action").defuzzify();
			System.out.println("This is the value of action after defuzzify " + action);
			switch ((int) action) {
			case 0:
				s.setAction(Action.NO_ACTION);
				System.out.println("action set = No Action");
				break;
			case 100:
				s.setAction(Action.PICK_UP);
				System.out.println("action set = Pick up");
				break;
			case -100:
				s.setAction(Action.DESTROY);
				System.out.println("action set = Destroy");
				break;
			}

		} else {
			System.err.println("Values is null, coulnd't get mushrooms attributes");
			run();
		}

		s.step();
		s.setAction(Action.NO_ACTION);
		run();
	}

	public static void main(String[] args) {
		actionDecider = new Weka_Decision();
		RobotController robot = new RobotController();
		robot.run();
	}

}
