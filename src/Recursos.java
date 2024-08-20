import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Recursos {

    private static final Map<String, Image> imagens = new HashMap<>();
    private static final Map<String, Clip> sons = new HashMap<>();

    public static void carregarRecursos() {
        carregarImagens();
    }

    private static void carregarImagens() {
        carregarImagem("/res/teste2.png", "asteroide", 50, 50);
        // Adicione mais imagens conforme necessário
    }

    private static void carregarImagem(String caminho, String chave, int largura, int altura) {
        try (InputStream inputStream = Recursos.class.getResourceAsStream(caminho)) {
            if (inputStream != null) {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                BufferedImage imagemRedimensionada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
                Image img = bufferedImage.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                imagemRedimensionada.getGraphics().drawImage(img, 0, 0, null);
                imagens.put(chave, imagemRedimensionada);
            } else {
                System.err.println("Imagem não encontrada: " + caminho);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image obterImagem(String chave) {
        return imagens.get(chave);
    }

    public static Clip obterSom(String chave) {
        return sons.get(chave);
    }
}
