package Model;

public class Port {
    /*
    * Agiert als zwischenspeicher (Buffer) für Eingang und Ausgang
    * Bei einem 1 Bit in TRIS wird der Inhalt auf PortA/B
    * uebertragen. Bei weichsel auf 0 wird der Inhalt in den Buffer
    * fuer die Ausgabe gelegt.
    */

    /*
     * Wenn ich kein write habe und write, dann wrid es erst übernommen wenn ich ein write bekomme
     *
     * Lesen ? Immer.
     * Schreiben ohne TRIS -> Zwischenbuffer
     * Schreiben mit TRIS -> Direkt in Port
     *
     */

    private int internalRegister = 0 ; // Internet Buffer

    public void set(int value) {
        internalRegister = value;
    }

    public int get() {
        return internalRegister;
    }
}
