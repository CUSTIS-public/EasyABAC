package custis.easyabac.demo.authz.rbac;

import custis.easyabac.demo.model.BranchId;

public enum Role {
    ROLE_MANAGER, ROLE_OPERATOR, ROLE_USER;

    public String ofBranch(BranchId branchId) {
        return this.name() + "_" + branchId.toString();
    }
}
