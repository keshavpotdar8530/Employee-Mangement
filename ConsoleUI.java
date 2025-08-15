import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private static final String DATA_FILE = "data/employees.dat";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        EmployeeRepository repo = new EmployeeRepository(DATA_FILE);
        EmployeeService service = new EmployeeService(repo);
        Scanner sc = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("         Employee Management System");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt(sc, "Enter choice: ");
            try {
                switch (choice) {
                    case 1: addEmployeeFlow(sc, service); break;
                    case 2: viewAllFlow(service); break;
                    case 3: updateEmployeeFlow(sc, service); break;
                    case 4: deleteEmployeeFlow(sc, service); break;
                    case 5: searchEmployeeFlow(sc, service); break;
                    case 6: sortFlow(sc, service); break;
                    case 0: running = false; System.out.println("Exiting... Bye!"); break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Validation Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected Error: " + ex.getMessage());
            }
            System.out.println();
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add New Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Search Employee (by ID or Name)");
        System.out.println("6. Sort Employees");
        System.out.println("0. Exit");
    }

    // === Flows ===
    private static void addEmployeeFlow(Scanner sc, EmployeeService service) {
        System.out.println("\n-- Add New Employee --");
        String name = readNonEmpty(sc, "Name: ");
        String dept = readOptional(sc, "Department (optional): ");
        String role = readOptional(sc, "Designation (optional): ");
        double salary = readDouble(sc, "Salary: ");
        LocalDate doj = readDateOptional(sc, "Date of Joining (yyyy-MM-dd, Enter to use today): ");

        Employee e = service.addEmployee(name, dept, role, salary, doj);
        System.out.println("Added successfully:\n" + e);
    }

    private static void viewAllFlow(EmployeeService service) {
        System.out.println("\n-- All Employees --");
        List<Employee> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("(no records)");
        } else {
            list.forEach(System.out::println);
            System.out.println("Total: " + list.size());
        }
    }

    private static void updateEmployeeFlow(Scanner sc, EmployeeService service) {
        System.out.println("\n-- Update Employee --");
        int id = readInt(sc, "Enter Employee ID: ");
        Optional<Employee> existing = service.getById(id);
        if (existing.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }
        System.out.println("Current: " + existing.get());

        String name = readMaybe(sc, "New Name (leave blank to keep): ");
        String dept = readMaybe(sc, "New Department (blank to keep): ");
        String role = readMaybe(sc, "New Designation (blank to keep): ");
        String salStr = readMaybe(sc, "New Salary (blank to keep): ");
        String dojStr = readMaybe(sc, "New DOJ yyyy-MM-dd (blank to keep): ");

        Double salary = null;
        if (!salStr.isBlank()) {
            try { salary = Double.parseDouble(salStr); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Salary must be a number."); }
        }

        LocalDate doj = null;
        if (!dojStr.isBlank()) {
            try { doj = LocalDate.parse(dojStr, DTF); }
            catch (DateTimeParseException e) { throw new IllegalArgumentException("DOJ must be yyyy-MM-dd."); }
        }

        boolean ok = service.updateEmployee(id,
                name.isBlank() ? null : name,
                dept.isBlank() ? null : dept,
                role.isBlank() ? null : role,
                salary,
                doj);

        System.out.println(ok ? "Updated successfully." : "Update failed.");
        service.getById(id).ifPresent(emp -> System.out.println("Now: " + emp));
    }

    private static void deleteEmployeeFlow(Scanner sc, EmployeeService service) {
        System.out.println("\n-- Delete Employee --");
        int id = readInt(sc, "Enter Employee ID: ");
        boolean ok = service.deleteEmployee(id);
        System.out.println(ok ? "Deleted." : "Employee not found.");
    }

    private static void searchEmployeeFlow(Scanner sc, EmployeeService service) {
        System.out.println("\n-- Search Employee --");
        System.out.println("1) By ID  2) By Name");
        int c = readInt(sc, "Choose: ");
        if (c == 1) {
            int id = readInt(sc, "Enter ID: ");
            service.getById(id).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Employee not found.")
            );
        } else if (c == 2) {
            String q = readNonEmpty(sc, "Enter name (partial allowed): ");
            List<Employee> res = service.searchByName(q);
            if (res.isEmpty()) System.out.println("No matches.");
            else res.forEach(System.out::println);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void sortFlow(Scanner sc, EmployeeService service) {
        System.out.println("\n-- Sort Employees --");
        System.out.println("Fields: id, name, department, designation, salary, doj");
        String field = readNonEmpty(sc, "Sort by: ");
        List<Employee> sorted = service.sortBy(field);
        sorted.forEach(System.out::println);
    }

    // === Input Helpers ===
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid integer."); }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    private static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readOptional(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String readMaybe(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static LocalDate readDateOptional(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, DTF);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format.");
        }
    }
}
