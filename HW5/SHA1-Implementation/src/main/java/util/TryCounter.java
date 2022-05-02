package util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class TryCounter {
    public static Map<String, String> hashToTextSearch = new HashMap<>();
}
