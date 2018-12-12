package jp.co.mo.mysns;

public enum TweetSearchType {
    MY_FOLLOWING(1),
    ONE_PERSON(2),
    SEARCH_IN(3),
    OTHER(4);

    private int typeId;

    TweetSearchType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public static TweetSearchType getByTypeId(int typeId) {
        for(TweetSearchType t : TweetSearchType.values()) {
            if(typeId == t.getTypeId()) {
                return t;
            }
        }
        return null;
    }
}
