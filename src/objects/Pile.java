package objects;

import java.util.LinkedList;

public class Pile {

	private LinkedList<Card> visiblePile, hiddenPile;
	
	public Pile() {
		super();
		this.visiblePile = new LinkedList<Card>();
		this.hiddenPile = new LinkedList<Card>();
	}
	
	public int size(){
		return this.visiblePile.size() + this.hiddenPile.size();
	}
	
	public int sizeVisible(){
		return this.visiblePile.size();
	}
	
	public int sizeHidden(){
		return this.hiddenPile.size();
	}
	
	public boolean isEmpty(){
		return this.visiblePile.isEmpty() && this.hiddenPile.isEmpty();
	}
	
	public boolean isEmptyVisible(){
		return this.visiblePile.isEmpty();
	}
	
	public boolean isEmptyHidden(){
		return this.hiddenPile.isEmpty();
	}
	

	public LinkedList<Card> getVisiblePile() {
		return visiblePile;
	}

	public LinkedList<Card> getHiddenPile() {
		return hiddenPile;
	}
	
	/**
	 * Call at init, set up by filling all the pile by drawing cards from main deck
	 * @param mainDeck deck to draw from
	 * @param size number of card to draw from the main deck
	 */
	public void fillPile(Deck mainDeck, int size){
		Card card = mainDeck.draw();
		card.setVisible();
		this.visiblePile.add(card);
		for(int i = 0 ; i < size-1 ; i++){
			this.hiddenPile.add(mainDeck.draw());
		}
	}
	
	public Card getFirstVisibleCard(){
		return this.visiblePile.getFirst();
	}
	
	public Card getFirstHiddenCard(){
		return this.hiddenPile.getFirst();
	}
	
	public Card getLastVisibleCard(){
		return this.visiblePile.getLast();
	}
	
	public Card getLastHiddenCard(){
		return this.hiddenPile.getLast();
	}

	public Card getVisibleElement(int counterLevel) {
		return this.visiblePile.get(counterLevel);
	}

	public Card removeFirstVisible() {
		return this.visiblePile.remove();
	}

	public void addFirstVisible(Card card) {
		this.visiblePile.addFirst(card);
	}

	
	/**
	 * Place all the pile of visible card from one pile to another
	 * @param pileTo the pile where the card will be placed
	 */
	public void placeSomeCards(Pile pileTo) {
		
		while(!this.visiblePile.isEmpty()){
			//Remove the card and place if on the other pile
			pileTo.addFirstVisible(this.visiblePile.removeLast());
		}
		//Reveal the next card from the hidden pile
		if(!this.hiddenPile.isEmpty()){
			Card newCard = this.hiddenPile.removeLast();
			newCard.setVisible();
			this.visiblePile.addFirst(newCard);
		}
	}
	/**
	 * Place the first card of the pile to an other one
	 * @param finalPile the final pile where the card will be placed
	 * @param draw if we gotta draw
	 */
	public void placeOneCard(Pile finalPile, boolean draw) {
		finalPile.addFirstVisible(this.visiblePile.removeFirst());
		
		//Reveal the next card from the hidden pile
		if(draw && !this.hiddenPile.isEmpty() && this.visiblePile.isEmpty()){
			Card newCard = this.hiddenPile.removeLast();
			newCard.setVisible();
			this.visiblePile.addFirst(newCard);
		}
	}

}















