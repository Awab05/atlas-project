package atlas;

import java.util.*;

public class Mapping {

    public HashMap<String,String> mapTwoFlatStrings(String flat1, String flat2){
        List<String> token1 = parseFlatString(flat1);
        List<String> token2 = parseFlatString(flat2);
        HashMap<String,String> map = new HashMap<>();
        if (token1.size() != token2.size()) {
            throw new IllegalArgumentException("Flats do not match");
        }

        for (int i = 0; i < token1.size(); i++) {
            if (!token1.get(i).equals(token2.get(i))) {
                if ((token1.get(i).charAt(0) == '*' && token2.get(i).charAt(0) != '*') ||
                        (token1.get(i).charAt(0) != '*' && token2.get(i).charAt(0) == '*') ||
                        (token1.get(i).equals("(") != token2.get(i).equals("(")) ||
                        (token1.get(i).equals(")") != token2.get(i).equals(")"))) {
                    throw new IllegalArgumentException("Flats do not match");
                }
                map.put(token2.get(i), token1.get(i));

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
