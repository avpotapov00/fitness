package info.potapov.common.command;

public interface Command {
    String process(CommandDao commandDao);
}
