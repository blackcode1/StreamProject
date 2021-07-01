package StreamTest;

import java.util.Date;

public class EndlessJar {
    public static void main(String[] args) throws Exception {
        Long t1 = new Date().getTime();
        while (true){
            Long t2 = new Date().getTime();
            if(t2 - t1 > 60000){
                break;
            }
            System.out.println(t2);
            Thread.sleep(10000);
        }
    }
}
