package online.umedgroup.ug_inventory_management.models;

import jakarta.persistence.*;

import java.util.List;

/*
 * Template Entity
 * Template acts as the blueprint for table.
 * IMP: Creating dynamic tables as per client request is not optimal and secure,
 *      instead we create data pool and based on the template we retrieve the data.
 *
 * To create any table we create corresponding template of that table.
 * Template have corresponding template fields which acts as table columns.
 */
@Entity
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String templateName;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private List<TemplateField> fields;

    public Template() {
    }

    public Template(String templateName) {
        this.templateName = templateName;
    }

    public Long getId() { return id; }

    public String getTemplateName() { return templateName; }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    // toString
    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", fields=" + fields +
                '}';
    }
}