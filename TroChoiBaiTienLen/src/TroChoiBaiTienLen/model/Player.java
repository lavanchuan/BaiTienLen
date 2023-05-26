package TroChoiBaiTienLen.model;

import java.net.SocketAddress;

public class Player {
    
    public Cards cards;
    SocketAddress sa;
    
    public Player(SocketAddress sa, Cards cards){
        this.sa = sa;
        this.cards = cards;
    }
    
    public Player(Cards cards){
        this.sa = null;
        this.cards = cards;
    }
    
    public String getCardsString(){
        return cards.getCardsString();
    }
    
    public Cards getCards(){
        return this.cards;
    }
    
    public boolean containsCard(Card card){
        return this.cards.contains(card);
    }
    
    public int getQuantityCard(){
        return this.cards.getQuantityCard();
    }
    
    public void sortCards(){
        this.cards.sort();
    }
    
    // remove card from cards
    public void removeCardOfCards(Card card){
        this.cards.remove(card);
    }
    
    public SocketAddress getSocketAddress(){
        return this.sa;
    }
}
