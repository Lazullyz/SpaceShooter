package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import java.util.Random;

public class MyGame extends Game {
    public static Random random = new Random();

    @Override
    public void create() {
        setScreen(new MenuScreen(this));  // Chama o MenuScreen na inicialização
    }

    @Override
    public void dispose() {
        super.dispose();  // O Game já cuida do dispose da tela atual
    }

    @Override
    public void render() {
        super.render();  // O Game já cuida do render da tela atual
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);  // O Game já cuida do resize da tela atual
    }
}
