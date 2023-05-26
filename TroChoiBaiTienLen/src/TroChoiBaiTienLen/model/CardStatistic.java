package TroChoiBaiTienLen.model;

public class CardStatistic {
    public int value;
    public int quantity;
    
    public CardStatistic(int value, int quantity){
        this.value = value;
        this.quantity = quantity;
    }
    
    public String getCardStatisticString(){
        return this.value + " " + this.quantity;
    }
}
