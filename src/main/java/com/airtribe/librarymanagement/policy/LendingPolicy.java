package com.airtribe.librarymanagement.policy;

import com.airtribe.librarymanagement.util.ValidationUtils;

public final class LendingPolicy {
    private final int maxLoansPerPatron;
    private final int loanPeriodDays;

    public LendingPolicy(int maxLoansPerPatron, int loanPeriodDays) {
        this.maxLoansPerPatron = ValidationUtils.requirePositive(maxLoansPerPatron, "Max loans per patron");
        this.loanPeriodDays = ValidationUtils.requirePositive(loanPeriodDays, "Loan period days");
    }

    public int getMaxLoansPerPatron() {
        return maxLoansPerPatron;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }
}


