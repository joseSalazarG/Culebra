package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import static com.almasb.fxgl.dsl.FXGL.*;
import static steps.CulebritaFactory.EntityType.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Hooks extends GameApplication {

    private Entity jugador1;
    private Entity jugador2;
    private Input clientInput;
    public SpawnData data;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Text puntosText;
    //multiplayer
    private Connection<Bundle> conexion;
    private Server<Bundle> server;
    //public Client<Bundle> client
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
    }

    private void initCollisions() {

        Sound comer = getAssetLoader().loadSound("necoarc.mp3");
        Sound morir = getAssetLoader().loadSound("muerte.mp3");

        //TODO colision con cola para P1 y P2

        //colision con comida
        FXGL.onCollisionBegin(JUGADOR, COMIDA, (jugador1, comida) -> {
            //getAudioPlayer().playSound(comer);
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(60, 600));
            data = new SpawnData();
            data.put("ubicacion", jugador1.getPosition());
            jugador1.getComponent(CulebritaLogic.class).crecer(data, conexion);
           // getService(MultiplayerService.class).spawn(conexion, , "cola");
        });

        FXGL.onCollisionBegin(JUGADOR, COMIDA, (jugador2, comida) -> {
            //getAudioPlayer().playSound(comer);
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(60, 600));
            data = new SpawnData();
            data.put("ubicacion", jugador2.getPosition());
            jugador2.getComponent(CulebritaLogic.class).crecer(data, conexion);
        });

        // la muerte de la culebrita
        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador1, muro) -> {
            //getAudioPlayer().playSound(morir);
            jugador1.getComponent(CulebritaLogic.class).respawnear();
        });

        // la muerte de la culebrita
        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador2, muro) -> {
            //getAudioPlayer().playSound(morir);
            jugador2.getComponent(CulebritaLogic.class).respawnear();
        });

        //colision entre jugadores
        FXGL.onCollisionBegin(JUGADOR, JUGADOR, (jugador1, jugador2) -> {
            //getAudioPlayer().playSound(morir);
            jugador1.getComponent(CulebritaLogic.class).respawnear();
            jugador2.getComponent(CulebritaLogic.class).respawnear();
        });
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

        onKeyBuilder(clientInput, KeyCode.A).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).izquierda();
        });

        onKeyBuilder(clientInput, KeyCode.D).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).derecha();
        });

        onKeyBuilder(clientInput, KeyCode.W).onAction(() -> {
            jugador2.getComponent(CulebritaLogic.class).arriba();
        });

        onKeyBuilder(clientInput, KeyCode.S).onAction(() -> {
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