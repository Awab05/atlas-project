package atlas;

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
    }
}