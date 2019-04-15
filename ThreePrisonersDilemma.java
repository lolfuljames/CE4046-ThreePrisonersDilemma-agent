public class ThreePrisonersDilemma {
	
	/* 
	 This Java program models the two-player Prisoner's Dilemma game.
	 We use the integer "0" to represent cooperation, and "1" to represent 
	 defection. 
	 
	 Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where
	 we give the payoff for the first player in the list. We want the three-player game 
	 to resemble the 2-player game whenever one player's response is fixed, and we
	 also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique ordering
	 
	 U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)
	 
	 The payoffs for player 1 are given by the following matrix: */
	static int wins = 0;
	static int runnerUp = 0;
	static int secondRunnerUp = 0;
	static int[][][] payoff = {  
		{{6,3},  //payoffs when first and second players cooperate 
		 {3,0}}, //payoffs when first player coops, second defects
		{{8,5},  //payoffs when first player defects, second coops
	     {5,2}}};//payoffs when first and second players defect
	
	/* 
	 So payoff[i][j][k] represents the payoff to player 1 when the first
	 player's action is i, the second player's action is j, and the
	 third player's action is k.
	 
	 In this simulation, triples of players will play each other repeatedly in a
	 'match'. A match consists of about 100 rounds, and your score from that match
	 is the average of the payoffs from each round of that match. For each round, your
	 strategy is given a list of the previous plays (so you can remember what your 
	 opponent did) and must compute the next action.  */
	
	
	abstract class Player {
		// This procedure takes in the number of rounds elapsed so far (n), and 
		// the previous plays in the match, and returns the appropriate action.
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			throw new RuntimeException("You need to override the selectAction method.");
		}
		
		// Used to extract the name of this player class.
		final String name() {
			String result = getClass().getName();
			return result.substring(result.indexOf('$')+1);
		}
	}
	
	/* Here are four simple strategies: */
	
	class NicePlayer extends Player {
		//NicePlayer always cooperates
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 0; 
		}
	}
	
	class NastyPlayer extends Player {
		//NastyPlayer always defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 1; 
		}
	}
	
	class RandomPlayer extends Player {
		//RandomPlayer randomly picks his action each time
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (Math.random() < 0.5)
				return 0;  //cooperates half the time
			else
				return 1;  //defects half the time
		}
	}
	
	class TolerantPlayer extends Player {
		//TolerantPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			if (opponentDefect > opponentCoop)
				return 1;
			else
				return 0;
		}
	}
	
	class FreakyPlayer extends Player {
		//FreakyPlayer determines, at the start of the match, 
		//either to always be nice or always be nasty. 
		//Note that this class has a non-trivial constructor.
		int action;
		FreakyPlayer() {
			if (Math.random() < 0.5)
				action = 0;  //cooperates half the time
			else
				action = 1;  //defects half the time
		}
		
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return action;
		}	
	}

	class T4TPlayer extends Player {
		//Picks a random opponent at each play, 
		//and uses the 'tit-for-tat' strategy against them 
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			if (Math.random() < 0.5)
				return oppHistory1[n-1];
			else
				return oppHistory2[n-1];
		}	
	}

	class GT4T extends Player {
		//Picks a random opponent at each play, 
		//and uses the 'tit-for-tat' strategy against them 
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n<=1) return 0; //cooperate by default
			int sum = 0;
			if (Math.random() < 0.5){
				for (int i : oppHistory1)
					sum += i;
				if(sum <= 1) return 0;
				return oppHistory1[n-1];
			}
			else{
				for (int i : oppHistory2)
					sum += i;
				if(sum <= 1) return 0;
				return oppHistory2[n-1];
			}
		}	
	}

	class NaiveProber extends Player {
		//Picks a random opponent at each play, 
		//and uses the 'tit-for-tat' strategy against them 
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			if(Math.random()<0.1) return 1;
			if (Math.random() < 0.5)
				return oppHistory1[n-1];
			else
				return oppHistory2[n-1];
		}	
	}

	class TianShunKenneth_Teo_Player extends Player {
		// A Tolerant Tit for Tat player that considers action of both
		// opponents instead of just looking at one.
	// If opponents are not acting in unison, fall back to being an  
	// alternator
	int selectAction(int n, int[] myHistory, int[] oppHistory1,
			int[] oppHistory2) {
	
			// Cooperate on the first two rounds
			if (n==0 || n==1) {
				return 0;
			} 
			// Defect on the last two rounds
			else if (n==98||n==99) {
				 return 1;  
			}
					
			// If both opponents are nasty, turn nasty as well. Only give
			// two chances before defecting
			if ((oppHistory1[n-1]==1&&oppHistory1[n-2]==1)
				&&(oppHistory2[n-1]==1&&oppHistory2[n-2]==1)) {
				return 1;
			}
			// If both opponents in synchronisation, possibly Tit for tat, 
			//   return Tit for tat
			else if (oppHistory1[n-1] == oppHistory2[n-1] 
				&& oppHistory1[n-2] == oppHistory2[n-2]) {
				return oppHistory1[n-1];
			}
			// Opponents not acting in unision, alternate between 0 and 1
			else {
				if(myHistory[n-1] == 1) {
					return 0;
				} else {
					return 1;
				}
			}
		}
	}
	

	class tan_JamesCheeMin extends Player {
		double NASTY_THRESHOLD = 0.7;
		boolean nasty = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//Get the initial gold mine
			if (n==0) return 0;

			//Someone defects previously in rounds 1-4
			if (n<=3 && (oppHistory1[n-1] + oppHistory2[n-1]) >= 1) return 1;
			
			/*
			 * Once nasty player is found, turn into another nasty player
			 * If not found, recheck after 10 rounds
			 */
			if(n%10 == 0){
				double sum = 0;
				for (int i : oppHistory1)
					sum += i;
				if(!nasty && (double)sum/n >= NASTY_THRESHOLD) nasty = true;
				sum = 0;
				for (int i : oppHistory2)
					sum += i;
				if(!nasty && (double)sum/n >= NASTY_THRESHOLD) nasty = true;
			}
			if(nasty) return 1;

			/*
			 * If only 1 of the agents defected previously, time for my agent to defect
			 */
			if((oppHistory1[n-1] + oppHistory1[n-1] + myHistory[n-1]) == 1) return 1;

			// Cooperate by default
			return 0;
		}
	}

	class Bummer extends NastyPlayer {

        //Count the number of defects by opp
        int intPlayer1Defects = 0;
        int intPlayer2Defects = 0;

        //Store the round where agent retaliate against defects
        int intRoundRetailate = -1;

        //Number of rounds where agent coop to observer opp actions
        int intObservationRound = 1;

        //Number of rounds where agent retaliate defects with defects
        //After this round, see opp actions to check if they decide to coop again
        int intGrudgeRound = 3;

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            //Record Defects count
            if (n > 0) {
                intPlayer1Defects += oppHistory1[n - 1];
                intPlayer2Defects += oppHistory2[n - 1];
            }

            //Start by cooperating
            if (n < intObservationRound) {
                return 0; //cooperate by default
            }

            //Loop rounds where agent coop to reverse the effects of retaliation
            if (intRoundRetailate < -1) {
                intRoundRetailate += 1;
                intPlayer1Defects = 0;
                intPlayer2Defects = 0;

                return 0;
            }

            //Check at round retaliated + threshold to measure if opp wishes to coop again
            if (intRoundRetailate > -1 && n == intRoundRetailate + intGrudgeRound + 1) {

                //Count the number of coop during retaliate round to check opp coop level
                int intPlayer1Coop = 0;
                int intPlayer2Coop = 0;

                for (int intCount = 0; intCount < intGrudgeRound; intCount++) {
                    intPlayer1Coop += oppHistory1[n - 1 - intCount] == 0 ? 1 : 0;
                    intPlayer2Coop += oppHistory2[n - 1 - intCount] == 0 ? 1 : 0;
                    //intPlayer1Coop += oppHistory1[n - 1 - intCount] == 1 ? 1 : 0;
                    //intPlayer2Coop += oppHistory2[n - 1 - intCount] == 1 ? 1 : 0;
                }

                //If both players wish to coop again, start to coop with them
                if (intPlayer1Coop > 1 && intPlayer2Coop > 1 && (oppHistory1[n - 1] + oppHistory2[n - 1]) == 0) {
                    //Hold round where agent coop to show intention to coop again
                    //Count backwards from -2
                    //-2 indicates 1 round where agent coop to reverse effect of retailation
                    //-5 indicates 4 rounds where agent coop to reverse effect
                    intRoundRetailate = -2;

                    intPlayer1Defects = 0;
                    intPlayer2Defects = 0;

                    return 0;
                } else {
                    intRoundRetailate = n;
                    return 1;
                }

            }

            //Punish Defection by defecting straight away
            //Stores the round defected
            if (intPlayer1Defects + intPlayer2Defects > 0) {
                intRoundRetailate = n;
                return 1;
            }

            //Coop as default action
            return 0;
        }
    }

    /* For King Chody */

 /* Gosu the Minion -- Note: Gosu the Minion is a NicePlayer */
    class GosuTheMinion extends NicePlayer {

        // For tracking Defect/Cooperate probabilities
        private double opp1Def = 0;
        private double opp2Def = 0;

        // Thresholds
        private static final double FRIENDLY_THRESHOLD = 0.850;
        private static final double DEFENSIVE_THRESHOLD = 0.750;

        /* ALL HAIL KING CHODY!! */
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            // Start by cooperating
            if (n == 0) {

                return 0;
            }

            // Calculate probability for Def/Coop (Opponent 1)
            opp1Def += oppHistory1[n - 1];
            double opp1DefProb = opp1Def / oppHistory1.length;
            double opp1CoopProb = 1.000 - opp1DefProb;

            // Calculate probability for Def/Coop (Opponent 2)
            opp2Def += oppHistory2[n - 1];
            double opp2DefProb = opp2Def / oppHistory2.length;
            double opp2CoopProb = 1.000 - opp2DefProb;

            /*System.out.printf("Opponent 1: %.3f, %.3f, Opponent 2: %.3f, %.3f%n",
					opp1CoopProb, opp1DefProb, opp2CoopProb, opp2DefProb);*/
            if (opp1CoopProb >= FRIENDLY_THRESHOLD
                    && opp2CoopProb >= FRIENDLY_THRESHOLD
                    && oppHistory1[n - 1] == 0
                    && oppHistory2[n - 1] == 0) {

                // Good chance that both opponents will cooperate
                // Just cooperate so that everyone will be happy
                return 0;

            } else if ((opp1DefProb >= DEFENSIVE_THRESHOLD || opp2DefProb >= DEFENSIVE_THRESHOLD)
                    && (oppHistory1[n - 1] == 1 || oppHistory2[n - 1] == 1)) {

                // Given that one of the opponents have been relatively nasty,
                // and one of them has defected in the previous turn,
                // high prob that one of them will defect again,
                // defect to protect myself!
                return 1;

            } else if (n >= 2) {

                // Check if either opponent has defected in the last 2 turns
                if (oppHistory1[n - 1] == 1 || oppHistory2[n - 1] == 1
                        || oppHistory1[n - 2] == 1 || oppHistory2[n - 2] == 1) {

                    // DESTROY them!!
                    return 1;
                } else {

                    // Just be friendly!
                    return 0;
                }
            } else {

                // At this moment, both players are not that friendly,
                // and yet neither of them are relatively nasty.
                // Just be friendly for now.
                return 0;
            }
        }
    }

    /* Gosu the Minion */
    class PM_Low extends Player {

        int myScore = 0;
        int opp1Score = 0;
        int opp2Score = 0;

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            if (n == 0) {
                return 0; // cooperate by default
            }

            // get the recent history index
            int i = n - 1;

            // add up the total score/points for each player
            myScore += payoff[myHistory[i]][oppHistory1[i]][oppHistory2[i]];
            opp1Score += payoff[oppHistory1[i]][oppHistory2[i]][myHistory[i]];
            opp2Score += payoff[oppHistory2[i]][myHistory[i]][oppHistory1[i]];

            // if my score is lower than the any of them
            // it means that at least one of them have defected
            if (myScore >= opp1Score && myScore >= opp2Score) {

                // cooperate if my score is higher or equal than all of them
                return 0;
            }

            return 1; // defect if my score is lower than any of them
        }
    }

	class HardProber extends Player{
		boolean nasty = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0) return 1;
			if(n==1) return 1;
			if(n==2) return 0;
			if(n==3) return 0;
			if(oppHistory1[1] == 0 && oppHistory1[2] == 0 || oppHistory2[1] == 0 && oppHistory2[2] == 0) nasty = true;
			if(nasty) return 1;
			if (Math.random() < 0.5) return oppHistory1[n-1];
			else return oppHistory2[n-1];
		}
	}
	
	class Tideman extends T4TPlayer{
		int defect_counter = 0;
		int defect_queue = 0;
		int restart_turn = 0;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0) return 0;
			if(oppHistory1[n-1] + oppHistory2[n-1] > 0)  {
				defect_counter++;
				if(defect_queue == 0) defect_queue = defect_counter;
				else defect_queue++;
			}
			if(defect_queue > 0){
				defect_queue--;
				return 1;
			}
			if(n - restart_turn > 20 && n <= 90) {
				defect_counter = 0;
				restart_turn = n;
			}
			return super.selectAction(n, myHistory, oppHistory1, oppHistory2);
		}
	}
	/* In our tournament, each pair of strategies will play one match against each other. 
	 This procedure simulates a single match and returns the scores. */
	float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {
		int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
		float ScoreA = 0, ScoreB = 0, ScoreC = 0;
		boolean verbose = false;
		// System.out.println("Starting match between " + A.name() + ", " + B.name() + " and " + C.name());
		for (int i=0; i<rounds; i++) {
			int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
			int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
			int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);
			ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
			ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
			ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];
			HistoryA = extendIntArray(HistoryA, PlayA);
			HistoryB = extendIntArray(HistoryB, PlayB);
			HistoryC = extendIntArray(HistoryC, PlayC);
			if (verbose)
				System.out.println(A.name() + " scored " + payoff[PlayA][PlayB][PlayC] +
						" points, " + B.name() + " scored " +  payoff[PlayB][PlayC][PlayA] + 
						" points, and " + C.name() + " scored " + payoff[PlayC][PlayA][PlayB] + " points.");
		}
		float[] result = {ScoreA/rounds, ScoreB/rounds, ScoreC/rounds};
		return result;
	}
	
//	This is a helper function needed by scoresOfMatch.
	int[] extendIntArray(int[] arr, int next) {
		int[] result = new int[arr.length+1];
		for (int i=0; i<arr.length; i++) {
			result[i] = arr[i];
		}
		result[result.length-1] = next;
		return result;
	}
	
	/* The procedure makePlayer is used to reset each of the Players 
	 (strategies) in between matches. When you add your own strategy,
	 you will need to add a new entry to makePlayer, and change numPlayers.*/
	
	int numPlayers = 15;
	Player makePlayer(int which) {
		switch (which) {
		case 0: return new tan_JamesCheeMin();
		case 1: return new GosuTheMinion();
		case 2: return new PM_Low();
		case 3: return new Bummer();
		case 4: return new HardProber();
		case 5: return new Tideman();
		case 6: return new GT4T();
		case 7: return new NaiveProber();
		case 8: return new TianShunKenneth_Teo_Player();
		case 9: return new TolerantPlayer();
		case 10: return new NicePlayer();
		case 11: return new FreakyPlayer();
		case 12: return new NastyPlayer();
		case 13: return new RandomPlayer();
		case 14: return new T4TPlayer();
		}
		throw new RuntimeException("Bad argument passed to makePlayer");
	}
	
	/* Finally, the remaining code actually runs the tournament. */
	
	public static void main (String[] args) {
		ThreePrisonersDilemma instance = new ThreePrisonersDilemma();
		for(int count = 0; count < 10000; count++){
			instance.runTournament();
			if(count%1000 == 0) System.out.println("Currently at round: " + count);
		}
		System.out.println("Total wins for James: " + wins);
		System.out.println("Total 2nd places for James: " + runnerUp);
		System.out.println("Total 3rd places for James: " + secondRunnerUp);
	}
	
	boolean verbose = false; // set verbose = false if you get too much text output
	
	void runTournament() {
		float[] totalScore = new float[numPlayers];

		// This loop plays each triple of players against each other.
		// Note that we include duplicates: two copies of your strategy will play once
		// against each other strategy, and three copies of your strategy will play once.

		for (int i=0; i<numPlayers; i++) for (int j=i; j<numPlayers; j++) for (int k=j; k<numPlayers; k++) {

			Player A = makePlayer(i); // Create a fresh copy of each player
			Player B = makePlayer(j);
			Player C = makePlayer(k);
			int rounds = 90 + (int)Math.rint(20 * Math.random()); // Between 90 and 110 rounds
			float[] matchResults = scoresOfMatch(A, B, C, rounds); // Run match
			totalScore[i] = totalScore[i] + matchResults[0];
			totalScore[j] = totalScore[j] + matchResults[1];
			totalScore[k] = totalScore[k] + matchResults[2];
			if (verbose)
				System.out.println(A.name() + " scored " + matchResults[0] +
						" points, " + B.name() + " scored " + matchResults[1] + 
						" points, and " + C.name() + " scored " + matchResults[2] + " points.");
		}
		int[] sortedOrder = new int[numPlayers];
		// This loop sorts the players by their score.
		for (int i=0; i<numPlayers; i++) {
			int j=i-1;
			for (; j>=0; j--) {
				if (totalScore[i] > totalScore[sortedOrder[j]]) 
					sortedOrder[j+1] = sortedOrder[j];
				else break;
			}
			sortedOrder[j+1] = i;
		}
		
		// Finally, print out the sorted results.
		if (verbose) System.out.println();
		// System.out.println("Tournament Results");
		// for (int i=0; i<numPlayers; i++) 
		// 	System.out.println(makePlayer(sortedOrder[i]).name() + ": " 
		// 		+ totalScore[sortedOrder[i]] + " points.");

		if(makePlayer(sortedOrder[0]).name().equals("tan_JamesCheeMin")) {
			wins++;
		}
		if(makePlayer(sortedOrder[1]).name().equals("tan_JamesCheeMin")) {
			runnerUp++;
		}
		if(makePlayer(sortedOrder[2]).name().equals("tan_JamesCheeMin")) {
			secondRunnerUp++;
		}
	} // end of runTournament()
	
} // end of class PrisonersDilemma

