package atlas;

import java.io.IOException;

import atlas.Rules.RewriteRuleLoader;

public class Main {
    public static void main(String[] args) throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        System.out.println(loader.getRules().get("dislike"));
        System.out.println(loader.getRules().get("undermine"));
        System.out.println(loader.getRules().get("flunk"));
    }
}