package steps;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getExecutor;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.getNetService;
import static com.almasb.fxgl.dsl.FXGL.getService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.getWorldProperties;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.onKeyBuilder;
import static com.almasb.fxgl.dsl.FXGL.runOnce;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;

import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import static steps.CulebritaFactory.EntityType.COMIDA;
import static steps.CulebritaFactory.EntityType.JUGADOR;
import static steps.CulebritaFactory.EntityType.MURO;
import static steps.CulebritaFactory.EntityType.COLA;
import static steps.CulebritaFactory.EntityType.COLA2;


public class Hooks extends GameApplication {

    private Entity jugador1;
    private Entity jugador2;
    private Input clientInput;
    public SpawnData data;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    
    //multiplayer
    private Connection<Bundle> conexion;
    private boolean isServer;
    

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        settings.setTicksPerSecond(7); //fps
        settings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("puntosNeko", 0);
        vars.put("puntosPikachu", 0); 
    }

    @Override
    protected void initGame() {
        runOnce(() -> {
            getDialogService().showConfirmationBox("¿Crear un servidor?", respuesta -> {

                getGameWorld().addEntityFactory(new CulebritaFactory());

                isServer = respuesta;

                if (respuesta) {
                    var server = getNetService().newTCPServer(55555);
                    server.setOnConnected(conn -> {
                        conexion = conn;
                        getExecutor().startAsyncFX(() -> onServer());
                    });

                    System.out.println("Servidor creado");
                    server.startAsync();

                } else {
                    var client = getNetService().newTCPClient("localhost", 55555);
                    client.setOnConnected(conn -> {
                        conexion = conn;
                        getExecutor().startAsyncFX(() -> onClient());
                    });

                    System.out.println("Cliente conectado");
                    client.connectAsync();
                    
                }
            });
        }, Duration.seconds(0.001));
    }

    private void onServer() {

        Entity bosque = spawn("bosque");
        getService(MultiplayerService.class).spawn(conexion, bosque, "bosque");

        Entity comida = spawn("comida");
        getService(MultiplayerService.class).spawn(conexion, comida, "comida");

        comida = spawn("comida2");
        getService(MultiplayerService.class).spawn(conexion, comida, "comida2");

        Entity muro = spawn("muroSuperior");
        getService(MultiplayerService.class).spawn(conexion, muro, "muroSuperior");
        muro = spawn("muroInferior");
        getService(MultiplayerService.class).spawn(conexion, muro, "muroInferior");
        muro = spawn("muroIzquierdo");
        getService(MultiplayerService.class).spawn(conexion, muro, "muroIzquierdo");
        muro = spawn("muroDerecho");
        getService(MultiplayerService.class).spawn(conexion, muro, "muroDerecho");

        // genera al jugador 1
        data = new SpawnData(500, 150);
        jugador1 = spawn("jugador1", data);
        getService(MultiplayerService.class).spawn(conexion, jugador1, "jugador1");
 
        // genera al jugador 2
        data = new SpawnData(500, 500);
        jugador2 = spawn("jugador2", data);
        getService(MultiplayerService.class).spawn(conexion, jugador2, "jugador2");
        
        getService(MultiplayerService.class).addInputReplicationReceiver(conexion, clientInput);
        // inicializa las colisiones
        initCollisions();
    }

    private void onClient() {
        // solo para que no falle
        jugador1 = new Entity();
        jugador1.addComponent(new CulebritaLogic());
        
        getService(MultiplayerService.class).addEntityReplicationReceiver(conexion, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(conexion, getInput());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(conexion, getWorldProperties());
    }

    private void initCollisions() {
        Sound comer = getAssetLoader().loadSound("necoarc.mp3");
        Sound morir = getAssetLoader().loadSound("muerte.mp3");
        Sound pikadeath = getAssetLoader().loadSound("pikadeath.mp3");
        Sound pikacomer = getAssetLoader().loadSound("pikacomer.mp3");

        //colision con comida
        FXGL.onCollisionBegin(JUGADOR, COMIDA, (jugador, comida) -> {
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(60, 600));
            data = new SpawnData();
            int puntajePikachu = getWorldProperties().getInt("puntosPikachu");
            int puntajeNeko = getWorldProperties().getInt("puntosNeko");
            getService(MultiplayerService.class).addPropertyReplicationSender(conexion, getWorldProperties());
            
            if (jugador == jugador2) {
                jugador2.getComponent(CulebritaLogic.class).crecer2(data, conexion);
                getWorldProperties().increment("puntosPikachu", +1);
                getAudioPlayer().playSound(pikacomer);
                
                if (puntajePikachu >= 10) {
                    getDialogService().showMessageBox("!Jugador Pikachu ha ganado!", () -> {
                        //getGameController().exit();
                    });
                    
                }
            } 
            else {
                jugador1.getComponent(CulebritaLogic.class).crecer(data);
                getWorldProperties().increment("puntosNeko", +1);
                getAudioPlayer().playSound(comer);
                if (puntajeNeko >= 10) {
                    getDialogService().showMessageBox("!Jugador Neko ha ganado!", () -> {
                        //getGameController().exit();
                    });
                }  
            }
        });

        // la muerte de la culebrita
        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador, muro) -> {
            if (jugador == jugador2) {
                jugador2.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosPikachu", -1);
                getAudioPlayer().playSound(pikadeath);
            } else {
                jugador1.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosNeko", -1);
                getAudioPlayer().playSound(morir);
            }
        });

        //colision entre jugadores
        FXGL.onCollisionBegin(JUGADOR, JUGADOR, (jugador, jugador2) -> {
            if (jugador == jugador2) {
                jugador2.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosPikachu", -1);
                getAudioPlayer().playSound(pikadeath);
            } else {
                jugador1.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosNeko", -1);
                getAudioPlayer().playSound(morir);
            }
        });

        //colision entre jugador y cola
        FXGL.onCollisionBegin(JUGADOR, COLA2, (jugador, cola) -> {
            if (jugador == jugador1) {
                jugador1.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosNeko", -1);
                getAudioPlayer().playSound(morir);
            }
        });

        FXGL.onCollisionBegin(JUGADOR, COLA, (jugador, cola) -> {
            if (jugador == jugador2) {
                jugador2.getComponent(CulebritaLogic.class).respawnear();
                getWorldProperties().increment("puntosPikachu", -1);
                getAudioPlayer().playSound(pikadeath);
            }
        });
    }

    @Override
    protected void initUI() {
        Text puntosJugador1 = getUIFactoryService().newText("", Color.BLACK, 22);
        Text puntosJugador2 = getUIFactoryService().newText("", Color.BLACK, 22);

        puntosJugador1.setTranslateX(50);
        puntosJugador1.setTranslateY(50);

        puntosJugador2.setTranslateX(1350);
        puntosJugador2.setTranslateY(50);

        puntosJugador1.textProperty().bind(getWorldProperties().intProperty("puntosNeko").asString());
        puntosJugador2.textProperty().bind(getWorldProperties().intProperty("puntosPikachu").asString());

        getGameScene().addUINodes(puntosJugador1, puntosJugador2);
    }

    //mueve al jugador
    @Override
    public void initInput() {
        //JUGADOR 1
        onKey(KeyCode.LEFT, "Mover hacia la izquierda" ,() -> {
            jugador1.getComponent(CulebritaLogic.class).izquierda();
        });

        onKey(KeyCode.RIGHT, "Mover hacia la derecha", () -> {
            jugador1.getComponent(CulebritaLogic.class).derecha();
        });

        onKey(KeyCode.UP, "Mover hacia arriba", () -> {
            jugador1.getComponent(CulebritaLogic.class).arriba();
        });

        onKey(KeyCode.DOWN, "Mover hacia abajo", () -> {
            jugador1.getComponent(CulebritaLogic.class).abajo();
        });

        //JUGADOR 2
        clientInput = new Input();

        onKeyBuilder(clientInput, KeyCode.LEFT).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).izquierda();
        });

        onKeyBuilder(clientInput, KeyCode.RIGHT).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).derecha();
        });

        onKeyBuilder(clientInput, KeyCode.UP).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).arriba();
        });

        onKeyBuilder(clientInput, KeyCode.DOWN).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).abajo();
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        if (isServer) {
            clientInput.update(tpf);
        }
    }
}

