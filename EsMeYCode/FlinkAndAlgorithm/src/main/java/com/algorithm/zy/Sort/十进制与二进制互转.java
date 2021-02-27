public class 十进制与二进制互转 {
    public static void main(String[] args) {
//        int asc = 'Z';
//        System.out.println("ASCII: " + asc);
//        System.out.println(4999 + 1000 - 4999 % 1000);
        int test = 50;
        String binary = "110010";
        System.out.println("原数：" + test);
        System.out.println("十进制整数转二进制字符串：" + Integer.toBinaryString(test));
        System.out.println("二进制字符串转十进制整数：" + Integer.valueOf(binary, 2));
    }
}
