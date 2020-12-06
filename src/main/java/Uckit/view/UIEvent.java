package Uckit.view;

public class UIEvent {
    private ChangeArea area;
    String extraData;
    public UIEvent(ChangeArea area){
        this.area = area;
    }

    public UIEvent withExtraData(String value) {
        this.extraData = value;
        return this;
    }

    public ChangeArea getArea() {
        return area;
    }

    public String getExtraData() {
        return extraData;
    }
}

enum ChangeArea{
    THEME
}
