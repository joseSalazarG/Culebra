package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getUIFactory;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;

import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static steps.CulebritaFactory.EntityType.COMIDA;
import static steps.CulebritaFactory.EntityType.JUGADOR;
import static steps.CulebritaFactory.EntityType.MURO;

public class Hooks extends GameApplication {

    public Entity jugador;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private int puntos = 0;
    private Text puntosText;

    private final CulebritaFactory culebritaFactory = new CulebritaFactory();
    

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        settings.setTicksPerSecond(7); //fps
    }

    @Override
    protected void initGame() {

        GameWorld mapa = FXGL.getGameWorld();
        mapa.addEntityFactory(culebritaFactory);


        spawn("bosque");
        spawn("muroSuperior");
        spawn("muroInferior");
        spawn("muroIzquierdo");
        spawn("muroDerecho");

        this.jugador = spawn("jugador", 500, 150);
        spawn("comida");
        spawn("comida2");

        puntosText = getUIFactory().newText("Puntos: 0", Color.WHITE, 20);
        puntosText.setTranslateX(5);
        puntosText.setTranslateY(60);
        getGameScene().addUINode(puntosText);
    }
    

    //colision
    @Override
    public void initPhysics() {

        Sound comer = getAssetLoader().loadSound("necoarc.mp3");
        Sound morir = getAssetLoader().loadSound("muerte.mp3");

        //colision con comida
        FXGL.onCollisionBegin(JUGADOR, COMIDA, (jugador, comida) -> {
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(60, 600));
            getAudioPlayer().playSound(comer);
            jugador.getComponent(CulebritaLogic.class).grow();
            puntos++;
            puntosText.setText("Puntos: " + puntos);
        });

        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador, muro) -> {
            getAudioPlayer().playSound(morir);
            //morir, respawnear, eliminar cola
            jugador.getComponent(CulebritaLogic.class).die();
            puntos = 0;
            puntosText.setText("Puntos: " + puntos);
        });
    }

    //mueve al jugador
    @Override
    public void initInput() {

        onKey(KeyCode.LEFT, "Mover hacia la izquierda" ,() -> {
            jugador.getComponent(CulebritaLogic.class).izquierda();
        });

        onKey(KeyCode.RIGHT, "Mover hacia la derecha", () -> {
            jugador.getComponent(CulebritaLogic.class).derecha();
        });

        onKey(KeyCode.UP, "Mover hacia arriba", () -> {
            jugador.getComponent(CulebritaLogic.class).arriba();
        });

        onKey(KeyCode.DOWN, "Mover hacia abajo", () -> {
            jugador.getComponent(CulebritaLogic.class).abajo();
        });
    }

}