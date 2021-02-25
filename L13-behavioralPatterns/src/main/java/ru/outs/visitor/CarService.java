package ru.outs.visitor;

/**
 * @author sergey
 * created on 12.09.18.
 */
public class CarService implements Visitor {
    @Override
    public void visit(Engine item) {
        System.out.println(item.checkEngine());
    }

    @Override
    public void visit(Transmission item) {
        System.out.println(item.refreshOil());
    }

    @Override
    public void visit(Brake item) {
        System.out.println(item.replaceBrakePad());
    }
}
