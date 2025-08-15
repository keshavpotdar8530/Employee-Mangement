import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;                   // Unique ID
    private String name;
    private String department;
    private String designation;
    private double salary;
    private LocalDate dateOfJoining;  // Optional but useful

    public Employee(int id, String name, String department, String designation, double salary, LocalDate doj) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.dateOfJoining = doj != null ? doj : LocalDate.now();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    @Override
    public String toString() {
        return String.format(
            "ID: %-4d | Name: %-20s | Dept: %-12s | Role: %-15s | Salary: %-10.2f | DOJ: %s",
            id, name, department, designation, salary,
            dateOfJoining != null ? dateOfJoining.format(DateTimeFormatter.ISO_DATE) : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

