package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGL.texture;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
public class Hooks extends GameApplication {

    //private Serpiente jugador;
    private Entity jugador;
    private Entity comida;
    private Entity wall;
    private Entity wall2;
    private Entity wall3;
    private Entity wall4;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private final int anchoJugador = 40;
    private final int altoJugador = 40;
    int vidas = 5;

    public enum EntityType {
        JUGADOR, COMIDA, WALL, WALL2, WALL3, WALL4
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        //settings.setDefaultLanguage(Language.SPANISH);
    }

    @Override
    protected void initGame() {

        GameWorld mapa = FXGL.getGameWorld();

        jugador = FXGL.entityBuilder()
                .type(EntityType.JUGADOR)
                .at(500, 150)
                .viewWithBBox(texture("neko.png", 80, 80))
                .collidable()
                .with(new AutoRotationComponent())
                .build();
                //.buildAndAttach();

        comida = FXGL.entityBuilder()
                .type(EntityType.COMIDA)
                .at(200, 150)
                .viewWithBBox(texture("sq.png", 60, 60))
                .collidable()
                .with(new AutoRotationComponent())
                .build();
                //.buildAndAttach(); 

        //derecha
        wall = FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(1350, 0)
                .viewWithBBox(new Rectangle(200, 700, javafx.scene.paint.Color.RED))
                .collidable()
                .build();
        //arriba
        wall2 = FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, 0)
                .viewWithBBox(new Rectangle(1400, 30, javafx.scene.paint.Color.RED))
                .collidable()
                .build();
        //abajo
        wall3 = FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, 670)
                .viewWithBBox(new Rectangle(1400, 50, javafx.scene.paint.Color.RED))
                .collidable()
                .build();

        //izquierda
        wall4 = FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, 0)
                .viewWithBBox(new Rectangle(50, 700, javafx.scene.paint.Color.RED))
                .collidable()
                .build();

        mapa.addEntities(jugador);
        mapa.addEntities(comida);
        mapa.addEntities(wall);
        mapa.addEntities(wall2);
        mapa.addEntities(wall3);
        mapa.addEntities(wall4);
    }
    
    //colision
    @Override
    protected void initPhysics() {
        //colision con comida
        Sound comer = getAssetLoader().loadSound("necoarc.mp3");
        Sound morir = getAssetLoader().loadSound("muerte.mp3");
        FXGL.onCollisionBegin(EntityType.JUGADOR, EntityType.COMIDA, (jugador, comida) -> {
            comida.setPosition(FXGLMath.random(0, 1300 - 600), FXGLMath.random(0, 1300 - 600));
            getAudioPlayer().playSound(comer);
        });
       
        EntityType[] muros = {EntityType.WALL, EntityType.WALL2, EntityType.WALL3, EntityType.WALL4};

        for (EntityType muro : muros) {
            FXGL.onCollisionBegin(EntityType.JUGADOR, muro, (jugador, wall) -> {
                getAudioPlayer().playSound(morir);
                vidas--;
                jugador.setPosition(500,150);
                if (vidas == 0) {
                    jugador.removeFromWorld();
                }
            });
        }
    }

    //mueve al jugador
    @Override
    protected void initInput() {

        Input input = FXGL.getInput();

        input.addAction(new UserAction("Mover hacia arriba") {
            @Override
            protected void onAction() {
                if (jugador.getY() > 0) {
                    jugador.translateY(-2.5); // se mueve arriba
                }
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Mover hacia abajo") {
            @Override
            protected void onAction() {
                if (jugador.getY() < altoPantalla-altoJugador) {
                    jugador.translateY(2.5); // se mueve abajo
                }
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Mover hacia la izquierda") {
            @Override
            protected void onAction() {
                if (jugador.getX() > 0) {
                    jugador.translateX(-2.5); // se mueve a la izquierda
                }
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Mover hacia la derecha") {
            @Override
            protected void onAction() {
                if (jugador.getX() < anchoPantalla-anchoJugador) {
                    jugador.translateX(2.5); // se mueve a la derecha
                }
            }
        }, KeyCode.RIGHT);

    }
}
