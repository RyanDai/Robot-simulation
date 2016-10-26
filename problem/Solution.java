package problem;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

import tester.Tester;

public class Solution {

	private List<ArmConfig> listConfig;
	/** The initial configuration */
	private ArmConfig initialState;
	/** The goal configuration */
	private ArmConfig goalState;
	/** The obstacles */
	private List<Obstacle> obstacles;
	private int jointCount;

	private Tester test;
	private ProblemSpec pS;

	private boolean gripper;
	private List<ArmConfig> finalPath;

	public Solution(String inputFileName) {
		test = new Tester();
		pS = new ProblemSpec();

		try {
			pS.loadProblem(inputFileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.initialState = pS.getInitialState();
		this.goalState = pS.getGoalState();
		this.jointCount = pS.getJointCount();
		this.gripper = pS.getGripper();
		this.obstacles = pS.getObstacles();

		listConfig = new ArrayList<ArmConfig>();
		finalPath = new ArrayList<ArmConfig>();
	}

	public void sampling(int nodes) {
		int Nodes = nodes;
		listConfig.add(initialState);
		listConfig.add(goalState);

		while (Nodes > 0) {

			ArmConfig node = createNode();
			if (listConfig.contains(node) || test.hasCollision(node, obstacles) || test.hasSelfCollision(node)
					|| !test.fitsBounds(node)) {
				Nodes++;
			} else {
				listConfig.add(node);
			}
			Nodes--;
		}
	}

	public ArmConfig createNode() {
		Random rd = new Random();
		ArrayList<Double> joints = new ArrayList<Double>();
		StringBuilder str = new StringBuilder();
		int count = jointCount;

		joints.add(rd.nextDouble());
		joints.add(rd.nextDouble());

		if (gripper == false) {
			if (count > 1) {
				while (count > 0) {
					joints.add(rd.nextDouble());
					count--;
				}
			} else if (count == 1) {
				joints.add(0.0);
			}
		}

		if (gripper == true) {
			while (count > 0) {
				joints.add(rd.nextDouble());
				count--;
			}
			for (int i = 0; i < 4; i++) {
				joints.add(0.03);
			}
		}

		for (double ele : joints) {
			str.append(ele);
			str.append(" ");
		}

		String config = str.toString();

		ArmConfig node = new ArmConfig(config, gripper);
		return node;
	}

	public void sampling2(int nodes) {
		int Nodes = nodes;
		listConfig.add(initialState);
		listConfig.add(goalState);

		if (initialState.getJointCount() >= 3) {
			Nodes += Nodes;
		}
		while (Nodes > 0) {
			int num = 50;
			ArmConfig node = createNode();
			if (listConfig.contains(node) || !test.fitsBounds(node) || test.hasSelfCollision(node)) {
			} else if (test.hasCollision(node, obstacles)) {
				while (num > 0) {
					ArmConfig tmp = createNode();
					if (!listConfig.contains(tmp) && node.maxDistance(tmp) < 0.2 && !test.hasCollision(tmp, obstacles) && !test.hasSelfCollision(tmp)
							&& test.fitsBounds(tmp)) {
						listConfig.add(tmp);
						Nodes--;
					}
					num--;
				}
			} else {
				listConfig.add(node);
				Nodes--;
			}
		}
	}

	public void sampling3(int nodes) {
		Random rd = new Random();
		int Nodes = nodes;
		int halfNodes = nodes / 2;
		listConfig.add(initialState);
		listConfig.add(goalState);

		if (initialState.getJointCount() >= 4) {
			for (int i = 0; i < initialState.getJointCount() - 3; i++) {
				Nodes += 150;
			}
		}

		while (Nodes > 0) {
			ArmConfig node = createNode();
			int num = 30;
			if (rd.nextInt() % 2 == 0) {
				if (listConfig.contains(node) || test.hasCollision(node, obstacles) || test.hasSelfCollision(node)
						|| !test.fitsBounds(node)) {
					Nodes++;
				} else {
					listConfig.add(node);
				}
				Nodes--;

			} else {
				if (obstacles.isEmpty()) {
					halfNodes = 0;
				}
				if (test.hasCollision(node, obstacles) && halfNodes != 0) {
					while (num > 0) {
						ArmConfig tmp = createNode();
						if (node.maxDistance(tmp) < 0.3 && !test.hasCollision(tmp, obstacles)
								&& !test.hasSelfCollision(tmp) && test.fitsBounds(tmp)) {
							listConfig.add(tmp);
							Nodes--;
						}
						num--;
					}
				}
			}
		}

	}

	public boolean validPath(ArmConfig node1, ArmConfig node2) {
		// how to get path between 2 nodes
		if (test.isValidStep(node1, node2)) {
			return true;
		} else {
			double xcor = (node1.getBaseCenter().getX() + node2.getBaseCenter().getX()) / 2;
			double ycor = (node1.getBaseCenter().getY() + node2.getBaseCenter().getY()) / 2;
			Point2D newBase = new Point2D.Double(xcor, ycor);

			ArrayList<Double> jointList = new ArrayList<Double>();
			ArrayList<Double> gripperList = new ArrayList<Double>();

			for (int i = 0; i < node1.getJointCount(); i++) {
				double newJoint = (node1.getJointAngles().get(i) + node2.getJointAngles().get(i)) / 2;
				jointList.add(newJoint);
			}
			ArmConfig temp = new ArmConfig(newBase, jointList);
			if (gripper == true) {
				for (int i = 0; i < 4; i++) {
					double newGripper = (node1.getGripperLengths().get(i) + node2.getGripperLengths().get(i)) / 2;
					gripperList.add(newGripper);
				}
				temp = new ArmConfig(newBase, jointList, gripperList);
			}

			if (test.hasCollision(temp, obstacles) || test.hasSelfCollision(temp) || !test.fitsBounds(temp)) {
				return false;
			}
			validPath(node1, temp);
			validPath(temp, node2);
		}
		return true;
	}

	public boolean printValidPath(ArmConfig node1, ArmConfig node2) {
		if (test.isValidStep(node1, node2)) {
			finalPath.add(node1);
			return true;
		} else {
			double xcor = (node1.getBaseCenter().getX() + node2.getBaseCenter().getX()) / 2;
			double ycor = (node1.getBaseCenter().getY() + node2.getBaseCenter().getY()) / 2;
			Point2D newBase = new Point2D.Double(xcor, ycor);
			ArrayList<Double> jointList = new ArrayList<Double>();
			ArrayList<Double> gripperList = new ArrayList<Double>();
			for (int i = 0; i < node1.getJointCount(); i++) {
				double newJoint = (node1.getJointAngles().get(i) + node2.getJointAngles().get(i)) / 2;
				jointList.add(newJoint);
			}

			ArmConfig temp = new ArmConfig(newBase, jointList);
			if (gripper == true) {
				for (int i = 0; i < 4; i++) {
					double newGripper = (node1.getGripperLengths().get(i) + node2.getGripperLengths().get(i)) / 2;
					gripperList.add(newGripper);
				}
				temp = new ArmConfig(newBase, jointList, gripperList);
			}

			if (test.hasCollision(temp, obstacles) || test.hasSelfCollision(temp) || !test.fitsBounds(temp)) {
				return false;
			}
			printValidPath(node1, temp);
			printValidPath(temp, node2);
		}
		return true;
	}

	public List<ArmConfig> getFinalPath() {
		finalPath.add(goalState);
		return finalPath;
	}

	public void generateGraph() {
		for (int i = 0; i < listConfig.size(); i++) {
			for (int j = 0; j < listConfig.size(); j++) {
				if (listConfig.get(i).maxDistance(listConfig.get(j)) < 0.15) {
					if (validPath(listConfig.get(i), listConfig.get(j))) {
						listConfig.get(i).adjacencies.add(new Edge(listConfig.get(j)));
					}
				}
			}
		}
	}

	public List<ArmConfig> searchGraph(ArmConfig initial) {
		Queue<ArmConfig> queue = new LinkedList<ArmConfig>();
		Set<ArmConfig> explored = new HashSet<ArmConfig>();
		List<ArmConfig> path = new ArrayList<ArmConfig>();
		queue.add(initial);

		while (!queue.isEmpty()) {
			ArmConfig current = queue.poll();
			explored.add(current);

			if (current.toString().equals(goalState.toString())) {
				path = printPath(goalState);
				return path;
			}
			if (current.adjacencies != null) {
				for (Edge e : current.adjacencies) {
					ArmConfig child = e.target;
					if (!explored.contains(child) && !queue.contains(child)) {
						child.parent = current;
						queue.add(child);
					}
				}
			}
		}
		return path;
	}

	public static List<ArmConfig> printPath(ArmConfig target) {
		List<ArmConfig> path = new ArrayList<ArmConfig>();
		for (ArmConfig node = target; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}

	public ProblemSpec getProblemSpec() {
		return pS;
	}

}
