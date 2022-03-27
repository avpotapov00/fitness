package info.potapov.manager.query.queries;

import info.potapov.common.query.Query;
import info.potapov.common.query.QueryDao;
import info.potapov.manager.query.QueryDaoImpl;

public record GetMembershipInfoQuery(Long uid) implements Query {

    @Override
    public String process(QueryDao queryDao) throws Exception {
        return ((QueryDaoImpl) queryDao).getSubscriptionInfo(uid);
    }
}
