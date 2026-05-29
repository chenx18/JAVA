public class DepartmentDemo {
    public static void main(String[] args) {
        DepartmentRepository repository = new DepartmentRepository();
        DepartmentService service = new DepartmentService(repository);

        service.addDepartment(new Department(1, "er"));
        service.addDepartment(new Department(2, "RGE"));

        System.out.println(service.getDepartmentById(2));

        try {
            System.out.println(service.getDepartmentById(123));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}