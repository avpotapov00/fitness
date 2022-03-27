package info.potapov.manager.command.impl;

import java.sql.Date;

import info.potapov.common.command.Command;
import info.potapov.common.command.CommandDao;
import info.potapov.manager.command.dao.CommandDaoImpl;

public class ExtendMembershipCommand implements Command {
    private final Long uid;
    private final Date expiryDate;

    public ExtendMembershipCommand(Long uid, Date expiryDate) {
        this.uid = uid;
        this.expiryDate = expiryDate;
    }

    @Override
    public String process(CommandDao commandDao) {
        return ((CommandDaoImpl)commandDao).extendSubscription(uid, expiryDate);
    }
}
