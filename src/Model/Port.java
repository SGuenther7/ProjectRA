package Model;

public class Port {
    /*
    * Agiert als zwischenspeicher (Buffer) für Eingang und Ausgang
    * Bei einem 1 Bit in TRIS wird der Inhalt auf PortA/B
    * uebertragen. Bei weichsel auf 0 wird der Inhalt in den Buffer
    * fuer die Ausgabe gelegt.
    */

    private int internalRegister = 0 ; // Internet Buffer
    private int lastState = 0; // Anzahl von Bits bei letztem update()

    private int bank = 0; // TODO: Ueberfluessig ?
    private int index = 0; // Index von Port Register in FSR

    private Worker peon;

    public Port(Worker peon) {
       this.peon = peon;
    }

    public Port(Port other) {
        this.internalRegister = other.internalRegister;
        this.lastState = other.lastState;
        this.bank = other.bank;
        this.index = other.bank;
        this.peon = other.peon;
    }

    public void update() {

        if(peon.getMemory().content()[bank][index] != lastState) {
            // Handle
        }

    }

    public int read() {
        return internalRegister;
    }

    public void write(int value) {
        // TODO: if(TRIS == 1) peon.getMemory().content[0][index] = value;
        internalRegister = value;
    }
}
