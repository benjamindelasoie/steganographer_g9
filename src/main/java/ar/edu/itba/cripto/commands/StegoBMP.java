package ar.edu.itba.cripto.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "stegobmp", synopsisSubcommandLabel = "(-embed | -extract)", version = "1.0",
        subcommands = {EmbedCommand.class, ExtractCommand.class},
        description = "Steganography functionalities with optional encryption.")
public class StegoBMP {
    public static void main(String[] args) {
        System.exit(new CommandLine(new StegoBMP()).execute(args));
    }
}
