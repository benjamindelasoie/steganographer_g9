package ar.edu.itba.cripto.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "stegobmp", synopsisSubcommandLabel = "(-embed | -extract)", version = "1.0",
    subcommands = {EmbedCommand.class, ExtractCommand.class}, mixinStandardHelpOptions = true,
    sortOptions = false, sortSynopsis = false, usageHelpAutoWidth = true,
    footer = {"Grupo 9 - Criptografía y Seguridad - 1C 2024"},
    description = "Steganography functionalities with optional encryption.")
public final class StegoBMP {
    private StegoBMP() {}

    public static void main(final String[] args) {
        System.exit(new CommandLine(new StegoBMP()).execute(args));
    }
}
