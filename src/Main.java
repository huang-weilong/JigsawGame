import javax.swing.*;

/**
 *
 * @author huang-weilong
 */
public class Main {
    public static void main(String[] args) {
        Jigsaw game=new Jigsaw();
        game.JigsawInit();
        game.setLocation(400, 50);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
