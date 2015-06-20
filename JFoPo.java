// Foreign Policy Decision Making Model 
// Joy Hill
// Advisor: Greg Marfleet
// Git Repository can be found at: https://github.com/joyvgh/ForeignPolicy

// TO RUN: javac FoPo.java
//         java FoPo

import java.lang.*;
import java.util.*;


public class JFoPo {
	//Global Data for the world in which the agents live. 
	private int numagent;
	private ArrayList<Exec> ExecList;
	private ArrayList<Legislator> LegList;
	private ArrayList<Advisor> AdvisorList;
	private ArrayList<Agent> AgentList;
	private int numInst;
	private ArrayList<Double> success;
	private ArrayList<ArrayList<Integer>> relationships;

	//Constructor for the world. Nothing practical happens here: capacities for
	//each list are set up with dummy for loops.
	public JFoPo(int numExec, int numLeg, int numAdvisor) {
		numagent = numExec + numLeg + numAdvisor;
		ExecList = new ArrayList<Exec>(numExec);
		for(int i = 0; i < numExec; i++) {
			Exec x = new Exec(new int[2], 0, 0);
			ExecList.add(x);
		}
		LegList = new ArrayList<Legislator>(numLeg);
		for(int i = 0; i < numLeg; i++) {
			Legislator x = new Legislator(new int[2], 0, 0);
			LegList.add(x);
		}
		AdvisorList = new ArrayList<Advisor>(numAdvisor);
		for(int i = 0; i < numAdvisor; i++) {
			Advisor x = new Advisor(new int[2], 0, 0);
			AdvisorList.add(x);
		}
		AgentList = new ArrayList<Agent>(numagent);
		for(int i = 0; i < numagent; i++) {
			Agent x = new Agent(new int[2], 0, 0);
			AgentList.add(x);
		}
		success = new ArrayList<Double>();
		relationships = new ArrayList<ArrayList<Integer>>(numagent);
		for (int i = 0; i < numagent; i++) {
			relationships.add(new ArrayList<Integer>(numagent));
		}
	}

	//Testing methods for ease of reading:
	//Returns an agent given the agent's ID.
	public Agent getAgent(int id){
		return AgentList.get(id);
	}
	//Prints the relationship matrix for every agent.
	public void printRelationships() {
		for (int i = 0; i < AgentList.size(); i++) {
			System.out.println(relationships.get(i));
		}
	}
	//Prints the agentset.
	public void printAgents() {
		for (int i = 0; i < AgentList.size(); i++) {
			System.out.println(getAgent(i));
		}
	}


	//Initializes the agentset. Separate from the FoPo constructor
	//in response to some self-referential problems in initialization. 
	public void initialize(){
		int total = 0; //total number of agents

		//Initialize the executive branch. Each get an opinion made up 
		//of a 2-element array called "o" representing a point on the 
		//coordinate plane and a randomly generated persuasiveness level between
		//0 and 25. 
		for (int i = 0; i < ExecList.size(); i++) {
			int[] o = new int[2];
			o[0] = (int)(Math.random() * 200 - 100);
			o[1] = (int)(Math.random() * 200 - 100);
			int p = (int)(Math.random() * 25);
			Exec x = new Exec(o, p, i);
			ExecList.set(i, x);
			AgentList.set(i, x);
		}
		total += ExecList.size();

		//Initialize the Legislative branch. Functions much the same as the Exec.
		for (int i = 0; i < LegList.size(); i++) {
			int[] o = new int[2];
			o[0] = (int)(Math.random() * 200 - 100);
			o[1] = (int)(Math.random() * 200 - 100);
			int p = (int)(Math.random() * 25);
			Legislator x = new Legislator(o, p, i + total);
			LegList.set(i, x);
			AgentList.set(i + total, x);
		}
		total += LegList.size();

		//For the Japanese model, Advisors will be seen just as a different rank of other
		//agents.
		for (int i = 0; i < AdvisorList.size(); i++) {
			int[] o = new int[2];
			o[0] = (int)(Math.random() * 200 - 100);
			o[1] = (int)(Math.random() * 200 - 100);
			int p = (int)(Math.random() * 25);
			Advisor x = new Advisor(o, p, i + total);
			AdvisorList.set(i, x);
			AgentList.set(i + total, x);
		}


		//Initializing relationships, but only of those of the same rank as the indiv.
		for (int i = 0; i < AgentList.size(); i++) {
			for (int j = 0; j < AgentList.size(); j++) {
				int rel = (int) (Math.random() * 10 + 5);
				if (AgentList.get(j).getRank() == AgentList.get(i).getRank())
					relationships.get(i).add(j, rel);
				else
					relationships.get(i).add(j, 0);
			}
		}

		//Separately, liaison relationships will be added. A randomly selected pair
		//from adjacent ranks will be given a high relationship.
		int execToLeg = ExecList.size() - 1;
		int legToExec = ExecList.size();
		int legToAdv = ExecList.size() + LegList.size() - 1;
		int advToLeg = ExecList.size() + LegList.size();
		relationships.get(execToLeg).add(legToExec, 20);
		relationships.get(legToAdv).add(advToLeg, 20);
	}



	//An agent: has friends, an action set, an opinion, and a persuasiveness level
	public class Agent{
		int[] opinion;
		int persuasiveness;
		int id;

		//The agent's constructor. Takes the given values in the initialize method
		//and constructs and Agent object. 
		public Agent(int[] o, int p, int i) { 
			opinion = o;
			persuasiveness = p;
			id = i;
		}

		//Converts an agent to a string of data to be read in the terminal
		public String toString() {
			String toReturn = "ID: " + id + ", Rank: " + this.getRank() + ", Opinion: [" + opinion[0] + ", " + 
				opinion[1] + "], Persuasiveness: " + persuasiveness;
			return toReturn;
		}

		//How each agent acts during the interaction period once it has 
		//chosen its target
		public void action(Agent target) {
			int interaction = 0; //how well the interaction is going
			// Debate period
			for (int i = 0; i < 5; i++) {
				int noise = (int)(Math.random() * 2 - 1); //generate number between -1 and 1
				//Find distance between the agent and the target
				double distance = Math.sqrt(Math.pow(opinion[0] - opinion[1], 2) + 
					Math.pow(target.opinion[0] - target.opinion[1], 2));
				//subtract persuasiveness and the relationship level, add noise. 
				distance += noise;
				distance -= persuasiveness;
				distance -= relationships.get(id).get(target.id);

				//update interaction value
				if (distance < 20) {
					interaction += 5;
				}
				else if (distance < 50) {
					interaction += 3;
				}
				else if (distance > 100) {
					interaction -= 2;
				}
				if (distance > 70) {
					interaction -= 1;
				}
			}
			// If successful, target's opinion moves closer to current agent's
			if (interaction > 0) {
				//int rel = relationships.get(id).get(target.id) + 1;
				//relationships.get(id).set(target.id, rel);
				target.opinion[0] = (opinion[0] + target.opinion[0]) / 2; 
				target.opinion[1] = (opinion[1] + target.opinion[1]) / 2;
			}
			// If unsuccessful, opinions do not change
		}

		//How each agent chooses a target.
		public int chooseTarget() {
			//Get a list of the agents
			List<Integer> myFriends = relationships.get(id);
			int target;

			//pick a target
			while (0 < 1) {
				target = (int)(Math.random() * myFriends.size());
				if (relationships.get(id).get(target) > 0)
					break;
			}
			
			return target;
		}

		// A placeholder. Rank is something used to evaluate the hierarchy
		// Because the class Agent is a superclass for the other types of agent
		// "10" is just a dummy number for error-checking purposes.
		public int getRank() {
			return 10;
		}
	}


	//Hierarchy, but all subclasses of Agent.
	public class Exec extends Agent{
		public Exec(int[] o, int p, int i) {
			super(o, p, i);
		}
		public int getRank(){
			return 0;
		}
	} 
	public class Legislator extends Agent{
		public Legislator(int[] o, int p, int i) {
			super(o, p, i);
		}
		public int getRank(){
			return 1;
		}
	}
	public class Advisor extends Agent{
		int institution;
		public Advisor(int[] o, int p, int i) {
			super(o, p, i);
		}
		public int getRank(){
			return 2;
		}
	} 
	public class Lobbyist extends Agent{
		int organization;
		public Lobbyist(int[] o, int p, int i, int org) {
			super(o, p, i);
			organization = org;
		}
		public int getRank(){
			return 3;
		}
	} 


	// public void updateInst() {
	// 	for (int i = 0; i < AdvisorList.size(); i++) {
	// 		Advisor x = AdvisorList.get(i);
	// 		int inst = x.institution;
	// 		int[] advop = x.opinion;
	// 		int[] instop = InstOpinions.get(inst);
	// 		advop[0] = (advop[0] + instop[0]) / 2; 
	// 		advop[1] = (advop[1] + instop[1]) / 2;
	// 	}
	// }
	// public void updateOrg() {
	// 	for (int i = 0; i < LobbyList.size(); i++) {
	// 		Lobbyist x = LobbyList.get(i);
	// 		int org = x.organization;
	// 		int[] lobop = x.opinion;
	// 		int[] orgop = OrgOpinions.get(org);
	// 		lobop[0] = (lobop[0] + orgop[0]) / 2; 
	// 		lobop[1] = (lobop[1] + orgop[1]) / 2;
	// 	}
	// }
	public void updatePersuasiveness(int[] OPT) {
		for (int i = 0; i < AgentList.size(); i++) {
			Agent x = AgentList.get(i);
			int[] op = x.opinion;
			double distance = Math.sqrt(Math.pow(OPT[0] - OPT[1],2) + Math.pow(op[0] - op[1], 2));
			
			//persuasiveness modifier
			int perMod = 0;
			if (distance <= 10) {
				perMod = 50;
			}
			else if (distance > 10 && distance <= 50) {
				perMod = 30;
			}
			else if (distance > 50 && distance <= 100) {
				perMod = 20;
			}
			else if (distance > 100 && distance <= 150) {
				perMod = 0;
			}
			else if (distance > 150 && distance <= 200) {
				perMod = -20;
			}
			else if (distance > 200 && distance <= 250) {
				perMod = -30;
			}
			else if (distance > 250) {
				perMod = -50;
			}

			x.persuasiveness = x.persuasiveness + perMod;
		}
	}

	//A single run of the model: for a given number of 
	//Generations and Turns, run the interactions and 
	//Finally, have the President make a decision
	public int[] run(int numGens, int numTurns) {
		// generate optimal action
		int[] OPT = new int[2];
		OPT[0] = (int)(Math.random() * 200 - 100); 
		OPT[1] = (int)(Math.random() * 200 - 100);
		int[] DEC = new int[2];
		DEC[0] = (int)(Math.random() * 200 - 100); 
		DEC[1] = (int)(Math.random() * 200 - 100);
		//System.out.println("[" + OPT[0] + ", " + OPT[1] + "]");
		//for each generation
		for (int i = 0; i < numGens; i++) {


			AgentList.get(0).opinion[0] = DEC[0];
			AgentList.get(0).opinion[1] = DEC[1];

			//run interactions
			for (int j = 0; j < numTurns; j++) {
				run_interactions();
			}
			// make decision
			DEC = makeDecision();

			//check against optimal decision
			double distance = Math.sqrt(Math.pow(OPT[0] - OPT[1],2) + Math.pow(DEC[0] - DEC[1], 2));

			//log the success value in the list
			success.add(distance);

			//TO DO: Recalibrate members of orgs and institutions and update persuasiveness level
			//updateInst();
			//updateOrg();
			updatePersuasiveness(OPT);
		}
		return OPT;
	}


	//For every agent, choose a target and act upon them.
	public void run_interactions() {
		for (int i = AgentList.size() - 1; i >= 0; i--) {
			int target = AgentList.get(i).chooseTarget();
			AgentList.get(i).action(AgentList.get(target));
		}
	}


	//Prints the success list in csv format for testing purposes.
	public void success_for_csv() {
		String toReturn = success.toString();
		toReturn = toReturn.substring(1, toReturn.length() - 2);
		System.out.println(toReturn);
	}

	//Returns the success list.
	public ArrayList<Double> get_success() {
		return success;
	}

	//Returns an opinion in String form
	public String printable_op(int[] op){
		String toRet = "[" + op[0] + "-" + op[1] + "]";
		return toRet;
	}



	//The President makes the final decision by adding noise to
	//their current opinion and returning that.
	public int[] makeDecision() {
			int[] decision = ExecList.get(0).opinion;
			int xnoise = (int)(Math.random() * 20 - 10);
			int ynoise = (int)(Math.random() * 20 - 10);
			decision[0] += xnoise;
			decision[1] += ynoise;
			return decision;
	}

	public String printFactors(int numExec, int numLeg, int numAdvisor, int numLobby, int numInst, int numOrg, double success_diff, int gens, int turns) {
		String toRet = numExec + ", " + numLeg + ", " + numAdvisor + ", " + numLobby + ", " + 
			numInst + ", " + numOrg + ", " + success_diff +
			", " + gens + ", " + turns + "\n";
		return toRet;
	}


	/* 
	 * The Main Method. Program executes from this method. 
	 */
	public static void main(String[] args) {
		//public JFoPo(int numExec, int numLeg, int numAdvisor, int numLobby, int numInst, int numOrg)
		//String toPrint = "numExec, numLeg, numAdvisor, numLobby, numInst, numOrg, success_diff, gens, turns \n";
		//JFoPo world = new JFoPo(5,8,12);
		int[] OPT = new int[2];

		JFoPo world = new JFoPo(5,10,15);
		world.initialize();
		OPT = world.run(100, 500);
		world.success_for_csv();


		world = new JFoPo(20,25,30);
		world.initialize();
		OPT = world.run(100, 500);
		world.success_for_csv();	

// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(1,1,120,150,3,3);
// 			world.initialize();
// 			OPT = world.run(5, 5);
// 			toPrint += world.printFactors(1,1,120,150,3,3,  
// 				world.get_success().get(1) - world.get_success().get(0), 5, 5);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(5,8,120,150,20,50);
// 			world.initialize();
// 			OPT = world.run(500, 500);
// 			toPrint += world.printFactors(5,8,120,150,20,50, 
// 				world.get_success().get(1) - world.get_success().get(0), 500, 500);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(50,800,120,15,30,3);
// 			world.initialize();
// 			OPT = world.run(100, 100);
// 			toPrint += world.printFactors(50,800,120,15,30,3,  
// 				world.get_success().get(1) - world.get_success().get(0), 100, 100);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(50,80,120,150,30,30);
// 			world.initialize();
// 			OPT = world.run(5, 5);
// 			toPrint += world.printFactors(50,80,120,150,30,30,  
// 				world.get_success().get(1) - world.get_success().get(0), 5, 5);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(5,80,120,150,30,30);
// 			world.initialize();
// 			OPT = world.run(50, 50);
// 			toPrint += world.printFactors(5,80,120,150,30,30,  
// 				world.get_success().get(1) - world.get_success().get(0), 50, 50);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(1,1,1,1,1,1);
// 			world.initialize();
// 			OPT = world.run(100, 100);
// 			toPrint += world.printFactors(1,1,1,1,1,1,  
// 				world.get_success().get(1) - world.get_success().get(0), 100, 100);
// 		}
// ////////////////////////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////
// 		for (int i = 0; i < 50; i++) {
// 			world = new FoPo(100,100,1,1,1,1);
// 			world.initialize();
// 			OPT = world.run(500, 500);
// 			toPrint += world.printFactors(100,100,1,1,1,1,  
// 				world.get_success().get(1) - world.get_success().get(0), 500, 500);
// 		}
// ////////////////////////////////////////////////////////////////////////////

// 		System.out.println(toPrint);
	}
}