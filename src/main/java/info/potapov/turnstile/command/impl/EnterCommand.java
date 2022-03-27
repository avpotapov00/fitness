package info.potapov.turnstile.command.impl;

import info.potapov.common.command.Command;
import info.potapov.common.command.CommandDao;
import info.potapov.turnstile.command.dao.CommandDaoImpl;

public record EnterCommand(Long uid, Long timestamp) implements Command {

    @Override
    public String process(CommandDao commandDao) {
        return ((CommandDaoImpl) commandDao).enter(uid, timestamp);
    }
}
