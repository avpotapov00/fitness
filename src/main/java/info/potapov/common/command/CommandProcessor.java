package info.potapov.common.command;

public record CommandProcessor(CommandDao commandDao) {

    public String process(Command command) {
        try {
            return command.process(commandDao);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
