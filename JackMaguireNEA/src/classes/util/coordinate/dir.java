package classes.util.coordinate;

public enum dir { //direction enumerator

    N,
    S,
    E,
    W,
    Nothing
    ;

    @Override
    public String toString() { //toString with full names
        switch (this) {
            case W -> {
                return "West";
            }
            case S -> {
                return "South";
            }
            case E -> {
                return "East";
            }
            case N -> {
                return "North"; //JDK 13 switch syntax
            }
            case Nothing -> {
                return "null, but not actually";
            }
        }
        return "WHAT! HOW DID YOU DO THIS??????????????"; //confused error message
    }


}
