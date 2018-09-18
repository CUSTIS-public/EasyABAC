package ru.custis.easyabac.core;

public enum Decision {
    PERMIT(0), DENY(1), INDETERMINATE(2), NOT_APPLICABLE(3);

    private final int index;

    Decision(int index) {
        this.index = index;
    }

    public static Decision getByIndex(int index) {
        for (Decision decision : Decision.values()) {
            if (decision.index == index) {
                return decision;
            }
        }
        return DENY;
    }

}
