package de.ergodirekt.dualestudenten.notenrechner;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name="gradecollection")
@XmlType (propOrder = {"name", "gradelist"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class GradeCollection {

    private String name;
    private List<Grade> gradelist;

    @XmlElement (name="name")
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="attainment")
    @XmlElementWrapper(name="gradelist")
    public List<Grade> getGradelist() {
        return gradelist;
    }


    void setGradelist(List<Grade> gradelist) {
        this.gradelist = gradelist;
    }
}
