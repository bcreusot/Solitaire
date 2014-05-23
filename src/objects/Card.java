package objects;


public class Card {

	public final static int nbColorCards = 13;
	
	public enum CARD_COLOR_GLOBAL{
		BLACK,RED
	}
	
	public enum CARD_COLOR{
		CLUB {
			@Override
			public String toString() {
				return "of clubs";
			}
		},
		SPADE {
			@Override
			public String toString() {
				return "of spades";
			}
		},
		HEART {
			@Override
			public String toString() {
				return "of hearts";
			}
		},
		DIAMOND {
			@Override
			public String toString() {
				return "of diamonds";
			}
		},
	}
	public enum CARD_NUMBER{
		ONE {
			@Override
			public String toString() {
				return "As";
			}
		},
		TWO {
			@Override
			public String toString() {
				return "2";
			}
		},
		THREE {
			@Override
			public String toString() {
				return "3";
			}
		},
		FOUR {
			@Override
			public String toString() {
				return "4";
			}
		},
		FIVE {
			@Override
			public String toString() {
				return "5";
			}
		},
		SIX {
			@Override
			public String toString() {
				return "6";
			}
		},
		SEVEN {
			@Override
			public String toString() {
				return "7";
			}
		},
		EIGHT {
			@Override
			public String toString() {
				return "8";
			}
		},
		NINE {
			@Override
			public String toString() {
				return "9";
			}
		},
		TEN {
			@Override
			public String toString() {
				return "10";
			}
		},
		JACK {
			@Override
			public String toString() {
				return "Jack";
			}
		},
		QUEEN {
			@Override
			public String toString() {
				return "Queen";
			}
		},
		KING {
			@Override
			public String toString() {
				return "King";
			}
		},
	}
	
	private CARD_COLOR color;
	private CARD_NUMBER number;
	private String name;
	private CardState state;
	private enum CardState {HIDDEN, VISIBLE};
	
	
	
	public Card(CARD_COLOR color, CARD_NUMBER number) {
		this.color = color;
		this.number = number;
		this.name = number.toString() + " " + color.toString();
		this.state = CardState.HIDDEN;
	}



	public CARD_COLOR getColor() {
		return color;
	}



	public void setColor(CARD_COLOR color) {
		this.color = color;
	}



	public CARD_NUMBER getNumber() {
		return number;
	}



	public void setNumber(CARD_NUMBER number) {
		this.number = number;
	}



	@Override
	public String toString() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public static int getNbcolorcards() {
		return nbColorCards;
	}

	public boolean isHidden(){
		return this.state == CardState.HIDDEN;
	}
	public boolean isVisible(){
		return this.state == CardState.VISIBLE;
	}

	public void setHidden() {
		this.state = CardState.HIDDEN;
	}
	public void setVisible() {
		this.state = CardState.VISIBLE;
	}

	public CARD_COLOR_GLOBAL getGlobalColor(){
		if(this.color == CARD_COLOR.CLUB || this.color == CARD_COLOR.SPADE)
			return CARD_COLOR_GLOBAL.BLACK;
		return CARD_COLOR_GLOBAL.RED;
	}

	/**
	 * Check if it's possible to place the card on the top of the final pile (one of the four)
	 * @param finalPile the final pile to check (one of the four)
	 * @param finalPileString the key that tell which pile it is (string required cause if there is no card on it...)
	 * @return true if that card can legally be placed, false if not
	 */
	public boolean isPlaceable(Pile finalPile, String finalPileString) {
		//If the pile is empty we leave!
		if(!this.name.contains(finalPileString))
			return false;
		
		int cardNumber = -1;
		if(finalPile.size()>0){
			Card firstCard = finalPile.getFirstVisibleCard();
			cardNumber = firstCard.getNumber().ordinal();
		}
		
		if(this.number.ordinal() -1 == cardNumber)
			return true;
		
		return false;
	}
	
	/**
	 * Check if it's possible to place the card on the top of the pile
	 * @param pile the pile to check (one of the four)
	 * @return true if that card can legally be placed, false if not
	 */
	public boolean isPlaceable(Pile pile) {
		//If the pile is empty we leave!
		
		if(pile.size()<1)
			return this.number == CARD_NUMBER.KING;
		
		Card firstCard = pile.getFirstVisibleCard();
		//Check if the number is OK, Check if this is the opposite color
		if(this.number.ordinal() +1 == firstCard.getNumber().ordinal() && this.getGlobalColor() != firstCard.getGlobalColor())
			return true;
		
		return false;
	}
	
	/**
	 * Check if it's possible to place the card on another card
	 * @param card the card to check
	 * @return true if that card can legally be placed, false if not
	 */
	public boolean isPlaceable(Card card) {
		//Check if the number is OK, Check if this is the opposite color
		if(this.number.ordinal() +1 == card.getNumber().ordinal() && this.getGlobalColor() != card.getGlobalColor())
			return true;
		
		return false;
	}
	
	
}





















