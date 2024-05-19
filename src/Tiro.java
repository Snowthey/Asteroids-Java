import java.awt.*;

public class Tiro {
    private int x;
    private int y;
    private double velocidadeX;
    private double velocidadeY;
    private static final double VELOCIDADE_TIRO = 5.0; // Ajuste conforme necessário
    public static int largura;
    public static int altura;
    private Color cor;
    private double direcao;

    public Tiro(int x, int y, double anguloRadianos) {
        this.x = x;
        this.y = y;
        this.velocidadeX = Math.cos(anguloRadianos) * VELOCIDADE_TIRO;
        this.velocidadeY = Math.sin(anguloRadianos) * VELOCIDADE_TIRO;
        this.largura = 3;
        this.altura = 10;
        this.cor = Color.RED;
        this.direcao = direcao;
    }

    public void mover() {
        x += velocidadeX;
        y += velocidadeY;
    }

    public void desenhar(Graphics g) {
        g.setColor(cor);
        g.fillRect(x, y, largura, altura);
    }

    public boolean foraDaTela(int alturaTela) {
        return y < 0 || y > alturaTela;
    }

    public int getX() {
        return x;
    }

    // add getY() method
    public int getY() {
        return y;
    }

    public int getLargura(){
        return largura;
    }
    public int getAltura(){
        return altura;
    }
}
