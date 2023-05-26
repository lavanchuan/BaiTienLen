package TroChoiBaiTienLen.model;

import java.io.Serializable;

public class Card implements Serializable{

    public int value;
    public int type;
    public String name;

    public static final int TYPE_HEARTS = 4;
    public static final int TYPE_DIAMOND = 3;
    public static final int TYPE_CLUBES = 2;
    public static final int TYPE_SPADES = 1;

    public static final int VALUE_THREE = 1;
    public static final int VALUE_FOUR = 2;
    public static final int VALUE_FIVE = 3;
    public static final int VALUE_SIX = 4;
    public static final int VALUE_SEVEN = 5;
    public static final int VALUE_EIGHT = 6;
    public static final int VALUE_NIGHT = 7;
    public static final int VALUE_TEN = 8;
    public static final int VALUE_JACK = 9;
    public static final int VALUE_QUEEN = 10;
    public static final int VALUE_KING = 11;
    public static final int VALUE_ACE = 12;
    public static final int VALUE_TWO = 13;
    public static final int VALUE_ERROR = 0;
    public static final int TYPE_ERROR = 0;

    public Card(int value, int type) {
        this.value = value;
        this.type = type;
        this.name = getName() + " " + getTypeString();
    }

    public String getTypeString() {
        String typeString = "";
        switch (type) {
            case TYPE_HEARTS:
                typeString = "HEARTS";
                break;
            case TYPE_DIAMOND:
                typeString = "DIAMOND";
                break;
            case TYPE_CLUBES:
                typeString = "CLUBES";
                break;
            case TYPE_SPADES:
                typeString = "SPADES";
                break;
        }
        return typeString;
    }
    
    public int getTotalValue(){
        return (this.value - 1) * 4 + this.type;
    }
    
    public String getName() {
        if(this.value >= VALUE_THREE && this.value <= VALUE_TEN)
            return this.name = this.value + 2 + "";
        if(this.value == VALUE_JACK) return "J";
        if(this.value == VALUE_QUEEN) return "Q";
        if(this.value == VALUE_KING) return "K";
        if(this.value == VALUE_ACE) return "A";
        if(this.value == VALUE_TWO) return 2+"";
        return ""; 
    }
    
    public static Card getMinCard(){
        return new Card(VALUE_THREE, TYPE_SPADES);
    }
    
    // TEST
    public static void main(String[] args){
        Card card = new Card(13, 2);
        System.out.println(card.name);
       
    }
    
    
}
