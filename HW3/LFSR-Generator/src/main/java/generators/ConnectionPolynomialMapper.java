package generators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConnectionPolynomialMapper {
    public static Map<Integer, List<Integer>> connectionPolynomialMap = new HashMap<>();

    static {
        connectionPolynomialMap.put(4, List.of(1, 4));
        connectionPolynomialMap.put(8, List.of(1, 5, 6, 8));
        connectionPolynomialMap.put(16, List.of(2, 3, 5, 16));
        connectionPolynomialMap.put(32, List.of(1, 27, 28, 32));
    }
}
