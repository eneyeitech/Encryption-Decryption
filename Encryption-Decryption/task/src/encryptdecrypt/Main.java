package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        String mode = "enc";
        String algorithm = "shift";
        int key = 0;
        StringBuilder data = new StringBuilder("");

        Algorithm dataConversionAlgorithm = null;

        boolean inIsProvided = false;
        boolean outIsProvided = false;

        String inputFilePath = "";
        String outputFilePath = "";

        for (int i = 0; i < args.length; i++) {

            if (args[i].charAt(0) == '-' && i == args.length - 1 ||
                    args[i].charAt(0) == '-' && args[i + 1].charAt(0) == '-') {

                System.out.println("Error in the arguments.");
                return;
            }

            switch (args[i]) {

                case "-mode" :
                    mode = args[i + 1];
                    i += 1;
                    break;

                case "-key" :
                    key = Integer.valueOf(args[i + 1]);
                    i += 1;
                    break;

                case "-data" :
                    data.append(args[i + 1]);
                    i += 1;
                    break;

                case "-in" :
                    inIsProvided = true;
                    inputFilePath = args[i + 1];
                    i += 1;
                    break;

                case "-out" :
                    outIsProvided = true;
                    outputFilePath = args[i + 1];
                    i += 1;
                    break;

                case "-alg" :
                    algorithm = args[i + 1];
                    i += 1;
                    break;
            }
        }

        try {
            data = inIsProvided ? new StringBuilder(new String(Files.readAllBytes(Paths.get(inputFilePath)))) : data;
        } catch (IOException e) {
            System.out.println("IOException occurred while inputting.");
        }

        if ("enc".equals(mode)) {

            if ("shift".equals(algorithm)) {
                dataConversionAlgorithm = new EncryptionShift();
            } else if ("unicode".equals(algorithm)) {
                dataConversionAlgorithm = new EncryptionUnicode();
            }
        } else if ("dec".equals(mode)) {

            if ("shift".equals(algorithm)) {
                dataConversionAlgorithm = new DecryptionShift();
            } else if ("unicode".equals(algorithm)) {
                dataConversionAlgorithm = new DecryptionUnicode();
            }
        }

        data = dataConversionAlgorithm.execute(data, key);

        if (outIsProvided) {

            try (FileWriter fileWriter = new FileWriter(new File(outputFilePath))) {

                fileWriter.write(data.toString());
            } catch (IOException e) {
                System.out.println("IOException occurred while outputting.");
            }
        } else {
            System.out.println(data);
        }
    }
}

interface Algorithm {
    StringBuilder execute(StringBuilder data, int key);
}

class DecryptionShift implements Algorithm {

    @Override
    public StringBuilder execute(StringBuilder data, int key) {

        for (int i = 0; i < data.length(); i++) {

            char c = data.charAt(i);

            if (data.charAt(i) >= 'A' && data.charAt(i) <= 'Z') {

                c = (char) (c - key);
                if (c < 'A') {
                    char temp = (char) ('A' - c);
                    c = (char) (91 - temp);
                }
            } else if (data.charAt(i) >= 'a' && data.charAt(i) <= 'z') {

                c = (char) (c - key);
                if (c < 'a') {
                    char temp = (char) ('a' - c);
                    c = (char) (123 - temp);
                }
            }
            data.replace(i, i+1, String.valueOf(c));
        }
        return data;
    }
}

class DecryptionUnicode implements Algorithm {

    @Override
    public StringBuilder execute(StringBuilder data, int key) {

        for (int i = 0; i < data.length(); i++) {

            char c = (char) (data.charAt(i) - key);
            data.replace(i, i + 1, String.valueOf(c));
        }
        return data;
    }
}

class EncryptionShift implements Algorithm {

    @Override
    public StringBuilder execute(StringBuilder data, int key) {

        for (int i = 0; i < data.length(); i++) {

            char c = data.charAt(i);

            if (data.charAt(i) >= 'A' && data.charAt(i) <= 'Z') {

                c = (char) (c + key);
                if (c > 'Z') {
                    c = (char) (c - 90 + 64);
                }
            } else if (data.charAt(i) >= 'a' && data.charAt(i) <= 'z') {

                c = (char) (c + key);
                if (c > 'z') {
                    c = (char) (c - 122 + 96);
                }
            }
            data.replace(i, i+1, String.valueOf(c));
        }
        return data;
    }
}

class EncryptionUnicode implements Algorithm {

    @Override
    public StringBuilder execute(StringBuilder data, int key) {

        for (int i = 0; i < data.length(); i++) {

            char c = (char) (data.charAt(i) + key);
            data.replace(i, i + 1, String.valueOf(c));
        }
        return data;
    }
}