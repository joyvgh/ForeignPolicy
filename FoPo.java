import java.lang.*;
import java.util.*;


public class FoPo {
	private int numagent;
	private ArrayList<Exec> ExecList;
	private ArrayList<Legislator> LegList;
	private ArrayList<Advisor> AdvisorList;
	private ArrayList<Lobbyist> LobbyList;
	private Agent[][] AgentMatrix;
	private int[] InstitutionList;
	private int[] OrganizationList;
	private ArrayList<Double> success;


	public FoPo(int numExec, int numLeg, int numAdvisor, int numLobby, int numInst, int numOrg) {
		numagent = numExec + numLeg + numAdvisor + numLobby;
		ExecList = new ArrayList<Exec>(numExec);
		LegList = new ArrayList<Legislator>(numLeg);
		AdvisorList = new ArrayList<Advisor>(numAdvisor);
		LobbyList = new ArrayList<Lobbyist>(numLobby);
		AgentMatrix = new Agent[numagent][numagent];
		InstitutionList = new int[numInst];
		OrganizationList = new int[numOrg];
		success = new ArrayList<Double>();
	}

	public class Agent{
		Agent[] friends;
		List<String> actions;
		int[] opinion;
		int persuasiveness;
		public Agent() {

		}
	}

	public class Exec extends Agent{
		public Exec() {
		}
		public int[] makeDecision() {
			int[] decision = new int[2];
			decision[0] = 5;
			decision[1] = 5;
			return decision;
		}
	} 
	public class Legislator extends Agent{

		public Legislator() {
		}
	} 
	public class Advisor extends Agent{
		String institution;
		public Advisor() {
		}
	} 
	public class Lobbyist extends Agent{
		String organization;
		public Lobbyist() {
		}
	} 


	public void run(int numGens, int numTurns) {
		//for each generation
		for (int i = 0; i < numGens; i++) {
			// generate optimal action
			int[] OPT = new int[2];
			int[] DEC = new int[2];
			OPT[0] = (int)(Math.random() * 200 - 100); 
			OPT[1] = (int)(Math.random() * 200 - 100);
			System.out.println(OPT[0] + ", " + OPT[1]);

			//run interactions
			for (int j = 0; j < numTurns; j++) {
				run_interactions();
			}
			// make decision
			DEC = ExecList.get(0).makeDecision();

			//check against optimal decision
			double distance = Math.sqrt((Math.pow(OPT[0], 2) - Math.pow(DEC[0], 2)) 
				+ (Math.pow(OPT[0], 2) - Math.pow(DEC[0], 2)));
		}
	}

	public void run_interactions() {
		//lobbyists talk to lobbyists, legislators, exec
		//advisors talk to legislators, exec
		//legislators talk to legislators, exec
	}

	public ArrayList<Double> get_success() {
		return success;
	}

	public static void main(String[] args) {
		FoPo world = new FoPo(1,1,1,1,1,1);
		world.run(100, 100);
		System.out.println(world.get_success());
	}
}