package info.potapov.report.query.queries;

import java.time.LocalDate;

import info.potapov.common.query.Query;
import info.potapov.common.query.QueryDao;
import info.potapov.report.query.QueryDaoImpl;

public class GetStatsQuery implements Query {
    LocalDate from;
    LocalDate to;

    public GetStatsQuery(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String process(QueryDao queryDao) throws Exception {
        return ((QueryDaoImpl)queryDao).getReport(from, to);
    }
}
