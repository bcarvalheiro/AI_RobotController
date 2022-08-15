package simulator;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class Main {
	private static FIS fis ;
	private static FunctionBlock fb;
	private static Simulator s;
	
	public static void main(String[] args) {
		
		String filename = "robot_set.fcl";
		
		fis = FIS.load(filename,true);
		
		if ( fis == null) {
			System.err.println("Can't load file : '" + filename + "'");
			System.exit(1);
		}
		 s = new Simulator();
		
		 fb = fis.getFunctionBlock("robot_sensores");
		
		if (fb == null) {
			System.err.println("Can't get the Function Block");
		}
		
		
//		while(s.getDistanceC() != 0 || s.getDistanceL() != 0 || s.getDistanceR() != 0 ) {
			
			System.out.println(s.getDistanceC());
			System.out.println(s.getDistanceR());
			System.out.println(s.getDistanceL());
			
			
			//System.out.println(s.getMushrooms());
			fb.setVariable("right_sensor" , s.getDistanceR());
			fb.setVariable("center_sensor", s.getDistanceC());
			fb.setVariable("left_sensor", s.getDistanceL());
			fb.evaluate();
			Double angle = fb.getVariable("wheels_angle").defuzzify();
			s.setRobotAngle(angle);
			//System.out.println(fb);
			System.out.println("Wheels angle is : " + fb.getVariable("wheels_angle").getValue());	
			s.step();
//		}
		
	
		
	}

}
