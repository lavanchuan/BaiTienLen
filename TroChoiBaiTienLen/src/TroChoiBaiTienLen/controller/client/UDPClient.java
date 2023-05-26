package TroChoiBaiTienLen.controller.client;

import TroChoiBaiTienLen.data.Alert;
import TroChoiBaiTienLen.data.Setting;
import TroChoiBaiTienLen.gameplay.GamePlay;
import TroChoiBaiTienLen.model.Player;
import TroChoiBaiTienLen.model.Cards;
import TroChoiBaiTienLen.model.Card;
import TroChoiBaiTienLen.model.Tools;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class UDPClient {

    // net    
    DatagramSocket client;
    DatagramPacket packet;
    InetAddress ia;

    // data
    byte[] data;

    int port;
    String serverName;

    // gameplay
    boolean running;
    boolean isStarted = false;

    // clients
    // message
    String msg;

    // handle data
    ByteArrayOutputStream baos;
    ByteArrayInputStream bais;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    // Player
    Player player;

    public UDPClient() {
        this.serverName = Setting.SERVER_NAME;
        this.port = Setting.PORT;
        try {
            ia = InetAddress.getByName(serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            client = new DatagramSocket();
            running = true;

            // gui yeu cau vao game
            data = new byte[Setting.MAX_PACKET_LENGTH];
            packet = new DatagramPacket(data, data.length, ia, port);
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeUTF("Connect");
            oos.flush();
            data = baos.toByteArray();
            packet = new DatagramPacket(data, data.length, ia, port);
            client.send(packet);
            oos.close();

            Cards cards; // Bài trong lượt đánh

            while (running) {
                // chưa bắt đầu game
                if (!isStarted) {
                    data = new byte[Setting.MAX_PACKET_LENGTH];
                    packet = new DatagramPacket(data, data.length);
                    client.receive(packet);
                    data = packet.getData();
                    bais = new ByteArrayInputStream(data);
                    ois = new ObjectInputStream(bais);
                    msg = ois.readUTF();
                    System.out.println(msg.split("_")[0]);
                    isStarted = Boolean.parseBoolean(msg.split("_")[1]);
                } else { // in game
                    // Receive cards or alert: nhận bài hoặc thông báo
                    data = new byte[Setting.MAX_PACKET_LENGTH];
                    packet = new DatagramPacket(data, data.length);
                    client.receive(packet);
                    data = packet.getData();
                    bais = new ByteArrayInputStream(data);
                    ois = new ObjectInputStream(bais);
                    Object obj = ois.readObject();
                    String alert = "";
                    boolean isAlert = false;
                    try {
                        alert = (String) obj;
                        isAlert = true;
                    } catch (ClassCastException e) {

                    }
                    // Nếu là thông báo
                    if (isAlert) {
                        System.out.println(alert);
                        running = false;
                        break; // Kết thúc 1 ván bài

                    }

                    // Không phải là thông báo
                    player = new Player((Cards) obj);
                    player.sortCards();
                    System.out.println(player.getCardsString());

                    // Cướp lượt
                    // Không cướp lượt
                    int typeTurn;
                    while (true) {
                        data = new byte[Setting.MAX_PACKET_LENGTH];
                        packet.setData(data);
                        client.receive(packet);
                        data = packet.getData();
                        bais = new ByteArrayInputStream(data);
                        ois = new ObjectInputStream(bais);
                        // test.........
                        obj = ois.readObject();
                        alert = "";
                        isAlert = false;
                        try {
                            alert = (String) obj;
                            isAlert = true;
                        } catch (ClassCastException e) {

                        }
                        // Nếu là thông báo
                        if (isAlert) {
                            System.out.println(alert);
                            running = false;
                            break; // Kết thúc 1 ván bài

                        }
                        //......end test
                        cards = (Cards) obj;
//                        cards = (Cards) ois.readObject();

                        if (cards.getQuantityCard() == 0) {
                            typeTurn = GamePlay.ANY;
                            // debugger => bỏ lượt
                        } else {
                            if (cards.getQuantityCard() == 1) {
                                if (cards.getCards().get(0).value == Card.VALUE_ERROR) {
                                    typeTurn = GamePlay.ANY_FORMAT;
                                } else {
                                    typeTurn = GamePlay.FORMAT;
                                }
                            } else { // quantity > 1
                                typeTurn = GamePlay.FORMAT;
                            }
                        }

                        // Hiển thị các lá bài mà người chơi trước đó đã đánh
                        if (typeTurn == GamePlay.FORMAT) {
                            System.out.println("\n\nCác lá bài bài người chơi trước đánh");
                            for (Card c : cards.getCards()) {
                                System.out.println(c.name);
                            }
                        } else if (typeTurn == GamePlay.ANY_FORMAT) {
                            System.out.println("\n\nBạn được đánh trước vì có 3 spades, "
                                    + "đánh các lá bài có lá 3 Spades");
                        }

                        // turn action ->
                        // cards use
                        boolean enable = false; // các lá bài được phép đánh
                        boolean isToSkipTurn = false; // xác định bỏ lượt
                        Cards cardsUse = new Cards(new Card[0]);

                        while (!enable) { //..

                            String cardsIndexString;
                            // Xử lý chuỗi để lấy được danh sách các lá bài sử dụng để đánh
                            Card[] arrayCard = new Card[0];

                            boolean enableText = false;
                            cardsIndexString = "";
                            while (!enableText) {
                                /* Kiểm tra chuỗi, số lượng các thẻ bài không vượt giới hạn, 
                                không có các chứ cái hay ký tự đặc biệt, nếu chứa gạch nối
                                thì chỉ có 3 phần tử, phần tử thứ 2 là dấu gạch nối '-',
                                số đầu tiên bé hơn số cuối: a - b (a < b)
                                Nếu không có dấu gạch nối thì phải toàn số:
                                Các số không được trùng nhau,
                                Số lượng các số không vượt quá đọ dài các thẻ bài đang có,
                                Và các số không được vượt qua vị trí các thẻ bài hiện có
                                 */
                                // Hiển thị thông tin các lá bài để đánh
                                System.out.println("\n\nLựa chọn các lá bài để đánh");
                                for (int i = 0; i < this.player.getCards().getQuantityCard(); i++) {
                                    System.out.printf("[%d]\t: %s\n", i,
                                            this.player.getCards().getCards().get(i).name);
                                }
                                if (typeTurn == GamePlay.FORMAT) {
                                    System.out.println(Alert.TO_SKIP_TURN);
                                }

                                System.out.print("\nCác lá bài muốn đánh: ");
                                cardsIndexString = Tools.getText();

                                enableText = Tools.checkString(cardsIndexString,
                                        this.player.getQuantityCard() - 1,
                                        this.player.getQuantityCard());

                                if (enableText) {
                                    if ((typeTurn == GamePlay.ANY
                                            || typeTurn == GamePlay.ANY_FORMAT)
                                            && cardsIndexString.equals(GamePlay.TO_SKIP_TURN + "")) {
                                        System.err.println("ALERT: Bạn không được bỏ lượt");
                                        enableText = false;
                                    }
                                }
                            }

                            // 
                            if (cardsIndexString.split(" ").length == 1
                                    && Integer.parseInt(cardsIndexString.split(" ")[0])
                                    == GamePlay.TO_SKIP_TURN) { // Bỏ lượt: to skip turn
                                // Bỏ lượt khi length = 1, value = -1
                                isToSkipTurn = true;
                                break;
                            } else if (cardsIndexString.indexOf("-") != -1) {
                                // nếu có gạch nối, lấy danh sách từ phần tử đầu đến phần tử thứ hai
                                int firstIndex = Integer.parseInt(cardsIndexString.split(" ")[0]);
                                int lastIndex = Integer.parseInt(cardsIndexString.split(" ")[2]);
                                arrayCard = new Card[lastIndex - firstIndex + 1];
                                for (int i = firstIndex; i <= lastIndex; i++) {
                                    arrayCard[i - firstIndex] = this.player.getCards().getCards().get(i);
                                }
                            } else {
                                // Nếu không có dấu gạch nối, lấy các phần tử theo chuỗi
                                arrayCard = new Card[cardsIndexString.split(" ").length];
                                int index = 0;
                                for (String s : cardsIndexString.split(" ")) {
                                    arrayCard[index++] = this.player.getCards().getCards().get(Integer.parseInt(s));
                                }
                            }

                            // Các lá bài sử dụng để đánh
                            cardsUse = new Cards(arrayCard);

                            // check cards use is enable
                            Card[] cardArray = cardsUse.getCardArray();
                            if (typeTurn == GamePlay.ANY) {
                                if (cardsUse.getQuantityCard() == 1) {
                                    enable = true;
                                } else if (cardsUse.getQuantityCard() == 2) {
                                    // Nếu số lượng lá bài đánh là 2
                                    enable = GamePlay.isPair(cardArray);
                                } else { // Số lượng các lá bài lớn hơn 3
                                    /*Số lượng lá bài đánh là 3: sám cô*/
                                    enable = GamePlay.isThreeOfAKind(cardArray);
                                    /*Số lượng lá bài là 4: tứ quý*/
                                    if (!enable) {
                                        enable = GamePlay.isFourOfAKind(cardArray);
                                    }
                                    /*Số lượng lá bài là 6: ba đôi thông*/
                                    if (!enable) {
                                        enable = GamePlay.isThreeCP(cardArray);
                                    }
                                    /*Số lượng lá bài là 8: bốn đôi thông*/
                                    if (!enable) {
                                        enable = GamePlay.isFourCP(cardArray);
                                    }
                                    /*Các lá bài là sảnh*/
                                    if (!enable) {
                                        enable = GamePlay.isStraight(cardArray);
                                    }
                                }
                                if (!enable) {
                                    System.err.printf("\nERROR: %s\n", Alert.INVALID_CARDS);
                                }
                            } else if (typeTurn == GamePlay.ANY_FORMAT) {
                                // Các lá bài phải có lá bài nhỏ nhất (3S)
                                if ((new Cards(cardArray)).contains(Card.getMinCard())) {
                                    if (cardsUse.getQuantityCard() == 1) {
                                        enable = true;
                                    } else if (cardsUse.getQuantityCard() == 2) {
                                        // Nếu số lượng lá bài đánh là 2
                                        enable = GamePlay.isPair(cardArray);
                                    } else { // Số lượng các lá bài lớn hơn 3
                                        /*Số lượng lá bài đánh là 3: sám cô*/
                                        enable = GamePlay.isThreeOfAKind(cardArray);
                                        /*Số lượng lá bài là 4: tứ quý*/
                                        if (!enable) {
                                            enable = GamePlay.isFourOfAKind(cardArray);
                                        }
                                        /*Số lượng lá bài là 6: ba đôi thông*/
                                        if (!enable) {
                                            enable = GamePlay.isThreeCP(cardArray);
                                        }
                                        /*Số lượng lá bài là 8: bốn đôi thông*/
                                        if (!enable) {
                                            enable = GamePlay.isFourCP(cardArray);
                                        }
                                        /*Các lá bài là sảnh*/
                                        if (!enable) {
                                            enable = GamePlay.isStraight(cardArray);
                                        }
                                    }
                                }

                                if (!enable) {
                                    System.err.printf("\nERROR: %s\n", Alert.INVALID_CARDS);
                                }
                            } else if (typeTurn == GamePlay.FORMAT) {
                                /*Xác định xem người chơi trước đó đánh gì
                                Số lượng quân bài + đặc thù về số lượng để đánh tiếp*/
                                if (cards.getQuantityCard() == 1) {
                                    // lá bài là 2
                                    if (cards.getCards().get(0).value == Card.VALUE_TWO) {
                                        // Nếu số lượng lá bài sử dụng cũng là 1, 
                                        // thì lá bài đó tổng giá trị(tgt) lớn hơn tgt lá bài của đối thủ
                                        if (cardsUse.getQuantityCard() == 1) {
                                            if (cardsUse.getCards().get(0).getTotalValue()
                                                    > cards.getCards().get(0).getTotalValue()) {
                                                enable = true;
                                            }
                                        } else if (cardsUse.getQuantityCard() == 4
                                                || cardsUse.getQuantityCard() == 6
                                                || cardsUse.getQuantityCard() == 8) {
                                            enable = GamePlay.isFourOfAKind(cardArray);
                                            if (!enable) {
                                                enable = GamePlay.isThreeCP(cardArray);
                                            }
                                            if (!enable) {
                                                enable = GamePlay.isFourCP(cardArray);
                                            }
                                        }
                                    } // lá bài không phải là 2, chỉ được đánh 1 lá bài
                                    // và lá bài đánh ra phải có tgt lớn hơn lá bài của đối thủ
                                    else {
                                        // Số lượng lá bài sử dụng bắt buộc là 1
                                        if (cardsUse.getQuantityCard() == 1) {
                                            if (cardsUse.getCards().get(0).getTotalValue()
                                                    > cards.getCards().get(0).getTotalValue()) {
                                                enable = true;
                                            }
                                        }
                                    }
                                } else if (cards.getQuantityCard() == 2) {
                                    /* Nếu người chơi trước đánh 2 lá bài
                                    // thì chắc chắn đó là đôi
                                    // nếu là đôi 2 thì dùng đôi 2 lớn hơn để đánh
                                    // hoặc dùng 4 đôi thông để đánh */
                                    // nếu là đôi 2
                                    int value, maxTotal;
                                    value = cards.getCards().get(0).value;
                                    maxTotal = cards.getMaxTotal();
                                    if (value == Card.VALUE_TWO) { // nếu là đôi 2
                                        if (cardsUse.getQuantityCard() == 2) { // Nếu người chơi đánh 2 lá
                                            if (GamePlay.isPair(cardArray)) { // Nếu là đôi, thì phải đánh đôi lớn hơn
                                                enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                            }
                                        } else if (cardsUse.getQuantityCard() == 8) { // nếu người chơi đánh 8 lá
                                            enable = GamePlay.isFourCP(cardArray);
                                        }
                                    } else {
                                        if (GamePlay.isPair(cardArray)) { // Nếu là đôi, thì phải đánh đôi lớn hơn
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        }
                                    }
                                } else if (cards.getQuantityCard() == 3) {
                                    // là sám cô hoặc sảnh
                                    if (GamePlay.isThreeOfAKind(cards.getCardArray())) {
                                        if (GamePlay.isThreeOfAKind(cardArray)) {
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        }
                                    } else {
                                        if (cardsUse.getQuantityCard() == 3) {
                                            if (GamePlay.isStraight(cardArray)) {
                                                enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                            }
                                        }
                                    }

                                } else if (cards.getQuantityCard() == 4) {
                                    // là tứ quý hoặc sảnh
                                    if (GamePlay.isFourOfAKind(cards.getCardArray())) { // là tứ quý
                                        if (GamePlay.isFourOfAKind(cardArray)) {
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        } else if (GamePlay.isFourCP(cardArray)){
                                            enable = true;
                                        }
                                    } else { // là sảnh
                                        if (cardsUse.getQuantityCard() == 4) {
                                            if (GamePlay.isStraight(cardArray)) {
                                                enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                            }
                                        }
                                    }
                                } else if (cards.getQuantityCard() == 6) {
                                    // là 3 đôi thông hoặc sảnh
                                    if (GamePlay.isThreeCP(cards.getCardArray())) {
                                        if (GamePlay.isThreeCP(cardArray)) {
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        }
                                    } else {
                                        if (cardsUse.getQuantityCard() == cards.getQuantityCard()) {
                                            if (GamePlay.isStraight(cardArray)) {
                                                enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                            }
                                        }
                                    }
                                } else if (cards.getQuantityCard() == 8) {
                                    // là 4 đôi thông hoặc sảnh
                                    if (GamePlay.isFourCP(cards.getCardArray())) {
                                        if (GamePlay.isFourCP(cardArray)) {
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        }
                                    } else {
                                        if (cardsUse.getQuantityCard() == 8) {
                                            if (GamePlay.isStraight(cardArray)) {
                                                enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                            }
                                        }
                                    }
                                } else { // là sảnh
                                    if (cardsUse.getQuantityCard() == cards.getQuantityCard()) {
                                        if (GamePlay.isStraight(cardArray)) {
                                            enable = cards.getMaxTotal() < cardsUse.getMaxTotal();
                                        }
                                    }
                                }

                                if (!enable) {
                                    System.err.printf("\nERROR: %s\n", Alert.INVALID_CARDS);
                                }
                            }

                            // .. end check enable
                        } // while to check cards valid: Kiểm tra các la bài là hợp lệ để đánh
                        // remove cards used of player
                        if (!isToSkipTurn) {
                            for (Card c : cardsUse.getCards()) {
                                this.player.removeCardOfCards(c);
                            }
                            // Hiển thị các lá bài đã đánh
                            System.out.println("\n\nCác lá bài đã đánh:\n" + cardsUse.getCardsString());
                        } else {
                            cardsUse = cards; // Nếu là bỏ lượt thì gửi lại các lá bài nhận được
                        }

                        // show remain cards: Các thẻ bài còn lại sau khi đánh
                        System.out.println("\n\nCác lá bài còn lại");
                        for (Card c : this.player.getCards().getCards()) {
                            System.out.printf("%s\n", c.name);
                        }

                        // send cards used
                        sendPacket(cardsUse);
                    }
                }
            }// end running

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // send packacket;
    public void sendPacket(Cards cardsUse) {
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(cardsUse);
            oos.flush();
            data = baos.toByteArray();
            packet.setData(data);
            client.send(packet);
        } catch (IOException e) {
            System.err.println("Error: send packet");
        }
    }

    public static void main(String[] args) {
        UDPClient client = new UDPClient();
        client.run();
    }
}
