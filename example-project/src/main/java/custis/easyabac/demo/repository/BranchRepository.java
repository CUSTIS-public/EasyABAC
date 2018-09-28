package custis.easyabac.demo.repository;

import custis.easyabac.demo.model.Branch;
import custis.easyabac.demo.model.BranchId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface BranchRepository extends CrudRepository<Branch, BranchId> {

    List<Branch> findBranchById(BranchId branchId);


    void deleteAllByNameLike(String name);
}
