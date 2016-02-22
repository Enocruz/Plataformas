package mx.itesm.plataformas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by rmroman on 22/02/16.
 */
public class PantallaJuego implements Screen
{
    private Plataforma plataforma;

    private OrthographicCamera camara;
    private Viewport vista;
    private SpriteBatch batch;

    public PantallaJuego(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    @Override
    public void show() {
        camara = new OrthographicCamera(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA);
        camara.position.set(Plataforma.ANCHO_CAMARA / 2, Plataforma.ALTO_CAMARA / 2, 0);
        camara.update();
        vista = new StretchViewport(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA, camara);

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();

        batch.end();
    }

    private void borrarPantalla() {
        Gdx.gl.glClearColor(107 / 255f, 140f / 255, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
