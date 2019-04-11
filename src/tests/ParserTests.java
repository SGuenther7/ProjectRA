package tests;

import Model.Command;
import Model.Instruction;
import Model.Parser;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTests {

    @Test
    void convertTest() {
        // Convertiere Hex codes in Command mit korrektem Command und Wert(en)

        // Erstelle benoetigte variablen
        String raw = "0000 3011           00017           movlw 11h           ;in W steht nun 11h, Statusreg. unveraendert";

        // Convertiere von String zu Command
        Command result = Parser.parse(Parser.cut(raw));

        // Check ob resultat korrekt ist
        assertEquals(result, new Command(Instruction.MOVLW, new int[]{0x11}));
    }
}
