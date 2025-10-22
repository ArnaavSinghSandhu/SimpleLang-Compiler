package Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GeneratorToFile {

    public static void writeGeneratedCodeToFile(generator gen, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Iterate over each function in the generator's map
            for (HashMap.Entry<functiontype, StringBuffer> entry : gen.map.entrySet()) {
                functiontype func = entry.getKey();
                StringBuffer code = entry.getValue();

                // Write function header
                writer.write("=== Function: " + (func.value != null ? func.value : "") + " ===\n");
                writer.write(code.toString());
                writer.write("\n========================\n\n");
            }

            // Optionally, write symbol tables
            for (HashMap.Entry<functiontype, HashMap<String, String>> e : gen.symbolTable.entrySet()) {
                functiontype func = e.getKey();
                HashMap<String, String> symbols = e.getValue();
                writer.write("Symbol Table for function: " + func.value + "\n");
                for (String var : symbols.keySet()) {
                    writer.write("  " + var + " -> " + symbols.get(var) + "\n");
                }
                writer.write("\n");
            }

            System.out.println("Generated code written to " + filename);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}