package mx.itesm.plataformas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/**
 * Pantalla principal del juego, muestra un mapa y el personaje
 *
 * @author Roberto Martínez Román
 */
public class PantallaJuego implements Screen
{
    public static final float ANCHO_MAPA = 1280;   // Como se creó en Tiled

    // Referencia al objeto de tipo Game (tiene setScreen para cambiar de pantalla)
    private Plataforma plataforma;

    // La cámara y vista principal
    private OrthographicCamera camara;
    private Viewport vista;
    // Objeto para dibujar en la pantalla
    private SpriteBatch batch;

    // MAPA
    private TiledMap mapa;      // Información del mapa en memoria
    private OrthogonalTiledMapRenderer rendererMapa;    // Objeto para dibujar el mapa

    // Personaje
    private Texture texturaPersonaje;       // Aquí cargamos la imagen marioSprite.png con varios frames
    private Personaje mario;
    public static final int TAM_CELDA = 16;

    // HUD. Los componentes en la pantalla que no se mueven
    private OrthographicCamera camaraHUD;   // Cámara fija
    // Botones izquierda/derecha
    private Texture texturaBtnIzquierda;
    private Boton btnIzquierda;
    private Texture texturaBtnDerecha;
    private Boton btnDerecha;

    public PantallaJuego(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    @Override
    public void show() {
        // Crea la cámara/vista
        camara = new OrthographicCamera(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA);
        camara.position.set(Plataforma.ANCHO_CAMARA / 2, Plataforma.ALTO_CAMARA / 2, 0);
        camara.update();
        vista = new StretchViewport(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA, camara);

        batch = new SpriteBatch();

        // Cámara para HUD
        camaraHUD = new OrthographicCamera(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA);
        camaraHUD.position.set(Plataforma.ANCHO_CAMARA / 2, Plataforma.ALTO_CAMARA / 2, 0);
        camaraHUD.update();

        cargarRecursos();
        crearObjetos();

        // Indicar el objeto que atiende los eventos de touch (entrada en general)
        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    // Carga los recursos a través del administrador de assets
    private void cargarRecursos() {
        // Cargar las texturas/mapas
        AssetManager assetManager = plataforma.getAssetManager();   // Referencia al assetManager
        assetManager.load("Mapa.tmx", TiledMap.class);  // Cargar info del mapa
        assetManager.load("marioSprite.png", Texture.class);    // Cargar imagen
        // Texturas de los botones
        assetManager.load("derecha.png", Texture.class);
        assetManager.load("izquierda.png", Texture.class);

        // Se bloquea hasta que cargue todos los recursos
        assetManager.finishLoading();
    }

    private void crearObjetos() {
        AssetManager assetManager = plataforma.getAssetManager();   // Referencia al assetManager
        // Carga el mapa en memoria
        mapa = assetManager.get("Mapa.tmx");
        //mapa.getLayers().get(0).setVisible(false);
        // Crear el objeto que dibujará el mapa
        rendererMapa = new OrthogonalTiledMapRenderer(mapa,batch);
        rendererMapa.setView(camara);
        // Cargar frames
        texturaPersonaje = assetManager.get("marioSprite.png");
        // Crear el personaje
        mario = new Personaje(texturaPersonaje);
        // Posición inicial del personaje
        mario.getSprite().setPosition(Plataforma.ANCHO_CAMARA / 10, Plataforma.ALTO_CAMARA * 0.90f);

        // Crear los botones
        texturaBtnIzquierda = assetManager.get("izquierda.png");
        btnIzquierda = new Boton(texturaBtnIzquierda);
        btnIzquierda.setPosicion(TAM_CELDA, 5 * TAM_CELDA);
        btnIzquierda.setAlfa(0.7f); // Un poco de transparencia
        texturaBtnDerecha = assetManager.get("derecha.png");
        btnDerecha = new Boton(texturaBtnDerecha);
        btnDerecha.setPosicion(6 * TAM_CELDA, 5 * TAM_CELDA);
        btnDerecha.setAlfa(0.7f); // Un poco de transparencia
    }

    /*
    Dibuja TODOS los elementos del juego en la pantalla.
    Este método se está ejecutando muchas veces por segundo.
     */
    @Override
    public void render(float delta) { // delta es el tiempo entre frames (Gdx.graphics.getDeltaTime())
        // Leer entrada

        // Actualizar objetos en la pantalla
        moverPersonaje();
        actualizarCamara(); // Mover la cámara para que siga al personaje

        // Dibujar
        borrarPantalla();

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();  // Dibuja el mapa

        // Entre begin-end dibujamos nuestros objetos en pantalla
        batch.begin();

        mario.render(batch);    // Dibuja el personaje

        batch.end();

        // Dibuja el HUD
        batch.setProjectionMatrix(camaraHUD.combined);
        batch.begin();
        btnIzquierda.render(batch);
        btnDerecha.render(batch);
        batch.end();

    }

    // Actualiza la posición de la cámara para que el personaje esté en el centro,
    // excepto cuando esta en la primera y última parte del mundo
    private void actualizarCamara() {
        float posX = mario.getX();
        // Si está en la parte 'media'
        if (posX>=Plataforma.ANCHO_CAMARA/2 && posX<=ANCHO_MAPA-Plataforma.ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>ANCHO_MAPA-Plataforma.ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-Plataforma.ANCHO_CAMARA/2, camara.position.y, 0);
        }
        camara.update();
    }

    /*
    Mueve el personaje en Y hasta que se encuentre sobre un bloque
     */
    private void moverPersonaje() {
        switch (mario.getEstado()) {
            case INICIANDO:
                // Los bloques en el mapa son de 16x16
                // Calcula la celda donde estaría después de moverlo
                int celdaX = (int)(mario.getX()/ TAM_CELDA);
                int celdaY = (int)((mario.getY()+mario.VELOCIDAD_Y)/ TAM_CELDA);
                // Recuperamos la celda en esta posición
                // La capa 0 es el fondo
                TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
                TiledMapTileLayer.Cell celda = capa.getCell(celdaX, celdaY);
                // probar si la celda está ocupada
                if (celda==null) {
                    // Celda vacía, entonces el personaje puede avazar
                    mario.actualizar();
                } else {
                    // Dejarlo sobre la celda que lo detiene
                    mario.setPosicion(mario.getX(), (celdaY+1)* TAM_CELDA);
                    mario.setEstado(Personaje.Estado.QUIETO);
                }
                break;
            case MOV_DERECHA:       // Siempre se mueve
            case MOV_IZQUIERDA:
                mario.actualizar();
                break;
        }

    }

    private void borrarPantalla() {
        //Gdx.gl.glClearColor(1, 1, 1, 1);    // Color de fondo
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

    // Libera los assets
    @Override
    public void dispose() {
        texturaPersonaje.dispose();
        mapa.dispose();
        texturaBtnDerecha.dispose();
        texturaBtnIzquierda.dispose();
    }

    /*
    Clase utilizada para manejar los eventos de touch en la pantalla
     */
    public class ProcesadorEntrada extends InputAdapter
    {
        private Vector3 coordenadas = new Vector3();
        private float x, y;     // Las coordenadas en la pantalla virtual
        /*
        Se ejecuta cuando el usuario pone un dedo sobre la pantalla, los dos primeros parámetros
        son las coordenadas relativas a la pantalla física (0,0) en la esquina superior izquierda
        pointer - es el número de dedo que se pone en la pantalla, el primero es 0
        button - el botón del mouse
         */
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            transformarCoordenadas(screenX, screenY);
            // Preguntar si está dentro del botón derecho
            if (btnDerecha.contiene(x,y)) {
                // Tocó el botón derecha, hacer que el personaje se mueva a la derecha
                mario.setEstado(Personaje.Estado.MOV_DERECHA);
            } else if (btnIzquierda.contiene(x,y)) {
                // Tocó el botón izquierda, hacer que el personaje se mueva a la izquierda
                mario.setEstado(Personaje.Estado.MOV_IZQUIERDA);
            }
            return true;    // Indica que ya procesó el evento
        }

        private void transformarCoordenadas(int screenX, int screenY) {
            // Transformar las coordenadas de la pantalla física a la cámara HUD
            coordenadas.set(screenX, screenY, 0);
            camaraHUD.unproject(coordenadas);
            // Obtiene las coordenadas relativas a la pantalla virtual
            x = coordenadas.x;
            y = coordenadas.y;
        }
    }
}
