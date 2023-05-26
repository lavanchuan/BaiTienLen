package TroChoiBaiTienLen.gameplay;

import TroChoiBaiTienLen.model.Cards;
import TroChoiBaiTienLen.model.Card;
import TroChoiBaiTienLen.model.CardStatistic;

import java.util.ArrayList;

public class GamePlay {
    
    // TYPE TURN
    public static final int ANY = 0;
    public static final int ANY_FORMAT = 1; // chứa lá bài 3S(3 bích)
    public static final int FORMAT = 2; // đánh theo định dạng của người chơi trước (nhận từ server)

    public static final int TO_SKIP_TURN = -1;
    
    // Statistic : thống kê số lượng bài
    public static ArrayList<CardStatistic> statistic(Cards cards) {
        ArrayList<CardStatistic> result = new ArrayList<CardStatistic>();
        Cards data = cards;
        data.sort();
        int value = 0;
        int counter = 0;
        for (Card c : data.getCards()) {
            if (value == 0) {
                value = c.value;
                counter = 1;
                continue;
            }
            if (c.value == value) {
                counter++;
                continue;
            } else {
                CardStatistic temp = new CardStatistic(value, counter);
                result.add(temp);
                value = c.value;
                counter = 1;
                continue;
            }
        }
        // add end element
        CardStatistic temp = new CardStatistic(value, counter);
        result.add(temp);

        return result;
    }

    // pair : 2 lá bài cùng giá trị (value)
    public static boolean isPair(Card[] cards){
        if(cards.length == 2){
            if(cards[0].value == cards[1].value)
                return true;
        }
        return false;
    }
    // ThreeOfAKind : 3 lá bài cùng giá trị (value)
    public static boolean isThreeOfAKind(Card[] cards){
        if(cards.length == 3){
            if(cards[0].value == cards[1].value && cards[0].value == cards[2].value){
                return true;
            }
        }
        return false;
    }
    // FourOfAKind : 4 lá bài cùng giá trị (value)
    public static boolean isFourOfAKind(Card... cards){
        if(cards.length != 4){
            return false;
        }
        for(int i = 1; i < cards.length; i++){
            if(cards[i] != null){
                if(cards[i].value != cards[0].value){
                    return false;
                }
            }
        }
        return true;
    }
    // Quantity pairs : Số lượng đôi
    public static int quantityPair(Cards cards){
        ArrayList<CardStatistic> statistic = statistic(cards);
        int counter = 0;
        for(CardStatistic c : statistic){
            counter += c.quantity / 2;
        }
        return counter;
    }
    
    // ConsecutivePairs: có ít nhất 3 đôi liên tiếp
    public static int quantityCP(Cards cards) {

        ArrayList<CardStatistic> statistics = statistic(cards);
        int value = 0;
        int counter = 0;
        int max = 0;
        for (CardStatistic c : statistics) {
            if (value == 0 && c.quantity >= 2) {
                value = c.value;
                counter = 1;
                continue;
            }

            if (c.quantity >= 2) {
                if (c.value == value + 1) {
                    counter++;
                    value = c.value;
                    continue;
                } else {
                    if (max < counter) {
                        max = counter;
                    }
                    value = c.value;
                    counter = 1;
                }
            }
        }
        // end for
        if (max < counter) {
            max = counter;
        }
        return max;
    }

    // ThreeConsecutivePairs : 3 đôi thông
    public static boolean isHasThreeCP(Cards cards) {
        if (quantityCP(cards) == 3) {
            return true;
        }
        return false;
    }
    // isThreeCP : các lá bài là 3 đôi thông
    public static boolean isThreeCP(Card... cards){
        Cards cList = new Cards(cards);
        if(cList.getQuantityCard()==6){
            if(isHasThreeCP(cList)) return true;
        }
        return false;
    }
    // FourConsecutivePairs : 4 đôi thông
    public static boolean isHasFourCP(Cards cards) {
        if (quantityCP(cards) == 4) {
            return true;
        }
        return false;
    }
    // isFourCP : các lá bài là 4 đôi thông
    public static boolean isFourCP(Card... cards){
        Cards cList = new Cards(cards);
        if(cList.getQuantityCard()==8){
            if(isHasFourCP(cList)) return true;
        }
        return false;
    }
    
    // isStraight : là sảnh
    public static boolean isStraight(Card... c){
        Cards cards = new Cards(c);
        cards.sort();
        // Không chứa lá 2
        if(cards.getCards().get(cards.getQuantityCard() - 1).value == 
                Card.VALUE_TWO){
            return false;
        }
        for(int i = 0; i < cards.getQuantityCard() - 1; i++){
            if(cards.getCards().get(i).value + 1 != cards.getCards().get(i+1).value)
                return false;
        }
        return true;
    }

    // Straight : sảnh có liên 3 đến A (value 1 - 12);
    public static boolean isHasStraightThreeToAce(Cards cards) {
        Cards data = cards;
        data.sort();
        ArrayList<CardStatistic> statistics = statistic(cards);
        if(statistics.get(0).value != Card.VALUE_THREE) // (3)
            return false;
        boolean hasAce = false;
        int value = statistics.get(0).value;
        for(CardStatistic c : statistics){
            if(c.value == Card.VALUE_ACE)
                hasAce = true;
            if(c.value == value)
                continue;
            value++;
            if(c.value != value)
                return false;
        }
        if(!hasAce)
            return false;
        
        return true;
    }

    // FourTwo : Tứ quý heo
    public static boolean isHasFourOfAKindTwo(Cards cards){
        ArrayList<CardStatistic> statistic = statistic(cards);
        for(int i = statistic.size()-1; i >= 0; i--){
            if(statistic.get(i).value == Card.VALUE_TWO){
                if(statistic.get(i).quantity == 4)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
    
    public static boolean isHasFourOfAKind(Cards cards, int value){ // kiem tra xem co tu quy lon hon, value
        ArrayList<CardStatistic> statistic = statistic(cards);
        for(int i = statistic.size()-1; i >= 0; i--){
            if(statistic.get(i).value > value){
                if(statistic.get(i).quantity == 4)
                    return true;
            }
        }
        return false;
    }
    
    // ăn trắng capot
    public static boolean isCapot(Cards cards) {
        
        // 4 heo
        if(isHasFourOfAKindTwo(cards)) return true;
        // 3 - ace
        if(isHasStraightThreeToAce(cards)) return true;
        // 5 consecutive pairs
        if(quantityCP(cards) == 5) return true;
        // 6 pairs
        if(quantityPair(cards) == 6) return true;
        return false;
    }

    // cướp cái
    public static boolean isRobTurn(Cards cards) {
        return isHasThreeCP(cards);
    }
    
    /// Turn
    public static int nextTurn(int turn, int quantity){
        if(++turn >= quantity)
            return 0;
        return turn;
    }
    
    
    // RANK


    public static void main(String[] args) {
        Card[] c = {new Card(1, 2),
            new Card(5, 2),
            new Card(4, 3),
            new Card(9, 1),
            new Card(3, 2),
            new Card(2, 4),
            new Card(5, 3),
            new Card(2, 1),
            new Card(3, 4),
            new Card(3, 1),
            new Card(11, 2),
            new Card(3, 3),
            new Card(11, 3)};
        Cards card = new Cards(c);
        
        System.err.println(quantityPair(card));;
        
        

//        ArrayList<CardStatistic> statistics = statistic(card);
//        for (CardStatistic item : statistics) {
//            System.out.println(item.getCardStatisticString());
//        }
//        System.out.println("===========\n\n")
//                ;
//        for (Card item : card.getCards()) {
//            System.out.println(item.name);
//        }
//        System.out.println("===========\n\n");
//
//        System.out.println(quantityCP(card));
//        System.out.println("===========\n\n");
//
//        System.out.println(isHasThreeCP(card));
//        System.out.println("===========\n\n");
//
//        System.out.println("IsHasFourCP\t: " + isHasFourCP(card));
//        System.out.println("===========\n\n");
//        
//        System.out.println("StraightThreeToAce\t:" + isHasStraightThreeToAce(card));
//        System.out.println("===========\n\n");
//        
//        System.out.println("FourOfAKindTwo\t:" + isHasFourOfAKindTwo(card));
//        System.out.println("===========\n\n");
//        
//        System.out.println("FourOfAKind\t:" + isHasFourOfAKind(card, 10));
//        System.out.println("===========\n\n");
//        
//        System.out.println("QuantityPair\t:" + quantityPair(card));
//        System.out.println("===========\n\n");
//        
//        System.out.println("IsStraight\t:" + isStraight(
//        new Card(8,2),
//                new Card(9,2),
//                new Card(10,2),
//                new Card(6,3),
//                new Card(5,2),
//                new Card(7,2)));
//        System.out.println("===========\n\n");
//        
//        System.out.println("IsFourOfAKind\t:" + isFourOfAKind(
//        new Card(8,2),
//                new Card(8,1),
//                new Card(8,4),
//                new Card(8,3)));
//        System.out.println("===========\n\n");
//        
//        System.out.println("IsThreeCP\t:" + isThreeCP(
//        new Card(6,2),
//                new Card(7,1),
//                new Card(5,4),
//                new Card(7,4),
//                new Card(8,4),
//                new Card(6,3)));
//        System.out.println("===========\n\n");
//        
//        System.out.println("IsFourCP\t:" + isFourCP(
//        new Card(6,2),
//                new Card(5,1),
//                new Card(5,4),
//                new Card(6,3),
//                new Card(8,1),
//                new Card(7,1),
//                new Card(8,4),
//                new Card(7,3)));
//        System.out.println("===========\n\n");


//        System.out.println(isCapot(card));
//        System.out.println(isRobTurn(card));
    }
}
