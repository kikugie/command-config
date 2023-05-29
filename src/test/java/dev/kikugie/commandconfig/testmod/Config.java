package dev.kikugie.commandconfig.testmod;

import net.minecraft.util.StringIdentifiable;

import java.util.List;

public class Config {
    public boolean boolOpt;
    public int intOpt;
    public float floatOpt;
    public String stringOpt;
    public String quotedStringOpt;
    public String greedyStringOpt;
    public List<Integer> intListOpt;
    public ExampleEnum enumOpt;

    public Config(boolean boolOpt, int intOpt, float floatArg, String stringOpt, String quotedStringOpt, String greedyStringOpt, List<Integer> intListOpt, ExampleEnum enumOpt) {
        this.boolOpt = boolOpt;
        this.intOpt = intOpt;
        this.floatOpt = floatArg;
        this.stringOpt = stringOpt;
        this.quotedStringOpt = quotedStringOpt;
        this.greedyStringOpt = greedyStringOpt;
        this.intListOpt = intListOpt;
        this.enumOpt = enumOpt;
    }

    public void save() {
    }

    enum ExampleEnum implements StringIdentifiable {
        A("a"),
        B("b"),
        C("c");
        private final String name;

        ExampleEnum(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
