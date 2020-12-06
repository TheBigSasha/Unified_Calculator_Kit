package Uckit.view;

import Uckit.model.Variable;

public class UIEvent {
    private ChangeArea area;
    String extraData;
    Variable extraVar;
    public UIEvent(ChangeArea area){
        this.area = area;
    }

    public UIEvent withExtraData(String value) {
        this.extraData = value;
        return this;
    }

    public UIEvent withExtraData(Variable value) {
        this.extraVar = value;
        return this;
    }

    public ChangeArea getArea() {
        return area;
    }

    public String getExtraData() {
        return extraData;
    }

    public Variable getVar() {
        return extraVar;
    }
}

