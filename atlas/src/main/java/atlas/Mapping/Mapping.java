package atlas.Mapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Mapping {

    public HashMap<String,String> mapTwoFlatStrings(String flat1, String flat2){
        List<String> token1 = parseFlatString(flat1);
        List<String> token2 = parseFlatString(flat2);

        HashMap<String,String> map = new HashMap<>();
        HashMap<String, String> reverseMap = new HashMap<>();

        if (token1.size() != token2.size()) {
            throw new IllegalArgumentException("Flats do not match");
        }

        for (int i = 0; i < token1.size(); i++) {
            String t1 =  token1.get(i);
            String t2 =  token2.get(i);

            if (!t1.equals(t2)) {
                if ((t1.charAt(0) == '*' && t2.charAt(0) != '*') ||
                        (t1.charAt(0) != '*' && t2.charAt(0) == '*') ||
                        (t1.equals("(") != t2.equals("(")) ||
                        (t1.equals(")") != t2.equals(")"))) {
                    throw new IllegalArgumentException("Flats do not match");
                }

                // -- Both Forward and Reverse consistency checking (ensuring 1-1 mapping) -- //
                if (map.containsKey(t2) && !map.get(t2).equals(t1)) {
                    throw new IllegalArgumentException("Conflicting mapping for " + t2);
                }

                if (reverseMap.containsKey(t1) && !reverseMap.get(t1).equals(t2)) {
                    throw new IllegalArgumentException("Mapping not one-to-one for " + t1);
                }

                map.put(t2, t1);
                reverseMap.put(t1, t2);
            }
        }
        return map;
    }

    private List<String> parseFlatString(String flatString){
        if (flatString == null || flatString.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        String clean = flatString.replace("(", " ( ").replace(")", " ) ");

        return Arrays.asList(clean.trim().split("\\s+"));
    }
}
