public class DepartmentDemo {
    public static void main(String[] args) {
        DepartmentRepository repository = new DepartmentRepository();

        DepartmentService service = new DepartmentService(repository);

        service.addDepartment(new Department(1, "coco"));
        service.addDepartment(new Department(2, "jay"));

        System.out.println(service.getDepartmentById(1));

        try {
            service.addDepartment(new Department(1, "TT"));
        } catch (Exception e) {
            System.out.println("error: "+ e.getMessage());
        }

        service.renameDepartment(1, "newName");

        System.out.println(service.getDepartmentById(1));

        service.removeDepartment(2);

        try {
            service.removeDepartment(99);
        } catch (Exception e) {
            System.out.println("remove error" + e.getMessage());
        }
    }
}