package ar.edu.itba.cripto;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "stegobmp", synopsisSubcommandLabel = "(-embed | -extract)", version = "1.0",
        subcommands = {EmbedCommand.class, ExtractCommand.class}, description = "Steganography functionalities with optional encryption.")
public class Main {

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
