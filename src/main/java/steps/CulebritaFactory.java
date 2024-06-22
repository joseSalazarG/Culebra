package steps;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import component.CulebritaLogic;

public class CulebritaFactory implements EntityFactory {


    public enum EntityType {
        JUGADOR, COLA, COMIDA, MURO
    }

    // create a list of entities


    @Spawns("cuerpito")
    public Entity crearCuerpo(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.COLA)
                .viewWithBBox(texture("neko.png", 40, 40))
                .collidable()
                .build();

    }

    @Spawns("jugador")
    public Entity nuevoJugador(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.JUGADOR)
                .viewWithBBox(texture("neko.png", 40, 40))
                .collidable()
                //.with(new AutoRotationComponent())
                .with(new CulebritaLogic())
                .build();
    }

    @Spawns("cola")
    public Entity agregarCola(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.COLA)
                .viewWithBBox(texture("neko.png", 40, 40))
                .collidable()
                .with(new AutoRotationComponent())
                .build();
    }

    @Spawns("bosque")
    public Entity generarBosque(SpawnData data) {
        return entityBuilder(data)
                .viewWithBBox("bosque.jpg")
                .build();
    }

   @Spawns("comida")
   public Entity generarComida(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.COMIDA)
                .at(200, 150)
                .viewWithBBox(texture("sq.png", 30, 30))
                .collidable()
                .with(new AutoRotationComponent())
                .build();
   }

    @Spawns("muroSuperior")
    public Entity crearMuroSuperior(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.MURO)
                .at(100, -40)
                .viewWithBBox(texture("muro_Sup.png", 1200, 100))
                .collidable()
                .build();
    }

    @Spawns("muroInferior")
    public Entity crearMuroInferior(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.MURO)
                .at(100, 640)
                .viewWithBBox(texture("muro_Inf.png", 1200, 100))
                .collidable()
                .build();
    }

    @Spawns("muroIzquierdo")
    public Entity crearMuroIzq(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.MURO)
                .at(-60, 80)
                .viewWithBBox(texture("muro_Izq.png", 150, 550))
                .collidable()
                .build();
    }

   @Spawns("muroDerecho")
   public Entity crearMuroDer(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.MURO)
                .at(1320, 70)
                .viewWithBBox(texture("muro_Der.png", 150, 550))
                .collidable()
                .build();
   }

}
