package objects;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Class managing the system, creating and running threads
 * @author bcreusot
 *
 */
public class SystemManagement {

	public enum ThreadMessage {
		PRINT {
			@Override
			public String toString() {
				return "print : Display the current state of the board\n";
			}
		},
		START {
			@Override
			public String toString() {
				return "start : start the current thread\n";
			}
		},
		STATE {
			@Override
			public String toString() {
				return "state : State of the thread\n";
			}
		},
		HELP {
			@Override
			public String toString() {
				return "help : Display avalaibles commands\n";
			}
		},
		KILL {
			@Override
			public String toString() {
				return "kill : execute order 666\n";
			}
		},
		QUIT {
			@Override
			public String toString() {
				return "exit/quit : leave thread, go back to system\n";
			}
		},
		EXIT {
			@Override
			public String toString() {
				return "exit/quit : leave thread, go back to system\n";
			}
		}
		
	}
	
	public enum SysMessage {
		STATE {
			@Override
			public String toString() {
				return "state : Display the current state of the system\n";
			}
		},
		HELP {
			@Override
			public String toString() {
				return "help : Display avalaibles commands\n";
			}
		},
		THREAD {
			@Override
			public String toString() {
				return "thread <number> : access thread <number>\n";
			}
		},
		EXIT {
			@Override
			public String toString() {
				return "exit : exit/quit the program\n";
			}
		},
		QUIT {
			@Override
			public String toString() {
				return "quit : exit/quit the program\n";
			}
		},
		CREATE {
			@Override
			public String toString() {
				return "create <number> <startID> : create threads but do not start them\n"
						+ "\t<number>     : (req) number of threads\n"
						+ "\t<startID>    : (req) startID of the list of thread\n"
						+ "\t<pileNumber> : (opt) number of bottom piles of thoses threads\n";
			}
		},
		START {
			@Override
			public String toString() {
				return "start <number> <startID> : start some threads\n"
						+ "\t<number> : (req) number of threads\n"
						+ "\t<startID> : (req) startID of the list of threads to start\n";
			}
		},
		CREATE_START {
			@Override
			public String toString() {
				return "create_start <number> <startID> : create and start threads\n"
						+ "\t<number>     : (req) number of threads\n"
						+ "\t<startID>    : (req) startID of the list of thread\n"
						+ "\t<pileNumber> : (opt) number of bottom piles of thoses threads\n";

			}
		},
		STATS {
			@Override
			public String toString() {
				return "stats : display statistique about finished games";

			}
		}
		
	}
	
	public enum GameStatus{
		CREATED {
			@Override
			public String toString() {
				return "Created...\n";
			}
		},
		RUNNING {
			@Override
			public String toString() {
				return "Running...\n";
			}
		},
		STOPPED {
			@Override
			public String toString() {
				return "Stopped...\n";
			}
		}
	}
	
	private HashMap<String,Board>   boardStorage;
	private String 					currentThread;
	private final int				numberOfPile = 7;
	
	
	public SystemManagement() {
		this.boardStorage = new HashMap<String,Board>();
		this.currentThread = "";
	}

	/**
	 * Analysis the message type by the use to create/run thread for example
	 * @param message array of command in one line type by the user split by spaces
	 * @return if the system have to quit
	 */
	public boolean systemMessage(String[] message){
		// TODO Auto-generated method stub
		
		try{
			switch(SysMessage.valueOf(message[0].toUpperCase())){
				case STATE:
					System.out.println(toString());
					break;
				case HELP:
					System.out.println(toStringCommandsAvalaibles());
					break;
				case EXIT:
					return true;
				case QUIT:
					return true;
				case CREATE:
					if(message.length >= 3 && isInteger(message[1]+message[2])){
						int createNumber = Integer.parseInt(message[1]);
						int startID 	 = Integer.parseInt(message[2]);
						int pileNumber   = numberOfPile;
						
						if(message.length >=4 && isInteger(message[3]))
							pileNumber   = Integer.parseInt(message[3]);

						createThreads(createNumber,startID,pileNumber);
					}
					else
						System.out.println("Error not enough params or not integers values (see help)");
					break;
				case CREATE_START:
					if(message.length >= 3 && isInteger(message[1]+message[2])){
						int createNumber = Integer.parseInt(message[1]);
						int startID 	 = Integer.parseInt(message[2]);
						int pileNumber   = numberOfPile;
						
						if(message.length >=4 && isInteger(message[3]))
							pileNumber   = Integer.parseInt(message[3]);
						
						createThreads(createNumber,startID,pileNumber);
						startThread (createNumber,startID);
					}
					else
						System.out.println("Error not enough params or not integers values (see help)");
					break;
				case START:
					if(message.length >= 3 && isInteger(message[1]+message[2])){
						int createNumber = Integer.parseInt(message[1]);
						int startID 	 = Integer.parseInt(message[2]);
						startThread(createNumber,startID);
					}
					else
						System.out.println("Error not enough params or not integers values (see help)");
					break;
				case THREAD:
					if(message.length >= 2 && this.boardStorage.containsKey(message[1])){
						this.currentThread = message[1];
					}
					else
						System.out.println("Error not enough params or not existing board (see help)");
					break;
				case STATS:
					System.out.println(displayStats());
					break;
			}
		}
		 catch (IllegalArgumentException e) {
			 System.out.println("Unknown thread command\n");
		}
		
		
		return false;
	}
	
	/**
	 * Display manyyyyyy stats of the program
	 */
	private String displayStats() {
		String  stats  		     = "";
		int     boardCreated     = 0;
		int	    boardRunning     = 0;
		int     boardStopped     = 0;
		int     boardWin         = 0;
		int     boardLoss        = 0;
		float   ratioWin		 = 0;
		int	    nbMoveWin        = 0;
		int	    nbMoveLoss       = 0;
		float   averageMoveWin   = 0;
		float   averageMoveLoss  = 0;
		float   averageMove      = 0;
		int	    maxMoveWin       = 0;
		int	    minMoveWin       = 0;
		int	    maxMoveLoss      = 0;
		int	    minMoveLoss      = 0;
		
		
		
		for(String key : this.boardStorage.keySet()){
			Board board = this.boardStorage.get(key);
			switch(board.getStatus()){
				case CREATED:
					boardCreated++;
					break;
				case RUNNING:
					boardRunning++;
					break;
				case STOPPED:
					boardStopped++;
					if(!board.getLoss()){
						int moves = board.getNbMoves();
						if(maxMoveWin < moves)
							maxMoveWin = moves;
						if(minMoveWin == 0 || minMoveWin > moves)
							minMoveWin = moves;
						
						nbMoveWin += moves;
						boardWin++;
					}
					else{
						int moves = board.getNbMoves();
						if(maxMoveLoss < moves)
							maxMoveLoss = moves;
						if(minMoveLoss == 0 || minMoveLoss > moves)
							minMoveLoss = moves;
						
						nbMoveLoss += moves;
						boardLoss++;
					}
					break;
			}
		}
		if(boardStopped>0){
			ratioWin    = (boardWin*100)/boardStopped;
			averageMove = Math.round((nbMoveLoss + nbMoveWin)/boardStopped);
		}
		if(boardLoss>0)
			averageMoveLoss = Math.round(nbMoveLoss/boardLoss);
		if(boardWin>0)
			averageMoveWin  = Math.round(nbMoveWin/boardWin);
		
		stats = "Statistiques :\n"
				+ "Created : " + boardCreated + "\n"
				+ "Running : " + boardRunning + "\n"
				+ "Stopped : " + boardStopped + "\n"
					+ "\tRatio Win/Games " + ratioWin + "%\n"
					+ "\t" + averageMove + " moves (AVG)\n"
					+ "\t" + boardWin + " wins\n"
						+ "\t\t" + averageMoveWin + " moves (AVG)\n"
						+ "\t\t" + maxMoveWin + " moves (MAX)\n"
						+ "\t\t" + minMoveWin + " moves (MIN)\n"
					+ "\t" + boardLoss + " losses\n"
						+ "\t\t" + averageMoveLoss + " moves (AVG)\n"
						+ "\t\t" + maxMoveLoss + " moves (MAX)\n"
						+ "\t\t" + minMoveLoss + " moves (MIN)\n";
		
		return stats;
	}
	

	/**
	 * Start a series of thread
	 * @param createNumber number of threads to start
	 * @param startID startID of the first thread to start
	 */
	private void startThread (int createNumber, int startID) {
		for(int i = startID ; i < createNumber+startID ; i++){
			if(!this.boardStorage.containsKey(""+i))
				System.out.println("[Thread-Start] Non existing key : "+i);
			else if(this.boardStorage.get(""+i).getStatus() == GameStatus.RUNNING)
				System.out.println("[Thread-Start] Already Running");
			else{
				Board board = this.boardStorage.get(""+i);
				if(board.getStatus() == GameStatus.STOPPED)
					board.reset();
				board.start(false);
				System.out.println("[Thread-Start] "+i+" started!");
			}
		}
	}

	/**
	 * Creation of X threads based on an X bottom piles
	 * @param createNumber number of threads to create
	 * @param startID startID of the first thread to start
	 * @param pileNumber number of bottom piles to create (default 7)
	 */
	private void createThreads(int createNumber, int startID, int pileNumber) {
		for(int i = startID ; i < createNumber+startID ; i++){
			if(this.boardStorage.containsKey(""+i)){
				System.out.println("[Thread-Creation] Existing key : "+i);
			}
			else{
				try {
					this.boardStorage.put(""+i, new Board(pileNumber, ""+ i));
					System.out.println("[Thread-Creation] " + i + " created!");
				} catch (Exception e) {
					System.out.println("[Thread-Creation] Too many piles!!(Max 9)");
				}
			}
		}
	}

	/**
	 * Display state of all the threads
	 */
	@Override
	public String toString(){
		String state = "State of the program :\n";
		state+="Number of Threads : " + this.boardStorage.size() + "\n";
		for(String key : this.boardStorage.keySet()){
			Board board = this.boardStorage.get(key);
			state += board.getName() + " > " + ((board.checkEndGame())? "Stopped...\n" : "Running...\n");
		}
		
		return state;
		
	}

	/**
	 * Check the message received by one board, call every step of one board
	 * @param board the board to check the message
	 */
	public void threadMessage(Board board,String[] message) {
		// TODO Auto-generated method stub
		try{
			switch(ThreadMessage.valueOf(message[0].toUpperCase())){
				case START:
					if(board.getStatus() == GameStatus.RUNNING){
						System.out.println("Already Running...");
						break;
					}
					if(board.getStatus() == GameStatus.STOPPED)
						board.reset();
					board.start(false);
					break;
				case PRINT:
					System.out.println(board.toDisplay());
					break;
				case STATE:
					System.out.println(board.getName() + " > " + board.getStatus());
					break;
				case HELP:
					System.out.println(toStringCommandsAvalaibles());
					break;
				case KILL:
					System.out.println("Killing board...");
					board.getThread().interrupt();
					break;
				case EXIT:
					this.currentThread = "";
					break;
				case QUIT:
					this.currentThread = "";
					break;
			}
		}
		 catch (IllegalArgumentException e) {
			 System.out.println("Unknown thread command\n");
		}
	}
	
	/**
	 * Display the available commands according to the status of the program (in main or in one thread)
	 * @return String to display about the available commands
	 */
	private String toStringCommandsAvalaibles() {
		String commands = "";
		
		if(this.currentThread.equals("")){
			for(SysMessage command : SysMessage.values()){
				commands += command.toString();
			}
		}
		else{
			for(ThreadMessage command : ThreadMessage.values()){
				commands += command.toString();
			}
		}
		return commands;
	}

	/**
	 * Main method to get all the input from the user
	 */
	public void run() {
		Scanner  keyboard  = null;
		boolean  exit      = false;
		String   prompt    = "";
		String[] message;
		
		while(!exit){
			keyboard = new Scanner(System.in);
			prompt = "Sys>" + ((this.currentThread.equals(""))? "" : this.currentThread + "> ");
			System.out.print(prompt);
			message = keyboard.nextLine().split(" ");
			if(this.currentThread.equals(""))
				exit = systemMessage(message);
			else
				threadMessage(this.boardStorage.get(this.currentThread),message);
		}
		keyboard.close();		
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
