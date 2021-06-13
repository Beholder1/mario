package view;

public enum WyborWMenu {
    GRAJ(0),
    STEROWANIE(1),
    TWORCY(2);

    private final int wiersz;
    WyborWMenu(int wiersz){
        this.wiersz = wiersz;
    }

    public WyborWMenu zwrocWybor(int wybor){
        if(wybor == 0)
            return GRAJ;
        else if(wybor == 1)
            return STEROWANIE;
        else if(wybor == 2)
            return TWORCY;
        else return null;
    }

    public WyborWMenu wybierz(boolean czyWGore){
        int wybor;

        if(wiersz > -1 && wiersz < 3){
            wybor = wiersz - (czyWGore ? 1 : -1);
            if(wybor == -1)
                wybor = 2;
            else if(wybor == 3)
                wybor = 0;
            return zwrocWybor(wybor);
        }

        return null;
    }

    public int getLineNumber() {
        return wiersz;
    }
}
