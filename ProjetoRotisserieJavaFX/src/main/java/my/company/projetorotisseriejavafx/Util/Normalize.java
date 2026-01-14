package my.company.projetorotisseriejavafx.Util;

import java.text.Normalizer;

public class Normalize {
    public static String normalize(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
