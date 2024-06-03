import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InterrupcaoTeclado extends KeyAdapter {
    private Nave nave;

    public InterrupcaoTeclado(Nave nave) {
        this.nave = nave;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            Jogo.Mutex.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                nave.acelerar();
                break;
            case KeyEvent.VK_A:
                nave.girarEsquerda();
                break;
            case KeyEvent.VK_D:
                nave.girarDireita();
                break;
        }

        Jogo.Mutex.release();
    }


    @Override
    public void keyReleased(KeyEvent e) {
        try {
            Jogo.Mutex.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                nave.desacelerar();
                break;
        }

        Jogo.Mutex.release();
    }


}
