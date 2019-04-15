	class Tan_JamesCheeMin_Player extends Player {
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
			if(n%10 == 0 && !nasty){
				double sum = 0;
				for (int i : oppHistory1)
					sum += i;
				if((double)sum/n >= NASTY_THRESHOLD) nasty = true;
				sum = 0;
				for (int i : oppHistory2)
					sum += i;
				if((double)sum/n >= NASTY_THRESHOLD) nasty = true;
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
    
    