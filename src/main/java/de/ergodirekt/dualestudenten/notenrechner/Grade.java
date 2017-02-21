package de.ergodirekt.dualestudenten.notenrechner;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.xml.bind.annotation.*;

@XmlType(name="attainment", propOrder = {"fullName", "shortName", "grade", "ects", "semester", "load"})
@XmlAccessorType (XmlAccessType.PROPERTY)
@XmlRootElement (name="attainment")
public class Grade {
    private SimpleStringProperty fullName;
    private SimpleStringProperty shortName;
    private SimpleDoubleProperty grade;
    private SimpleDoubleProperty ects;
    private SimpleIntegerProperty semester;
    private SimpleDoubleProperty load;


    public Grade() {
        this.fullName = new SimpleStringProperty("test");
        this.shortName = new SimpleStringProperty("");
        this.grade = new SimpleDoubleProperty(0.0);
        this.ects = new SimpleDoubleProperty(0.0);
        this.semester = new SimpleIntegerProperty(0);
        this.load = new SimpleDoubleProperty(0.0);
    }

    public Grade(String fullName, String shortName, double grade, double ects, int semester, double load) {
        this.fullName = new SimpleStringProperty(fullName);
        this.shortName = new SimpleStringProperty(shortName);
        this.grade = new SimpleDoubleProperty(grade);
        this.ects = new SimpleDoubleProperty(ects);
        this.semester = new SimpleIntegerProperty(semester);
        this.load = new SimpleDoubleProperty(load);
    }

    public Grade(String fullName, String shortName, double grade, double ects, int semester) {
        this(fullName, shortName, grade, ects, semester, computeLoad(semester, ects));
    }

    private static double computeLoad(int semester, double ects) {
        if (semester == 1 || semester == 2) {
            return ects / 2.0;
        }
        return ects;
    }

    @XmlElement (name="shortName")
    public String getShortName() {
        return shortName.get();
    }


    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    @XmlElement(name="fullName")
    public String getFullName() {
        return fullName.get();
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    @XmlElement(name="ects")
    public double getEcts() {
        return ects.get();
    }

    public void setEcts(double ects) {
        this.ects.set(ects);
    }

    @XmlElement(name="semester")
    public int getSemester() {
        return semester.get();
    }

    public void setSemester(int semester) {
        this.semester.set(semester);
    }

    @XmlElement(name="load")
    public double getLoad() {
        return load.get();
    }

    public void setLoad(double load) {
        this.load.set(load);
    }

    @XmlElement(name="grade")
    public double getGrade() {
        return grade.get();
    }

    public void setGrade(double grade) {
        this.grade.set(grade);
    }

    @Override
    public String toString() {
        return String.format("[Grade: [fullName: %s, shortName: %s, grade: %f, ects: %f, semester: %d, load: %f]", getFullName(), getShortName(), getGrade(), getEcts(), getSemester(), getLoad());
    }
}
