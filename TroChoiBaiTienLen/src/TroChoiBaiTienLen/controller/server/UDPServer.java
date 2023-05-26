package TroChoiBaiTienLen.controller.server;

import TroChoiBaiTienLen.data.Setting;

import TroChoiBaiTienLen.model.Tools;
import TroChoiBaiTienLen.model.Deck;
import TroChoiBaiTienLen.model.Card;
import TroChoiBaiTienLen.model.Cards;
import TroChoiBaiTienLen.model.Player;

import TroChoiBaiTienLen.gameplay.GamePlay;
import TroChoiBaiTienLen.gameplay.Time;

import TroChoiBaiTienLen.data.Alert;
import TroChoiBaiTienLen.data.DataTest;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;

public class UDPServer {

    // net    
    DatagramSocket server;
    DatagramPacket packet;

    // data
    byte[] data;

    int port;

    // gameplay
    boolean running;
    boolean inGame;
    boolean isStarted;
    boolean gameEnd = false;
    int typeGame;

    // clients
    int quantityPlayer;
    ArrayList<SocketAddress> clients;
    int counter = 0;
    ArrayList<Player> players;

    // message
    String msg;

    // handle data
    ByteArrayOutputStream baos;
    ByteArrayInputStream bais;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    // Deck
    Deck deck;

    public UDPServer() {
        this.port = Setting.PORT;
        this.clients = new ArrayList<SocketAddress>();
        typeGame = Setting.NORMAL; // default type game
    }

    public void run() {
        try {
            server = new DatagramSocket(this.port);
            running = true;
            while (running) {
                isStarted = false;
                // so nguoi choi cho phep
                do {
                    System.out.printf("Quantity player [%d - %d]: ",
                            Setting.MIN_PLAYER, Setting.MAX_PLAYER);
                    quantityPlayer = Tools.getInt();
                } while (quantityPlayer < Setting.MIN_PLAYER || quantityPlayer > Setting.MAX_PLAYER);

                // thể loại game
                if (quantityPlayer >= Setting.MIN_PLAYER_RANK) {
                    do {
                        System.out.printf("Type game: [Normal(%d), Rank(%d)]: ",
                                Setting.NORMAL,
                                Setting.RANK);
                        typeGame = Tools.getInt();
                    } while (typeGame < Setting.NORMAL || typeGame > Setting.RANK);
                }
                SocketAddress sa;
                boolean start00 = false; // Bắt đầu
                inGame = true;

                while (inGame) { // mot lan
                    // TEST...
                    // xac dinh nguoi choi
                    data = new byte[Setting.MAX_PACKET_LENGTH];
                    packet = new DatagramPacket(data, data.length);
                    server.receive(packet);
                    sa = packet.getSocketAddress();
                    if (!clients.contains(sa)) {
                        // thêm địa chỉ người chơi
                        clients.add(sa);
                        // nếu chưa đủ người chơi, thông báo chờ
                        if (++counter < quantityPlayer) {
                            msg = "Chờ thêm " + (quantityPlayer - counter) + " người chơi";
                        } else // nếu đủ người chơi, thông báo bắt đầu
                        if (counter == quantityPlayer) {
                            msg = "Bắt đầu";
                            start00 = true;
                        } else {
                            msg = "Đã đủ người chơi, chờ ván sau";
                        }
                        // thông báo
                        if (isStarted) {
                            baos = new ByteArrayOutputStream();
                            oos = new ObjectOutputStream(baos);
                            oos.writeUTF(msg + "_" + !isStarted);
                            oos.flush();
                            data = baos.toByteArray();
                            packet = new DatagramPacket(data, data.length, sa);
                            server.send(packet);
                            continue; // ngăn chặn ảnh hưởng đến các client khác
                        } else {
                            for (SocketAddress socketAddress : clients) {
                                baos = new ByteArrayOutputStream();
                                oos = new ObjectOutputStream(baos);
                                if (start00) {
                                    oos.writeUTF(msg + "_" + !isStarted);
                                } else {
                                    oos.writeUTF(msg + "_" + isStarted);
                                }
                                oos.flush();
                                data = baos.toByteArray();
                                packet = new DatagramPacket(data, data.length, socketAddress);
                                server.send(packet);
                            }
                        }

                        if (counter == quantityPlayer) {
                            isStarted = true;
                        }
                    }
                    // ...TEST */

                    // game play
                    if (isStarted) {
                        // players
                        this.players = new ArrayList<Player>();
                        // tạo mới bộ bài
                        deck = new Deck();
                        // xóc bài
                        deck.shuffles(13 * quantityPlayer);
                        // chia bài => player
                        ArrayList<Card[]> list = deck.deal(quantityPlayer);
                        boolean[] capot = new boolean[quantityPlayer]; // Danh sách người chơi tới trắng
                        boolean isHasCapot = false; // có người chơi tới trắng
                        for (int i = 0; i < list.size(); i++) {
                            DataTest test = new DataTest();
                            //TEST capot
//                            if(i == 0){
////                                this.players.add(new Player(clients.get(i), test.testFourCP()));
//                            } else
//                            if(i == 1){
//                                this.players.add(new Player(clients.get(i), test.testFiveCP()));
//                                this.players.add(new Player(clients.get(i), test.testFourOfAKindTwo()));
//                                this.players.add(new Player(clients.get(i), test.testSixPair()));
//                                this.players.add(new Player(clients.get(i), test.testStraight()));
//                                this.players.add(new Player(clients.get(i), test.testFourCP()));
//                                this.players.add(new Player(clients.get(i), test.testFourOfAKind()));
//                            } else// ... end TEST
                            this.players.add(new Player(clients.get(i), new Cards(list.get(i))));

                            // check capot
                            capot[i] = GamePlay.isCapot(this.players.get(i).getCards());
                            if (capot[i]) {
                                isHasCapot = true;
                            }
                        }

                        // Gửi các lá bài được chia tới các người chơi
                        for (Player p : this.players) {
                            System.out.println(p.getCardsString());
                            baos = new ByteArrayOutputStream();
                            oos = new ObjectOutputStream(baos);
                            oos.writeObject(p.getCards());
                            oos.flush();
                            data = baos.toByteArray();
                            packet = new DatagramPacket(data, data.length, p.getSocketAddress());
                            server.send(packet);
                        }

                        if (!isHasCapot) {// Không có tới trắng
                            int turn = 0; // neu khong tim thay nguoi giu la bai nho nhat, thi nguoi choi dau tien duoc danh truoc
                            // lá bài nhỏ nhất đánh trước v = 1;
                            boolean isFirstTurn = true; // đánh dấu là người đánh đầu tiên (không cần 3 bích)
                            for (Player p : this.players) {
                                if (p.containsCard(Card.getMinCard())) {
                                    turn = this.players.indexOf(p);
                                    break;
                                }
                            }
                            if (turn != 0) {
                                isFirstTurn = false;
                            }

                            // cards
                            Cards cards;
                            int indexLastPlayer; // Đánh dấu người đánh các lá bài trước (index) => sở hữu
                            int indexCurrentPlayer; // Đánh dấu người vừa đánh các lá bài (có thể là bỏ lượt)
                            Cards cardsLastPlayer; // Các lá bài của người chơi trước
                            Cards cardsCurrentPlayer; // Các lá bài của người chơi vừa đánh, = người chơi trước nghĩa là bỏ lượt
                            int turnCounter = 0;
//                        int[] roundDefault; // vòng đấu mặt định(những người được đánh)
                            int[] round;
                            int typeTurn; // trạng thái đánh, kiểu đánh (ANY | ANY_FORMAT | FORMAT)

                            // setup round(0 -> out round, 1 in round)
                            round = new int[quantityPlayer];
                            for (int i = 0; i < round.length; i++) {
                                round[i] = 1;
                            }

                            switch (typeGame) {
                                case Setting.NORMAL:
                                    System.out.println("GAME STARTING");
                                    // Cướp lượt (ROB TURN)

                                    // Khởi tạo mới => bắt đầu lượt đầu tiên
                                    cards = new Cards(new Card[0]);
                                    int indexWinner = -1;

                                    indexLastPlayer = 0;
                                    cardsLastPlayer = new Cards(new Card[0]);
                                    do {
                                        // ACTION TURN

                                        // nếu isFirstTurn = false => gửi 1 lá bài có value lỗi (value = 14)
                                        if (turnCounter == 0 && !isFirstTurn) {
                                            cards.addCard(new Card(Card.VALUE_ERROR, Card.TYPE_ERROR));
                                            indexLastPlayer = turn;
//                                            isFirstTurn = true;// ngăn truy cập lại vào đoạn mã này để hủy là lượt đầu tiên
                                        }

                                        // Đánh tự do khi quay lại vòng
                                        if (turnCounter != 0 && turn == indexLastPlayer) {
                                            cards = new Cards(new Card[0]);
                                        }

                                        typeTurn = GamePlay.FORMAT;
                                        if (cards.getQuantityCard() == 0) {
                                            typeTurn = GamePlay.ANY;
                                        } else if (cards.getQuantityCard() == 1
                                                && cards.getCards().get(0).value == Card.VALUE_ERROR) {
                                            typeTurn = GamePlay.ANY_FORMAT;
                                        }

                                        if (typeTurn == GamePlay.ANY || typeTurn == GamePlay.ANY_FORMAT) {
                                            for (int i = 0; i < round.length; i++) {
                                                round[i] = 1;
                                            }
                                        }

                                        // (1)send
                                        baos = new ByteArrayOutputStream();
                                        oos = new ObjectOutputStream(baos);
                                        oos.writeObject(cards);
                                        oos.flush();
                                        data = baos.toByteArray();
                                        packet = new DatagramPacket(data, data.length, this.players.get(turn).getSocketAddress());
                                        server.send(packet);
                                        // Xác định người chơi trước đó đánh, và các lá bài đã đánh

                                        // (2)receive
                                        data = new byte[Setting.MAX_PACKET_LENGTH];
                                        packet.setData(data);
                                        server.receive(packet);
                                        data = packet.getData();
                                        bais = new ByteArrayInputStream(data);
                                        ois = new ObjectInputStream(bais);
                                        try {
                                            cards = (Cards) ois.readObject();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        // Xử lý vòng
                                        // Xử lý bỏ lượt
                                        if (turnCounter == 0) {
                                            cardsLastPlayer = cards;
                                        }

                                        indexCurrentPlayer = turn;
                                        cardsCurrentPlayer = cards;
                                        // Bỏ lượt khi nhận các lá bài là lá bài của người đánh trước
                                        if (turnCounter++ == 0 || !cardsCurrentPlayer.equals(cardsLastPlayer)) { // tính cả lượt đánh đầu tiên để bỏ các lá bài vừa đánh của người chơi
                                            // Không phải là bỏ lượt
                                            indexLastPlayer = indexCurrentPlayer;
                                            cardsLastPlayer = cardsCurrentPlayer;

                                            // remove cards used of player
                                            Player tempPlayer = this.players.get(indexCurrentPlayer);
                                            for (Card c : cardsCurrentPlayer.getCards()) {
                                                for (Card item : tempPlayer.getCards().getCards()) {
                                                    if (c.value == item.value && c.type == item.type) {
//                                                    System.out.println("Item Remove: " + item.name);
                                                        tempPlayer.removeCardOfCards(item);
                                                        break;
                                                    }
                                                }
                                            }
                                            this.players.set(indexCurrentPlayer, tempPlayer);
                                            // CHECK END
                                            if (this.players.get(indexCurrentPlayer).getQuantityCard() == 0) {
                                                indexWinner = indexCurrentPlayer;
                                                gameEnd = true;
                                            }
                                        } else {
                                            // Là bỏ lượt
                                            cards = cardsLastPlayer;
                                            // Xóa vị trí ra khỏi vòng đấu
                                            if (indexLastPlayer != indexCurrentPlayer) { // Người đánh những lá bài đó thì vẫn nằm trong vòng đấu
                                                round[indexCurrentPlayer] = 0;
                                            }
                                        }
                                        System.out.print("INROUND: ");
                                        for (int i = 0; i < round.length; i++) {
                                            System.out.print(round[i] + " ");
                                        }
                                        System.out.println("");

                                        do {
                                            turn = GamePlay.nextTurn(turn, quantityPlayer); // chuyển lượt cho người khác
                                            if (round[turn] == 0) { // xứ lý đánh 4 đôi thông ngoài vòng
                                                if (cardsLastPlayer.isTwoSingle()
                                                        || cardsLastPlayer.isTwoSingle()
                                                        || cardsLastPlayer.isFourOfAKind()
                                                        || cardsLastPlayer.isFourCP()) { // nếu người chơi trước đó đánh 2, 22, tứ quý
                                                    if (GamePlay.isHasFourCP(this.players.get(turn).getCards())) {
                                                        round[turn] = 1;
                                                    }
                                                }
                                            }
                                        } while (round[turn] == 0); // Nếu bị loại thì không được đánh
                                    } while (!gameEnd);
                                    if (indexWinner != -1) {
                                        for (int i = 0; i < this.players.size(); i++) {
                                            if (i == indexWinner) {
                                                msg = Alert.getAlertEnd(true);
                                            } else {
                                                msg = Alert.getAlertEnd(false);
                                            }
                                            baos = new ByteArrayOutputStream();
                                            oos = new ObjectOutputStream(baos);
                                            oos.writeObject(msg);
                                            oos.flush();
                                            data = baos.toByteArray();
                                            packet.setData(data);
                                            packet.setSocketAddress(this.players.get(i).getSocketAddress());
                                            server.send(packet);
                                        }
                                        inGame = false;
                                    }
                                    break;
                                case Setting.RANK:
                                    ArrayList<Integer> rank = new ArrayList<Integer>(); // Xếp hạng cho người chơi

                                    System.out.println("GAME STARTING");
                                    // Cướp lượt (ROB TURN)

                                    // Khởi tạo mới => bắt đầu lượt đầu tiên
                                    cards = new Cards(new Card[0]);

                                    indexLastPlayer = 0;
                                    cardsLastPlayer = new Cards(new Card[0]);

                                    int indexEnjoy = -1; // hưởng sái khi có người về(Nếu các người chơi còn lại bỏ lượt)
                                    do {
                                        // ACTION TURN

                                        // nếu isFirstTurn = false => gửi 1 lá bài có value lỗi (value = 14)
                                        if (turnCounter == 0 && !isFirstTurn) {
                                            cards.addCard(new Card(Card.VALUE_ERROR, Card.TYPE_ERROR));
                                            indexLastPlayer = turn;
                                        }

                                        // Đánh tự do khi quay lại vòng
                                        if (turnCounter != 0 && turn == indexLastPlayer) {
                                            cards = new Cards(new Card[0]);
                                        }

                                        typeTurn = GamePlay.FORMAT;
                                        if (cards.getQuantityCard() == 0) {
                                            typeTurn = GamePlay.ANY;
                                        } else if (cards.getQuantityCard() == 1
                                                && cards.getCards().get(0).value == Card.VALUE_ERROR) {
                                            typeTurn = GamePlay.ANY_FORMAT;
                                        }

                                        if (typeTurn == GamePlay.ANY || typeTurn == GamePlay.ANY_FORMAT) {
                                            System.out.println("Số người tham gia đánh hiện tại: " + round.length);
                                            for (int i = 0; i < round.length; i++) {
                                                round[i] = 1;
                                            }
                                        }

                                        // (1)send
                                        baos = new ByteArrayOutputStream();
                                        oos = new ObjectOutputStream(baos);
                                        oos.writeObject(cards);
                                        oos.flush();
                                        data = baos.toByteArray();
                                        packet = new DatagramPacket(data, data.length, this.players.get(turn).getSocketAddress());
                                        server.send(packet);
                                        // Xác định người chơi trước đó đánh, và các lá bài đã đánh

                                        // (2)receive
                                        data = new byte[Setting.MAX_PACKET_LENGTH];
                                        packet.setData(data);
                                        server.receive(packet);
                                        data = packet.getData();
                                        bais = new ByteArrayInputStream(data);
                                        ois = new ObjectInputStream(bais);
                                        try {
                                            cards = (Cards) ois.readObject();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        // Xử lý vòng
                                        // Xử lý bỏ lượt
                                        if (turnCounter == 0) {
                                            cardsLastPlayer = cards;
                                        }

                                        indexCurrentPlayer = turn;
                                        cardsCurrentPlayer = cards;
                                        // Bỏ lượt khi nhận các lá bài là lá bài của người đánh trước
                                        if (turnCounter++ == 0 || !cardsCurrentPlayer.equals(cardsLastPlayer)) { // tính cả lượt đánh đầu tiên để bỏ các lá bài vừa đánh của người chơi
                                            // Không phải là bỏ lượt
                                            
                                            if(indexEnjoy != -1){
                                                indexEnjoy = -1;
                                            }
                                            
                                            indexLastPlayer = indexCurrentPlayer;
                                            cardsLastPlayer = cardsCurrentPlayer;

                                            // remove cards used of player
                                            Player tempPlayer = this.players.get(indexCurrentPlayer);
                                            for (Card c : cardsCurrentPlayer.getCards()) {
                                                for (Card item : tempPlayer.getCards().getCards()) {
                                                    if (c.value == item.value && c.type == item.type) {
//                                                    System.out.println("Item Remove: " + item.name);
                                                        tempPlayer.removeCardOfCards(item);
                                                        break;
                                                    }
                                                }
                                            }
                                            this.players.set(indexCurrentPlayer, tempPlayer);
                                            // CHECK WIN RANK: Người chơi đã tới thì không tham gia đánh nữa
                                            if (this.players.get(indexCurrentPlayer).getQuantityCard() == 0) {
                                                rank.add(indexCurrentPlayer);
//                                                round = new int[quantityPlayer - rank.size()];
//                                                for(int i = 0; i < round.length; i++){
//                                                    round[i] = 1;
//                                                }
                                                boolean exists;
                                                do {
                                                    exists = false;
                                                    indexEnjoy = GamePlay.nextTurn(turn, round.length);
                                                    for(int i : rank){
                                                        if(indexEnjoy == i){
                                                            exists = true;
                                                            break;
                                                        }
                                                    }
                                                } while (exists);
                                                
//                                                indexLastPlayer = indexCurrentPlayer;
                                                System.out.println("Co 1 nguoi da ve");
                                            }
                                        } else {
                                            // Là bỏ lượt
                                            cards = cardsLastPlayer;
                                            // Xóa vị trí ra khỏi vòng đấu
                                            if (indexLastPlayer != indexCurrentPlayer) { // Người đánh những lá bài đó thì vẫn nằm trong vòng đấu
                                                round[indexCurrentPlayer] = 0;
                                            }
                                        }

                                        System.out.println("Số người đã tới: " + rank.size());

                                        System.out.print("INROUND: ");
                                        for (int i = 0; i < round.length; i++) {
                                            System.out.print(round[i] + " ");
                                        }
                                        System.out.println("");

                                        int enjoyCounter = 0; // Số lần đánh của người hưởng sái
                                        do {
                                            // check exists in rank
                                            boolean exists;

                                            {
                                                exists = false;
                                                turn = GamePlay.nextTurn(turn, round.length); // chuyển lượt cho người khác
                                                for (int i : rank) {
                                                    if (i == turn) {
                                                        exists = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            while (exists);
                                            
                                            if(indexEnjoy != -1){
                                                if(enjoyCounter++ == 1){
                                                    round[enjoyCounter] = 1;
                                                    // break;
                                                }
                                            }
                                            
                                            if (round[turn] == 0) { // xứ lý đánh 4 đôi thông ngoài vòng
                                                if (cardsLastPlayer.isTwoSingle()
                                                        || cardsLastPlayer.isTwoSingle()
                                                        || cardsLastPlayer.isFourOfAKind()
                                                        || cardsLastPlayer.isFourCP()) { // nếu người chơi trước đó đánh 2, 22, tứ quý
                                                    if (GamePlay.isHasFourCP(this.players.get(turn).getCards())) {
                                                        round[turn] = 1;
                                                    }
                                                }
                                            }
                                            
                                        } while (round[turn] == 0); // Nếu bị loại thì không được đánh

                                        // khi nao thi game end
                                        if (rank.size() >= quantityPlayer - 1) {
                                            int sum = 0;
                                            for (int i = 0; i < quantityPlayer; i++) {
                                                sum += i;
                                            }
                                            int sums = 0;
                                            for (int i = 0; i < rank.size(); i++) {
                                                sums += rank.get(i);
                                            }
                                            rank.add(sum - sums);
                                            System.out.println(sum - sums);
                                            gameEnd = true;
                                        }

                                    } while (!gameEnd);

                                    // Thông báo xếp hạng
                                    if (gameEnd) {
                                        System.out.println("XẾP HẠNG CỦA CÁC NGƯỜI CHƠI");

                                        for (int i = 0; i < rank.size(); i++) {
                                            msg = Alert.getAlertRank(rank.get(i));
                                            sa = this.players.get(i).getSocketAddress();

                                            System.out.println(sa + " \t: " + msg);

                                            baos = new ByteArrayOutputStream();
                                            oos = new ObjectOutputStream(baos);
                                            oos.writeObject(msg);
                                            oos.flush();
                                            data = baos.toByteArray();
                                            packet.setData(data);
                                            packet.setSocketAddress(sa);
                                            server.send(packet);
                                        }

                                        inGame = false;
                                    }

                                    break;
                            }

                            // out game
                            if (!inGame) {
                                break;
                            }

                        } else { // có tới trắng
                            // Hiển thị danh sách các người chơi tới trắng
                            for (int i = 0; i < capot.length; i++) {
                                if (capot[i]) {
                                    System.out.println(this.players.get(i).getSocketAddress());
                                }
                                // Gửi thông báo kết quả đến cho các người chơi
                                baos = new ByteArrayOutputStream();
                                oos = new ObjectOutputStream(baos);
                                oos.writeObject(Alert.getAlertCapot(capot[i]));
                                oos.flush();
                                data = baos.toByteArray();
                                packet.setData(data);
                                packet.setSocketAddress(this.players.get(i).getSocketAddress());
                                server.send(packet);
                            }
                            inGame = false;
                        }

                    }
                }
                int choose;
                do {
                    System.out.printf("\n\nNew game [y(%d)/n(%d)]: ",
                            0, 1);
                    choose = Tools.getInt();
                } while (choose < 0 || choose > 1);
                if (choose == 1) {
                    running = false;
                }
            }
            server.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.run();
    }

    /**
     * * 4 đôi thông có thể đánh ngoài vòng Trong khi chuyển lượt đánh kế tiếp
     * Nếu người chơi hiện tại không nằm trong vòng đấu Và người chơi trước có
     * đánh các lá bài chứa lá 2 thì kiểm tra xem người chơi có 4 đôi thông hay
     * không Nếu có thì người chơi đó được đánh Nếu không chuyển lượt tiếp
     * (Completed)
     */
    /**
     * *
     * Nếu các lá bài trước là của người chơi đó, thì người chơi đó không được
     * bỏ lượt Hay nếu là any hoặc any_format thì không được bỏ lượt vì kể cả
     * người đánh đầu tiên không được bỏ lượt (Completed)
     */
    /**
     * *
     * Ăn trắng(capot) Khi người chơi có các lá bài thuộc nhóm ăn trắng 1 thông
     * báo kết quả 2 kết thúc Cách giải quyết: sau khi chia bài, kiểm tra lần
     * lượt các lá bài của người chơi Nếu các lá bài thuộc nhóm ăn trắng thêm
     * người chơi vào danh sách ăn trắng Nếu có người ăn trắng, đặt biến là ăn
     * trắng thành true trước khi đánh bài, kiểm tra xem ăn trắng là true không,
     * nếu không đánh bình thường, nếu true thì gửi kết quả và kết thúc ván bài
     * (Completed)
     */
    /**
     * *
     * Rank: Nếu có 1 người tới trước mà số người còn lại chưa hết bài lớn hơn 1
     * Thì vẫn đánh tiếp Người nào tới trước người đó giành vị trí 1, tương tự 1
     * 2 3 4
     */
}
