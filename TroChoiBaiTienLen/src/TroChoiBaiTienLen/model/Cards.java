package TroChoiBaiTienLen.model;

import TroChoiBaiTienLen.gameplay.GamePlay;
import java.util.ArrayList;

import java.io.Serializable;

public class Cards implements Serializable { // tay bài

    public ArrayList<Card> cards;

    public Cards(Card[] cards) {
        this.cards = new ArrayList<Card>();
        for (Card c : cards) {
            if (c != null) {
                this.cards.add(c);
            }
        }
    }
    
    public ArrayList<Card> getCards(){
        return this.cards;
    }
    
    public Card[] getCardArray(){
        Card[] rs = new Card[this.cards.size()];
        for(int i = 0; i < this.cards.size(); i++){
            rs[i] = this.cards.get(i);
        }
        return rs;
    }

    public String getCardsString() {
        String result = "";
        for (Card c : cards) {
            result += c.name + "\n";
        }
        return result;
    }
    
    // Add Card: thêm lá bài
    public void addCard(Card c){
        this.cards.add(c);
    }
    
    // Remove card: xóa lá bài
    public void remove(Card c){
        this.cards.remove(c);
    }
    

    public int getQuantityCard() {
        return this.cards.size();
    }
    
    // Giá trị lớn nhất của tay bài maxTotal
    public int getMaxTotal(){
        Cards temp = this;
        temp.sort();
        return temp.getCards().get(temp.getQuantityCard()-1).getTotalValue();
    }

    public boolean contains(Card card) {
//        return cards.contains(card);
        for(Card c : cards){
            if(c.getTotalValue() == card.getTotalValue()){
                return true;
            }
        }
        return false;
    }
    
    // equals
    public boolean equals(Cards c){
        if(this.getQuantityCard() != c.getQuantityCard())
            return false;
        for(int i = 0; i < this.getQuantityCard(); i++){
            if(this.getCards().get(i).getTotalValue() != 
                    c.getCards().get(i).getTotalValue())
                return false;
        }
        return true;
    }
    
    // IS TWO SINGLE
    public boolean isTwoSingle(){
        if(this.getQuantityCard() == 1){
            return this.getCards().get(0).value == Card.VALUE_TWO;
        }
        return false;
    }
    
    // IS SINGLE TWO
    public boolean isTwoPair(){
        if(this.getQuantityCard() == 2){
            return this.getCards().get(0).value == Card.VALUE_TWO &&
                    this.getCards().get(1).value == Card.VALUE_TWO;
        }
        return false;
    }
    
    // IS FOUR OF A KIND
    public boolean isFourOfAKind(){
        return GamePlay.isFourOfAKind(getCardArray());
    }
    
    // IS FOUR OF A KIND
    public boolean isFourCP(){
        return GamePlay.isFourCP(getCardArray());
    }

    public void sort() {
        for (int i = 0; i < cards.size() - 1; i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(i).getTotalValue() > cards.get(j).getTotalValue()) {
                    Card temp = cards.get(i);
                    cards.set(i, cards.get(j));
                    cards.set(j, temp);
                }
            }
        }
    }
}
