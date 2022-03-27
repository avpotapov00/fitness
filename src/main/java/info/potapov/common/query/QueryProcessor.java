package info.potapov.common.query;

public record QueryProcessor(QueryDao queryDao) {

    public String process(Query query) {
        try {
            return query.process(queryDao);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
