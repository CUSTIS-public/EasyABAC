package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;

@AuthorizationEntity(name = "Report")
public class Report {

    /**
     * Authorization attribute "код отчета"
     */
    @AuthorizationAttribute(id = "id")
    private Integer id;

    /**
     * Authorization attribute "категория доступа отчета"
     */
    @AuthorizationAttribute(id = "category")
    private String category;

    public Report() {
    }

    public Report(Integer id, String category) {
        this.id = id;
        this.category = category;
    }

    // Simple getters and setters
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
