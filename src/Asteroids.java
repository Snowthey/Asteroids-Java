import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Asteroids {

    public static final int LARGURA_ASTEROIDE = 50; // Largura do asteroide
    public static final int ALTURA_ASTEROIDE = 50; // Altura do asteroide


    private int posicao_x;
    private int posicao_y;
    private double velocidade_x;
    private double velocidade_y;
    private Random random;
    private ImageIcon imageIcon;
    private Image image;


    public Asteroids(){
        random = new Random();
        posicao_x = random.nextInt(Jogo.LARGURA_TELA);
        posicao_y = random.nextInt(Jogo.ALTURA_TELA);
        imageIcon = new ImageIcon("C:/Users/sherl/IdeaProjects/Asteroids/src/teste2.png");
        image = imageIcon.getImage().getScaledInstance(LARGURA_ASTEROIDE, ALTURA_ASTEROIDE, Image.SCALE_DEFAULT); // Redimensiona a imagem para caber na tela

        boolean movimentoNaDirecaoX = random.nextBoolean();


        if (movimentoNaDirecaoX) {
            velocidade_x = 1.5; // Velocidade fixa no eixo X
            velocidade_y = 0; // Velocidade zero no eixo Y
        } else {
            velocidade_x = 0; // Velocidade zero no eixo X
            velocidade_y = 1.5; // Velocidade fixa no eixo Y
        }

    }

    public void atualizarPosicao() {
        // Atualiza a posição do asteroide com base na velocidade
        posicao_x += velocidade_x;
        posicao_y += velocidade_y;

        // Verifica se o asteroide atingiu os limites da tela e o faz voltar para o outro lado
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
        // Define a posição inicial do asteroide em um canto aleatório da tela
        int canto = random.nextInt(4); // Escolhe um número aleatório entre 0 e 3, representando os quatro cantos da tela

        switch (canto) {
            case 0: // Canto superior esquerdo
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2);
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2);
                break;
            case 1: // Canto superior direito
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2) + Jogo.LARGURA_TELA / 2;
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2);
                break;
            case 2: // Canto inferior esquerdo
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2);
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2) + Jogo.ALTURA_TELA / 2;
                break;
            case 3: // Canto inferior direito
                posicao_x = random.nextInt(Jogo.LARGURA_TELA / 2) + Jogo.LARGURA_TELA / 2;
                posicao_y = random.nextInt(Jogo.ALTURA_TELA / 2) + Jogo.ALTURA_TELA / 2;
                break;
        }
    }

    public void Desenhar(Graphics g){
        g.drawImage(image, posicao_x, posicao_y, null);
    }

    public boolean Colisao(int tiro_x, int tiro_y) {
        if (tiro_x >= posicao_x && tiro_x <= posicao_x + LARGURA_ASTEROIDE &&
                tiro_y >= posicao_y && tiro_y <= posicao_y + ALTURA_ASTEROIDE) {
            return true;
        }
        return false;
    }

    public void Atualizar() {
        // Definir a velocidade máxima de movimento do asteroide
        int velocidadeMaxima = 3;

        // Gerar um deslocamento aleatório para a posição x e y do asteroide dentro da velocidade máxima
        int deslocamentoX = random.nextInt(velocidadeMaxima * 2 + 1) - velocidadeMaxima;
        int deslocamentoY = random.nextInt(velocidadeMaxima * 2 + 1) - velocidadeMaxima;

        // Atualizar a posição do asteroide somando o deslocamento
        posicao_x += deslocamentoX;
        posicao_y += deslocamentoY;

        // Verificar se o asteroide ultrapassou os limites da tela e corrigir sua posição se necessário
        if (posicao_x < 0) {
            posicao_x = 0;
        } else if (posicao_x > Jogo.LARGURA_TELA - imageIcon.getIconWidth()) {
            posicao_x = Jogo.LARGURA_TELA - imageIcon.getIconWidth();
        }

        if (posicao_y < 0) {
            posicao_y = 0;
        } else if (posicao_y > Jogo.ALTURA_TELA - imageIcon.getIconHeight()) {
            posicao_y = Jogo.ALTURA_TELA - imageIcon.getIconHeight();
        }
    }

    public void SomDeAcerto() {
        try {
            if (Colisao(posicao_x, posicao_y)) {
                File arquivo = new File("C:/Users/sherl/IdeaProjects/Cobrinha/src/erro.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(arquivo);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return posicao_x;
    }

    // add getY() method
    public int getY() {
        return posicao_y;
    }
}
