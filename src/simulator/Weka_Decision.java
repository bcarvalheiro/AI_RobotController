package simulator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

//THIS CLASS WILL CLASSIFY THE MUSHROOOOMS

public class Weka_Decision {

	static DataSource shroomFile;
	static Instances shroom;
	static J48 shroomClassifier;
	static Visualizer viewDecisionTree;
	static Evaluation eval;

	public Weka_Decision() {
		try {
			// load dataSource from "mushroom.arff"
			shroomFile = new DataSource("mushroom.arff");

			// Each shroom loaded is an Instance;
			shroom = shroomFile.getDataSet();

//			We have got 4 atributes, index starts at 0, so 4-1 its 3, and the attribute choosen
//			is class
			// we don't have to find the bestsplit because we are going to use the attirbute
			// class (???)
			shroom.setClassIndex(shroom.numAttributes() - 1);

			// starts a new decisionTree
			shroomClassifier = new J48();

			// Divide os shrooms e classifica-os
			shroomClassifier.buildClassifier(shroom);

			// Open the decision Tree visualization
			viewDecisionTree = new Visualizer();
			viewDecisionTree.start(shroomClassifier);

			// set cross validation 10 by 10 with all training set
			eval = new Evaluation(shroom);
			eval.crossValidateModel(shroomClassifier, shroom, 10, new Random(1));

			// prints the evaluation metrics
			System.out.println(eval.toSummaryString("Those are the results : \n", false));

			// prints the fuzzy matrix
			// it represents the way the system classifys the shrooms
			// how many were poisonous and were classified as poisonous or edible, and
			// vice-versa
			System.out.println("Eval matrix \n" + eval.toMatrixString());

			// prints the tree
			System.out.println("Decision Tree : \n" + shroomClassifier.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getAction(String[] value) {
		double predict = 0;
		try {
			// Test for NewInstances
			NewInstances newInstances = new NewInstances(shroom);
			// TO-DO : Those are static values, now we need to get the values from the
			// simulator windows. Something like this, but I don't know where to call the
			// simulator from...
			// Simulator s = new Simulator();
			// String[] values = s.getMushroomAttributes();
			// Maybe we should implement a method that recieves the atribbutes of the shroom
			// and give us a string (Edible, Poisonous) as output.
			String[] shroomValues = value;
			newInstances.addInstance(shroomValues);

			Instances testNewInstances = newInstances.getDataset();
			// testNewInstances.setClassIndex(testNewInstances.numAttributes() - 1);

			System.out.println("Actual Class \t PredictedClass");
			Instance instance = testNewInstances.get(0);
			// decision value ("Poisonous or Edible")
			String actual = instance.stringValue(instance.numAttributes() - 1);

			// We take the classify and we classifie the instance
			predict = shroomClassifier.classifyInstance(instance);
			String pred = testNewInstances.classAttribute().value((int) (predict));
			System.out.println(actual + " \t " + pred);
			//return predict;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return predict;

	}
}
