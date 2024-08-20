import javax.sound.sampled.Clip;
import java.awt.*;
import java.util.Random;

public class Asteroids {

    public static final int LARGURA_ASTEROIDE = 50;
    public static final int ALTURA_ASTEROIDE = 50;

    private int posicao_x;
    private int posicao_y;
    private double velocidade_x;
    private double velocidade_y;
    private Random random;
    private Image image;

    public Asteroids() {
        random = new Random();
        posicao_x = random.nextInt(Jogo.LARGURA_TELA);
        posicao_y = random.nextInt(Jogo.ALTURA_TELA);

        image = Recursos.obterImagem("asteroide");

        boolean movimentoNaDirecaoX = random.nextBoolean();

        if (movimentoNaDirecaoX) {
            velocidade_x = 2.5;
            velocidade_y = 0;
        } else {
            velocidade_x = 0;
            velocidade_y = 2.5;
        }
    }

    public void atualizarPosicao() {
        posicao_x += velocidade_x;
        posicao_y += velocidade_y;

        if (posicao_x < 0) {
            posicao_x = Jogo.LARGURA_TELA;
        } else if (posicao_x > Jogo.LARGURA_TELA) {
            posicao_x = 0;
        }

        if (posicao_y < 0) {
            posicao_y = Jogo.ALTURA_TELA;
        } else if (posicao_y > Jogo.ALTURA_TELA) {
            posicao_y = 0;
        }
    }

    public void criarNovoAsteroide() {
        int canto = random.nextInt(4);

        switch (canto) {
            case 0:
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2 + 1);
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2 + 1);
                break;
            case 1:
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2) + Jogo.LARGURA_TELA / 2;
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2);
                break;
            case 2:
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2);
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2) + Jogo.ALTURA_TELA / 2;
                break;
            case 3:
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2) + Jogo.LARGURA_TELA / 2;
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2) + Jogo.ALTURA_TELA / 2;
                break;
        }
    }

    public void Desenhar(Graphics g) {
        if (image != null) {
            g.drawImage(image, posicao_x, posicao_y, null);
        } else {
            // Alternativamente, desenhe uma imagem padrão ou uma mensagem
        }
    }

    public boolean Colisao(int tiro_x, int tiro_y) {
        return tiro_x >= posicao_x && tiro_x <= posicao_x + LARGURA_ASTEROIDE &&
                tiro_y >= posicao_y && tiro_y <= posicao_y + ALTURA_ASTEROIDE;
    }

    public void Atualizar() {
        int velocidadeMaxima = 3;

        int deslocamentoX = random.nextInt(velocidadeMaxima * 2 + 1) - velocidadeMaxima;
        int deslocamentoY = random.nextInt(velocidadeMaxima * 2 + 1) - velocidadeMaxima;

        posicao_x += deslocamentoX;
        posicao_y += deslocamentoY;

        if (posicao_x < 0) {
            posicao_x = 0;
        } else if (posicao_x > Jogo.LARGURA_TELA - (image != null ? image.getWidth(null) : 0)) {
            posicao_x = Jogo.LARGURA_TELA - (image != null ? image.getWidth(null) : 0);
        }

        if (posicao_y < 0) {
            posicao_y = 0;
        } else if (posicao_y > Jogo.ALTURA_TELA - (image != null ? image.getHeight(null) : 0)) {
            posicao_y = Jogo.ALTURA_TELA - (image != null ? image.getHeight(null) : 0);
        }
    }

    public void SomDeAcerto() {
        if (Colisao(posicao_x, posicao_y)) {
            Clip clip = Recursos.obterSom("erro");
            if (clip != null) {
                clip.setFramePosition(0); // Rewind to the beginning
                clip.start();
            } else {
                System.err.println("Áudio não encontrado!");
            }
        }
    }

    public int getX() {
        return posicao_x;
    }

    public int getY() {
        return posicao_y;
    }
}
