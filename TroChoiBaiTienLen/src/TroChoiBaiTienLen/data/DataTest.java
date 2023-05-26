package TroChoiBaiTienLen.data;

import TroChoiBaiTienLen.model.Card;
import TroChoiBaiTienLen.model.Cards;

public class DataTest {

    public DataTest(){

    }
    
    public Cards testFourOfAKindTwo(){
        Card[] c_test = {new Card(1, 2),
        new Card(5, 2),
        new Card(4, 3),
        new Card(9, 1),
        new Card(4, 2),
        new Card(2, 4),
        new Card(5, 3),
        new Card(2, 1),
        new Card(3, 4),
        new Card(13, 1),
        new Card(13, 2),
        new Card(13, 3),
        new Card(13, 4)};
        return new Cards(c_test);
    }
    
    public Cards testSixPair(){
        Card[] c_test = {new Card(1, 2),
        new Card(3, 2),
        new Card(3, 3),
        new Card(2, 1),
        new Card(2, 2),
        new Card(1, 4),
        new Card(5, 3),
        new Card(9, 1),
        new Card(9, 4),
        new Card(10, 1),
        new Card(10, 2),
        new Card(7, 3),
        new Card(5, 4)};
        return new Cards(c_test);
    }
    
    public Cards testFiveCP(){
        Card[] c_test = {new Card(1, 2),
        new Card(3, 2),
        new Card(3, 3),
        new Card(5, 1),
        new Card(5, 2),
        new Card(7, 4),
        new Card(7, 3),
        new Card(6, 1),
        new Card(6, 4),
        new Card(4, 1),
        new Card(4, 2),
        new Card(7, 3),
        new Card(5, 4)};
        return new Cards(c_test);
    }
    
    public Cards testStraight(){
        Card[] c_test = {new Card(1, 2),
        new Card(2, 2),
        new Card(3, 3),
        new Card(4, 1),
        new Card(5, 2),
        new Card(6, 4),
        new Card(7, 3),
        new Card(8, 1),
        new Card(9, 4),
        new Card(10, 1),
        new Card(11, 2),
        new Card(12, 3),
        new Card(5, 4)};
        return new Cards(c_test);
    }
    
    public Cards testFourCP(){
        Card[] c_test = {new Card(1, 2),
        new Card(3, 2),
        new Card(3, 3),
        new Card(2, 1),
        new Card(2, 2),
        new Card(9, 4),
        new Card(5, 3),
        new Card(5, 1),
        new Card(9, 4),
        new Card(4, 1),
        new Card(4, 2),
        new Card(7, 3),
        new Card(5, 4)};
        return new Cards(c_test);
    }
    
    public Cards testFourOfAKind(){
        Card[] c_test = {new Card(1, 2),
        new Card(5, 2),
        new Card(4, 3),
        new Card(9, 1),
        new Card(4, 2),
        new Card(2, 4),
        new Card(5, 3),
        new Card(2, 1),
        new Card(3, 4),
        new Card(7, 1),
        new Card(7, 2),
        new Card(7, 3),
        new Card(7, 4)};
        return new Cards(c_test);
    }
}
