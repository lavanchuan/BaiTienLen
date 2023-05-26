package TroChoiBaiTienLen.model;

import TroChoiBaiTienLen.gameplay.GamePlay;
import java.util.Random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Tools {

    public static int ONE_BILION = 1000000000;
    static Random random = new Random();

    public static int getRandomNumber(int min, int max) {
        int rd = random.nextInt(ONE_BILION);
        return min + rd % (max - min + 1);
    }

    public static int getInt() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int result = -1;
        boolean flag = false;
        while (!flag) {
            try {
                result = Integer.parseInt(br.readLine());
                flag = true;
            } catch (IOException e) {

            } catch (NumberFormatException e) {

            }
        }
        return result;
    }

    public static String getText() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String result = "";
        boolean flag = false;
        try {
            result = br.readLine();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    // CHECK STRING:
    public static boolean checkString(String text, int maxItem, int limitElements) {
        if(text.length() == 0)
            return false;
        // Nếu có 1 phần tử và dạng -a
        if (text.split(" ").length == 1) {
            try {
                if (Integer.parseInt(text.split(" ")[0]) == GamePlay.TO_SKIP_TURN) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        
        // Nếu ký tự trong chuỗi không phải là số, khoảng trắng, dấu gạch(-) nối return false;
        boolean isHasHyphen = false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' '
                    || text.charAt(i) == '-'
                    || (text.charAt(i) <= '9' && text.charAt(i) >= '0')) {
                if (text.charAt(i) == '-') {
                    isHasHyphen = true;
                }
                continue;
            } else {
                return false;
            }
        }
        
        // Nếu chuỗi không chứa dấu gạch nối
        if (!isHasHyphen) {
            String elements[] = text.split(" ");
            // Số lượng phần tử không được vượt số lượng giới hạn(lấy chỉ số)
            if (elements.length > limitElements) {
                return false;
            }
            // Các phần tử không được lớn hơn giới hạn
            for (String s : elements) {
                try {
                    if (Integer.parseInt(s) > maxItem) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            // các phần tử không được trùng nhau(duy nhất)
            for (int i = 0; i < elements.length - 1; i++) {
                for (int j = i + 1; j < elements.length; j++) {
                    if (elements[i].equals(elements[j])) {
                        return false;
                    }
                }
            }
            return true;
        }
        // Nếu có chứa ký tự gạch nối, thì chỉ chứa tối đa là 1 ký tự gạch nối
        if (text.lastIndexOf('-') != text.indexOf('-')) {
            return false;
        }

        // số phần tử là 3
        String[] elements = text.split(" ");
        if (elements.length != 3) {
            return false;
        }
        // Dấu gạch nối nằm giữa 2 số
        if (!elements[1].equals("-")) {
            return false;
        }

        // Số thứ nhất bé hơn số thứ hai
        if (Integer.parseInt(elements[0]) >= Integer.parseInt(elements[2])) {
            return false;
        }

        // Cả 2 số điều bé hơn hoặc bằng max item
        if (Integer.parseInt(elements[0]) > maxItem
                || Integer.parseInt(elements[2]) > maxItem) {
            return false;
        }

        return true;
    }

    // TEST
    public static void main(String[] args) {
        String[] textCase = new String[100];
        int i = 0;
        textCase[i++] = "a b c 1 2 3 * - ";
        textCase[i++] = " - + x a 1 2 3 ";
        textCase[i++] = "1 2 - 2 1";
        textCase[i++] = "1 - - 2";
        textCase[i++] = "1 - 2";
        textCase[i++] = "1 - 2 3";
        textCase[i++] = "1 13";
        textCase[i++] = "0 - 13";
        textCase[i++] = "- - 1 2";
        textCase[i++] = "10 8";
        textCase[i++] = "2 3 1 7 2 1 0";
        textCase[i++] = "2 3 3 2";
        textCase[i++] = "2 - 1";
        textCase[i++] = "0 - 9";
        textCase[i++] = "-1 2 3 6 0";
        textCase[i++] = "-1 3 3";
        textCase[i++] = "-1 3";
        textCase[i++] = "- 3 3";
        textCase[i++] = "-a";
        textCase[i++] = "0 - 13";
        textCase[i++] = "12 - 12";
        textCase[i++] = "01";
        textCase[i++] = "";

        for (String s : textCase) {
            if (s != null) {
                System.out.println(s + " =>\t\t\t\t" + checkString(s, 0, 1));
                System.out.println();
            }
        }
    }
}
