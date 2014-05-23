package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import objects.Card.CARD_NUMBER;
import objects.SystemManagement.GameStatus;

/**
 * Board of the game with the main deck, the 4 color piles and the 7 piles
 * @author bcreusot
 *
 */
public class Board implements Runnable {

	/**
	 * Sleeping time of the thread in milli
	 */
	private final long sleepTime = 50;
	/**
	 * Status of the game
	 */
	private GameStatus status;
	/**
	 * If the game is loss
	 */
	private boolean loss;
	/**
	 * Debug mode cause the game to print its state at each step
	 */
	private boolean debugMode;
	
	/**
	 * if a move occur during at least one round (draw the entire deck)
	 */
	private boolean moveDuringRound;
	/**
	 * Bruteforce mode  when no move during one round, cause the game to place a card from the display pile to the bottom whenever it can
	 */
	private boolean bruteForceMode;
	
	/**
	 * Number of bottom piles to setup are the begin
	 */
	private int nbOfplayablePiles;
	
	/**
	 * Counter of the number of moves made
	 */
	private int numberOfMoves;
	/**
	 * Collection of the X piles
	 */
	private ArrayList<Pile> playableXPiles;
	/**
	 * Collection of the 4 final piles
	 */
	private HashMap<String,Pile> final4Piles;
	/**
	 * Main deck of the board (HIDE)
	 */
	private Deck deckHide;
	/**
	 * Main displayed pile of the board (DISPLAYED)
	 */
	private Pile pileDisplayed;
	/**
	 * The 4 main spots required to win the game
	 */
	private Pile spadesPile,clubsPile,heartsPile,diamondsPile;
	
	/**
	 * Running thread
	 */
	private Thread thread;
	/**
	 * Name of the board, must be unique if multiples boards are run
	 */
	private String name;
	
	/**
	 * Main class of the game, represent the board
	 * @throws Exception If wrong number of playables piles set up
	 */
	public Board(int pileNumber, String name) throws Exception{
		this.name              = name;
		this.nbOfplayablePiles = pileNumber;
		//Check if the number of Piles doesn't excess the number of cards in the deck;
		int sum                = 0;
		for(int i = 0 ; i < nbOfplayablePiles ; i++){
			sum+=i+1;
			if(sum > Deck.deckSize)
				throw new Exception("Number of pile(" + nbOfplayablePiles+") to high!");
		}
		
		
		this.reset();
	}
	
	/**
	 * Reset all the vars to party again!
	 */
	public void reset(){
		///------------- VAR INIT -------------///
		this.numberOfMoves	   = 0;
		this.deckHide          = new Deck(true);
		this.pileDisplayed     = new Pile();
		this.final4Piles       = new HashMap<String,Pile>();
		this.playableXPiles    = new ArrayList<Pile>();
		this.loss			   = false;
		this.bruteForceMode	   = false;
		this.debugMode		   = false;
		this.moveDuringRound   = false;
		this.status			   = GameStatus.CREATED;
		
		
		///------------- BOARD INIT ------------///
		/* Creation of the 4 final pile */
		for(int i = 0 ; i < 4 ; i++){
			spadesPile = new Pile();
			this.final4Piles.put("spade",spadesPile);
			
			clubsPile = new Pile();
			this.final4Piles.put("club",clubsPile);
			
			heartsPile = new Pile();
			this.final4Piles.put("heart",heartsPile);
			
			diamondsPile = new Pile();
			this.final4Piles.put("diamond",diamondsPile);
		}
		
		
		/* Creation of the X piles */
		for(int i = 0 ; i < nbOfplayablePiles ; i++){
			//Create pile and store at the end of the list
			Pile pile = new Pile();
			pile.fillPile(this.deckHide, i+1);
			
			this.playableXPiles.add(pile);
		}
		
	}

	/**
	 * Display a board in ASCII art
	 * @return the board to display
	 */
	public String toDisplay(){
		String display = "";
		
		
		
		// ----------------------------- HEADER ------------------------//
		
		String emptyBlockHeader = String.format("%-26s", "|");
		String emptyLineHeader = emptyBlockHeader + emptyBlockHeader + emptyBlockHeader + emptyBlockHeader + emptyBlockHeader + "|\n";

		char[] charr = new char[131];
		Arrays.fill(charr, '-');
		String fullLineHeader = new String(charr) + "\n";
		
		
		display += fullLineHeader + emptyLineHeader;
		
		String cardName        = (!this.pileDisplayed.isEmpty()) ? this.pileDisplayed.getFirstVisibleCard().toString():"";
		String cardHeartName   = (!this.heartsPile   .isEmpty()) ? this.heartsPile   .getFirstVisibleCard().toString():"";
		String cardDiamondName = (!this.diamondsPile .isEmpty()) ? this.diamondsPile .getFirstVisibleCard().toString():"";
		String cardSpadeName   = (!this.spadesPile   .isEmpty()) ? this.spadesPile   .getFirstVisibleCard().toString():"";
		String cardClubName    = (!this.clubsPile    .isEmpty()) ? this.clubsPile    .getFirstVisibleCard().toString():"";
		
		
		display += String.format("%-26s", "| (" + this.deckHide.size()     + ") " + cardName)        + 
				   String.format("%-26s", "| (" + this.heartsPile.size()   + ") " + cardHeartName)   + 
				   String.format("%-26s", "| (" + this.diamondsPile.size() + ") " + cardDiamondName) + 
				   String.format("%-26s", "| (" + this.spadesPile.size()   + ") " + cardSpadeName)   + 
				   String.format("%-26s", "| (" + this.clubsPile.size()    + ") " + cardClubName)    +  "|\n";
		
		display += emptyLineHeader;
		
		
		
		// ---------------------------- MAIN -------------------------//
		
		String emptyBlockMain = String.format("%-22s", "|");
		String emptyLineMain = "";
		for(int i = 0 ; i < nbOfplayablePiles ; i++)
			emptyLineMain += emptyBlockMain;
		emptyLineMain += "|\n";

		char[] charrr = new char[22 * nbOfplayablePiles + 1];
		Arrays.fill(charrr, '-');
		String fullLineMain = new String(charrr) + "\n";
		
		
		display += fullLineMain + emptyLineMain;
		
		boolean cardToPrint = true;
		int counterLevel = -1;
		while(cardToPrint){
			cardToPrint = false;
			//Iteration on all the pile
			for(Pile pile : this.playableXPiles){
				//If there is one card at that level
				if(counterLevel == -1){
					display += String.format("%-22s", "| (" + pile.sizeHidden() + ")");
					cardToPrint = true;
				}
				//If there is at least a card in the pile
				else if(pile.sizeVisible() > counterLevel){
					Card card = pile.getVisibleElement(counterLevel);
					display += String.format("%-22s", "| " + card.toString());
					cardToPrint = true;
				}
				else{
					display += emptyBlockMain;
				}
			}
			counterLevel++;

			display += "|\n";
		}

		display += fullLineMain;
		
		return display;
	}
	
	
	/**
	 * Start the game!
	 */
	public void start(boolean debug){
		this.status = GameStatus.RUNNING;
		this.thread = new Thread(this, this.name);
		this.thread.start();
		this.debugMode = debug;
	}
	
	/**
	 * Method automatically launch by  the start() call of the thread
	 */
	@Override
	public void run() {
		System.out.println(this.thread.getName() + "(" + this.thread.getId() + ") -> Starting the game");
		boolean exit = false;
		while(!exit){
//			this.sysManagement.checkThreadMessage(this);
			try {
				Thread.sleep(sleepTime);
				//Clear the display
				playTheGame();
				if(this.debugMode)
					System.out.println(this.toDisplay());
				if(checkEndGame())
					exit=true;
			} catch (InterruptedException e) {
				exit = true;
				this.status = GameStatus.STOPPED;
				e.printStackTrace();
			}
		}
		this.status = GameStatus.STOPPED;
		if(this.loss)
			System.out.print("[" + this.getName() + "] Looser! (");
		else
			System.out.print("[" + this.getName() + "] Winner! (");
		System.out.println(this.numberOfMoves + " moves)");
	}


	/**
	 * Main method, describe the algorithm and how to play and beat the game
	 */
	private void playTheGame() {
		//Placing cards on the board
		
		//------ Browse the bottom piles and execute basic moves ------//
		boolean basicsMovesToDo = true;
		while(basicsMovesToDo){
			basicsMovesToDo = false;
			for(Pile pile : this.playableXPiles){
				//If there is at least one card in the pile
				if(!pile.isEmptyVisible()){
					//------ Browse the bottom AND Final piles ------//
					if(browseAndPlaceBottomPiles(pile) || browseAndPlaceFinalPile(pile)){
						numberOfMoves++;
						basicsMovesToDo = true;
						moveDuringRound = true;
						break;
					}
				}
			}
		}
		
		//------ Draw one card and try to place it in  the finals piles ------//
		//Refill the deck with the display pile
		if(deckHide.size()<1){
			deckHide.refill(this.pileDisplayed);
			//If after one round there in at least one move when reset the var
			if(!moveDuringRound && bruteForceMode){
				loss = true;
				return;
			}
			else if(moveDuringRound)
				moveDuringRound = false;
			//If there is no move during one round the game must be lock, so we activate brutForceMode
			else
				bruteForceMode = true;
		}
		//Draw one card and place it on the display pile
		if(!deckHide.isEmpty()){
			Card card = deckHide.draw();
			this.pileDisplayed.addFirstVisible(card);
			//------ Browse through the one card displayed (activate bruteForceMode if nothing has move from one round, makes every cards to go ever they can from display)------//
			if(browseAndPlaceFinalPile(pileDisplayed) || (bruteForceMode && browseAndPlaceBottomPiles(card,pileDisplayed))){
				numberOfMoves++;
				moveDuringRound = true;
				bruteForceMode = false;
				return;
			}
			
		// -------------- If There is no Basics Moves left, the card on the displayed pile cannot be used --------//
			// ---- We try to place one card in the bottom and then if another card from the bottom goes on it we validate the move ----//
			Pile hypothesisPile = checkDoubleMoveAvalaible(card); 
			if(hypothesisPile != null){
				pileDisplayed.placeOneCard(hypothesisPile, false);
				numberOfMoves++;
				moveDuringRound = true;
			}
		}
		// -------------- If There is really no moves at all, we start digging into the final pile (doesn't change winrate :/) ------------- //
		/*else{
			for(String key : this.final4Piles.keySet()){
				Pile pile = this.final4Piles.get(key);
				if(!pile.isEmpty()){
					Pile hypothesisPile = checkDoubleMoveAvalaible(pile.getFirstVisibleCard()); 
					if(hypothesisPile != null){
						pile.placeOneCard(hypothesisPile, false);
						numberOfMoves++;
						moveDuringRound = true;
					}
				}
			}
		}*/
	}
	/**
	 * try to place one card in the bottom and then if another card from the bottom goes on it we validate the move
	 * @param card the card we try to place
	 */
	private Pile checkDoubleMoveAvalaible(Card card) {
		for(Pile pile : this.playableXPiles){
			if(card.isPlaceable(pile)){
				for(Pile otherPile : this.playableXPiles){
					if(!otherPile.isEmptyVisible() && otherPile.getLastVisibleCard().isPlaceable(card)){
						return pile;
					}
				}			
			}
		}
		return null;
	}


	/**
	 * Browse through the final pile and place One card if possible
	 * @param pile the pile where the card had to be placed
	 * @return a card has been placed
	 */
	private boolean browseAndPlaceFinalPile(Pile pile){
		Card card = pile.getFirstVisibleCard();
		for(String finalPileString : this.final4Piles.keySet()){
			Pile finalPile = this.final4Piles.get(finalPileString);
			if(card.isPlaceable(finalPile,finalPileString)){
//				System.out.println(card + " placed on " + finalPileString + " pile");
				pile.placeOneCard(finalPile,true);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Browse through the bottom pile and switch the piles of cards if possible
	 * @param pile the current checked pile
	 * @return a card has been placed
	 */
	private boolean browseAndPlaceBottomPiles(Pile pile){
		Card card = pile.getLastVisibleCard();
		for(Pile otherPile : this.playableXPiles){
			//If the card is placable and IF IT'S NOT A KING ON A EMPTY HIDDEN CARD SPOT (Infinity loooooop)
			if(card.isPlaceable(otherPile) && !(card.getNumber() == CARD_NUMBER.KING && pile.isEmptyHidden())){
//				System.out.println(card + " placed on pile " + (this.playableXPiles.indexOf(otherPile)+1));
				pile.placeSomeCards(otherPile);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Browse through the bottom pile and place the card form the displayedPile if possible
	 * @param displayedCard the card to place from the displayed pile
	 * @param pile the current checked pile
	 * @return a card has been placed
	 */
	private boolean browseAndPlaceBottomPiles(Card displayedCard, Pile pile){
		for(Pile otherPile : this.playableXPiles){
			//If the card is placable
			if(displayedCard.isPlaceable(otherPile)){
//				System.out.println(displayedCard + " placed on pile " + (this.playableXPiles.indexOf(otherPile)+1));
				pile.placeOneCard(otherPile,false);
				return true;
			}
		}
		return false;
	}

	public Thread getThread() {
		return thread;
	}
	public String getName(){
		return this.name;
	}
	
	/**
	 * Check if the game is win or loss
	 * @return state of the game
	 */
	public boolean checkEndGame() {
		if(loss)
			return true;
		
		for(String finalPileString : this.final4Piles.keySet()){
			Pile finalPile = this.final4Piles.get(finalPileString);
			if(finalPile.size()<13)
				return false;
		}
		return true;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public boolean getLoss() {
		return this.loss;
	}
	public int getNbMoves() {
		return this.numberOfMoves;
	}
	
	
}







