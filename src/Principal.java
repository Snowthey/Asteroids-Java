import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        Jogo game = new Jogo();
        JFrame janelaprincipal = new JFrame("Asteroids");

        janelaprincipal.add(game);
        janelaprincipal.pack();
        janelaprincipal.setLocationRelativeTo(null);
        janelaprincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaprincipal.setResizable(false);
        janelaprincipal.setVisible(true);

        new Thread(game).start();
    }
}
