public class Department {
    private int id;
    private String name;
    
    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Department{id=}" + id;
    }
}