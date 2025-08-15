import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    private final EmployeeRepository repo;
    private final List<Employee> employees;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
        this.employees = new ArrayList<>(repo.loadAll());
    }

    public List<Employee> getAll() {
        return Collections.unmodifiableList(employees);
    }

    public Optional<Employee> getById(int id) {
        return employees.stream().filter(e -> e.getId() == id).findFirst();
    }

    public List<Employee> searchByName(String namePart) {
        String q = namePart.toLowerCase();
        return employees.stream()
                .filter(e -> e.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public int getNextId() {
        return employees.stream().mapToInt(Employee::getId).max().orElse(0) + 1;
    }

    public Employee addEmployee(String name, String department, String designation, double salary, LocalDate doj) {
        validateName(name);
        validateNonNegative(salary, "Salary");

        int id = getNextId();
        Employee e = new Employee(id, name.trim(), nvl(department), nvl(designation), salary, doj);
        employees.add(e);
        persist();
        return e;
    }

    public boolean updateEmployee(int id, String name, String department, String designation, Double salary, LocalDate doj) {
        Optional<Employee> opt = getById(id);
        if (opt.isEmpty()) return false;

        Employee e = opt.get();
        if (name != null) {
            validateName(name);
            e.setName(name.trim());
        }
        if (department != null) e.setDepartment(nvl(department));
        if (designation != null) e.setDesignation(nvl(designation));
        if (salary != null) {
            validateNonNegative(salary, "Salary");
            e.setSalary(salary);
        }
        if (doj != null) e.setDateOfJoining(doj);

        persist();
        return true;
    }

    public boolean deleteEmployee(int id) {
        boolean removed = employees.removeIf(e -> e.getId() == id);
        if (removed) persist();
        return removed;
    }

    public List<Employee> sortBy(String field) {
        List<Employee> copy = new ArrayList<>(employees);
        switch (field.toLowerCase()) {
            case "name": copy.sort(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)); break;
            case "department": copy.sort(Comparator.comparing(Employee::getDepartment, String.CASE_INSENSITIVE_ORDER)); break;
            case "designation": copy.sort(Comparator.comparing(Employee::getDesignation, String.CASE_INSENSITIVE_ORDER)); break;
            case "salary": copy.sort(Comparator.comparingDouble(Employee::getSalary)); break;
            case "doj":
            case "dateofjoining": copy.sort(Comparator.comparing(Employee::getDateOfJoining)); break;
            default: copy.sort(Comparator.comparingInt(Employee::getId)); // id
        }
        return copy;
    }

    private void persist() {
        repo.saveAll(employees);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters.");
        }
    }

    private static void validateNonNegative(double value, String field) {
        if (value < 0) throw new IllegalArgumentException(field + " cannot be negative.");
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s.trim();
    }
}
