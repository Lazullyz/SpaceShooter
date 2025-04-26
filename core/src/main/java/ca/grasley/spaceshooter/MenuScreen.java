package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont font;

    public MenuScreen(MyGame game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont(); // Fonte básica
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Toque na tela para jogar", 100, 150);
        batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen()); // Troca para o GameScreen
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    // Os outros métodos (resize, pause, resume, etc) podem ficar vazios se quiser
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

