package TroChoiBaiTienLen.data;

import TroChoiBaiTienLen.gameplay.GamePlay;

public class Alert {
    // SERVER
    public static String HELLO = "Chào mừng bạn đến với trò chơi tiến lên";
    
    public static String getAlertEnd(boolean isWinner){
        String rs = isWinner?"giành chiến thắng" : "thua";
        return "Bạn đã " + rs;
    }
    
    public static String CAPOT = "Chúc mừng bạn đã may mắn tới trắng";
    
    public static String getAlertCapot(boolean isCapot){
        if(isCapot)
            return "Bạn đã may mắn ăn trắng ván này";
        return "Có người đã may mắn ăn trắng ván này, bạn đã thua";
    }
    
    public static String getAlertRank(int i){
        switch(i){
            case 0: return "Bạn về nhất";
            case 1: return "Bạn về nhì";
            case 2: return "Bạn về ba";
            case 3: return "Bạn về tư";
        }
        return "";
    }

    // CLIENT
    public static String INVALID_CARDS = "Các lá bài không hợp lệ";
    public static String TO_SKIP_TURN = "TO SKIP TURN (" + GamePlay.TO_SKIP_TURN + ")";
}
