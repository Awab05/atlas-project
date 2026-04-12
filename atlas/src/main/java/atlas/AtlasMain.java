package atlas;
import java.util.HashMap;

import atlas.Mapping.Mapping;

public class AtlasMain {
    public static void main(String[] args) {
        AtlasPrinter printer = new AtlasPrinter();
        AtlasParser parse = new AtlasParser();
        String input = "(work in scientist (some lab (that (conduct experiment))))" ;

        AtlasNode root = parse.parse(input);

        System.out.println("Flat:");
        System.out.println(root.toString());
        System.out.println(printer.AtlasToFlatString(root));

        System.out.println("\nPretty:");
        System.out.println(printer.toPrettyString(root));

        Mapping mapping = new Mapping();
        String flat1 = "(serve *priest (some congregation (that (perform (for (some god)) (some worship)))))";
        String flat2 = "(serve *soldier (some army (that (perform (for (some leader)) (some conquest)))))";
        HashMap<String,String> v = mapping.mapTwoFlatStrings(flat1, flat2);
        System.out.println(v);
    }
}