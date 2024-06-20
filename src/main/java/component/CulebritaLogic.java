package component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;

public class CulebritaLogic extends Component {

    // head - body - ...
    private List<Entity> bodyParts = new ArrayList<>();
    public Pair<Double, Double> ubicacionAnterior = new Pair<>(0.0, 0.0);

    public void guardarUbicacion() {
        this.ubicacionAnterior = new Pair<>(entity.getX(), entity.getY());
    }

    public void crecer(Pair<Double, Double> pos) {

        //Point2D pos = lastBodyPart.getObject("prevPos");

        Point2D pos1 = new Point2D(ubicacionAnterior.getKey(), ubicacionAnterior.getValue());

        var body = spawn("cuerpito", pos1);

        //body.setPosition(pos.getKey(), pos.getValue());

        bodyParts.add(body);
    }

    @Override
    public void onAdded() {
        bodyParts.add(entity);
        Point2D pos = new Point2D(ubicacionAnterior.getKey(), ubicacionAnterior.getValue());
        // agrega la posicion anterior a la entidad como un atributo
        entity.setProperty("ubicacion", pos);
        //entity.setProperty("ubicacion", entity.getPosition());
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setProperty("ubicacion", entity.getPosition());

        for (int i = 1; i < bodyParts.size(); i++) {
            var prevPart = bodyParts.get(i - 1);
            var part = bodyParts.get(i);

            Point2D prevPos = prevPart.getObject("ubicacion");

            part.setProperty("ubicacion", part.getPosition());
            part.setPosition(prevPos);
        }
    }
}
