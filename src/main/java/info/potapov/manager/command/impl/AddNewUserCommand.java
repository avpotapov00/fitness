package info.potapov.manager.command.impl;

import info.potapov.common.command.Command;
import info.potapov.common.command.CommandDao;
import info.potapov.manager.command.dao.CommandDaoImpl;

public class AddNewUserCommand implements Command {
    public String process(CommandDao commandDao) {
        return ((CommandDaoImpl) commandDao).addNewUser();
    }
}
