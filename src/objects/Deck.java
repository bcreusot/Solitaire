package objects;

import java.util.Collections;
import java.util.LinkedList;


public class Deck {

	final static int deckSize = 52;
	private LinkedList<Card> deck;
	
	/**
	 * Create a new deck
	 * @param shuffled Shuffle the deck after its creation
	 */
	public Deck(boolean shuffled){
		this.deck = new LinkedList<Card>();
		this.generateCards();
		
		if(shuffled)
			this.shuffleDeck();
		
	}

	/**
	 * Fill the deck with card in predefinit order
	 */
	private void generateCards() {
		for(Card.CARD_COLOR color : Card.CARD_COLOR.values()){
			for(Card.CARD_NUMBER name : Card.CARD_NUMBER.values()){
				this.deck.add(new Card(color, name));
			}
		}
	}

	/**
	 * Shuffle the deck in random order
	 */
	public void shuffleDeck(){
		Collections.shuffle(this.deck);
	}

	@Override
	public String toString() {
		String stringCards = "";
		for(Card card : this.deck){
			stringCards += card.toString() + "\n";
		}
		
		return stringCards;
	}
	
	/**
	 * Draw one card from the last card of the deck
	 * @return The drawn card
	 */
	public Card draw(){
		Card card = this.deck.removeLast();
		card.setVisible();
		return card;
	}
	
	public int size(){
		return this.deck.size();
	}

	/**
	 * Place all the displayed pile back in the deck
	 * @param pileDisplayed the displayed pile
	 */
	public void refill(Pile pileDisplayed) {
		while(!pileDisplayed.isEmpty()){
			Card card = pileDisplayed.removeFirstVisible();
			card.setHidden();
			this.deck.add(card);
		}
	}

	public boolean isEmpty() {
		return this.deck.isEmpty();
	}

	
}
