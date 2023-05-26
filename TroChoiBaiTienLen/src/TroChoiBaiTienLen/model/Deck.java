package TroChoiBaiTienLen.model;

import java.util.ArrayList;

public class Deck {

    public static final int MAX_VALUE = 13;
    public static final int MAX_TYPE = 4;
    public static final int MAX_LENGTH = MAX_VALUE * MAX_TYPE;

    public Card[] cards;

    public Deck() {
        cards = new Card[MAX_LENGTH];
        createDeck();
    }

    private void createDeck() {
        int index = 0;
        for (int value = 1; value <= MAX_VALUE; value++) {
            for (int type = 1; type <= MAX_TYPE; type++) {
                cards[index++] = new Card(value, type);
            }
        }
    }

    public void shuffle() {
        int left = Tools.getRandomNumber(1, MAX_LENGTH);
        int right = Tools.getRandomNumber(1, MAX_LENGTH);

        if (left == right || left == MAX_LENGTH || right == MAX_LENGTH) {
            return;
        }

        if (left > right) {
            left += right;
            right = left - right;
            left -= right;
        }

        Card[] leftCards = new Card[left - 1];
        Card[] midCards = new Card[right - left + 1];
        Card[] rightCards = new Card[cards.length - right];
        
        for(int i = 0; i < leftCards.length; i++){
            leftCards[i] = cards[i];
        }
        
        for(int i = 0; i < midCards.length; i++){
            midCards[i] = cards[i + leftCards.length];
        }
        
        for(int i = 0; i < rightCards.length; i++){
            rightCards[i] = cards[i + leftCards.length + midCards.length];
        }
        
        // update.....
        for(int i = 0; i < leftCards.length; i++){
            cards[i] = leftCards[i];
        }
        
        for(int i = 0; i < rightCards.length; i++){
            cards[i + leftCards.length] = rightCards[i];
        }
        
        for(int i = 0; i < midCards.length; i++){
            cards[i + leftCards.length + rightCards.length] = midCards[i];
        }

    }

    public void shuffles(int quantity) {
        int counter = 0;
        while (counter++ < quantity) {
            shuffle();
        }
    }

    public ArrayList<Card[]> deal(int quantity) {
        if (quantity > 4) {
            return null;
        }
        int maxCard = MAX_VALUE;
        ArrayList<Card[]> list = new ArrayList<Card[]>();
        Card[] card;
        for (int i = 1; i <= quantity; i++) {
            card = new Card[maxCard];
            for (int j = 1; j <= maxCard; j++) {
                card[j - 1] = cards[MAX_LENGTH - (i - 1) - (j - 1) * quantity - 1];
            }
            list.add(card);
        }

        return list;
    }
    
    public String getDectString(){
        StringBuffer result = new StringBuffer();
        for(Card c : cards){
            result.append(c.name + "\n");
        }
        return result.toString();
    }

    // TEST
    public static void main(String[] args) {
        Deck deck = new Deck();

        deck.shuffles(52);
    }
}
